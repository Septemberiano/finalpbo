/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.*;
import view.MainFrame;
import javax.swing.*;
import java.awt.Color;
import java.util.List;
/**
 *
 * @author adityaseptemberiano
 */
public class WeatherController {
    private MainFrame view;
    private WeatherRepository repository;

    public WeatherController(MainFrame view, WeatherRepository repository) {
        this.view = view;
        this.repository = repository;

        inisialisasiDropdownKecamatan();
        this.view.btnCekCuaca.addActionListener(e -> prosesDataCuaca());
    }

    private void inisialisasiDropdownKecamatan() {
        List<String> listKec = repository.ambilSemuaNamaKecamatan();
        view.comboKecamatan.removeAllItems();
        for (String kec : listKec) {
            view.comboKecamatan.addItem(kec);
        }
    }

    private void prosesDataCuaca() {
        Object selected = view.comboKecamatan.getSelectedItem();
        if (selected == null) return;

        String kecamatanDipilih = selected.toString();
        
        view.btnCekCuaca.setEnabled(false);
        view.lblStatus.setText("Scanning Radar...");
        view.lblMap.setIcon(null);
        view.lblMap.setText("Mengunduh Citra Satelit...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private KondisiUdara cuacaHasil;
            private ImageIcon petaHasil;

            @Override
            protected Void doInBackground() throws Exception {
                cuacaHasil = repository.dapatkanCuacaSektor(kecamatanDipilih);
                String status = (cuacaHasil != null) ? cuacaHasil.getDeskripsiCuaca() : "Normal";
                
                petaHasil = repository.unduhGambarPeta(kecamatanDipilih, status);
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (cuacaHasil != null) {
                        view.lblSuhu.setText(cuacaHasil.getSuhu() + " °C");
                        view.lblAngin.setText(cuacaHasil.getKecepatanAngin() + " km/h");
                        view.lblHujan.setText(cuacaHasil.getCurahHujan() + " mm");
                        view.txtPeringatan.setText(cuacaHasil.getPeringatanSektoral());

                        if (cuacaHasil instanceof CuacaEkstrem) {
                            view.panelAlert.setBackground(new Color(255, 23, 68)); 
                            view.txtPeringatan.setForeground(Color.WHITE);
                        } else {
                            view.panelAlert.setBackground(new Color(0, 230, 118)); 
                            view.txtPeringatan.setForeground(Color.BLACK);
                        }
                    }

                    if (petaHasil != null) {
                        view.lblMap.setText("");
                        view.lblMap.setIcon(petaHasil);
                    } else {
                        view.lblMap.setText("Gagal Sinkronisasi Peta Satelit.");
                    }

                    view.lblStatus.setText("SINKRONISASI SUKSES");
                } catch (Exception ex) {
                    view.txtPeringatan.setText("System Fault: Jalur komunikasi data terputus.");
                } finally {
                    view.btnCekCuaca.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
