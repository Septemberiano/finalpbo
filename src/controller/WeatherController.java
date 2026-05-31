package controller;

import view.MainFrame;
import database.DbConnection;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 * Controller Multi-Sektor: Integrasi Real-Time API Open-Meteo & Live Satellite Tile API
 * @author adityaseptemberiano
 */
public class WeatherController {
    private MainFrame mainFrame;

    public WeatherController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        loadComboKecamatan();
        
        this.mainFrame.btnCekCuaca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eksekusiPindaiRadar();
            }
        });
    }

    private void loadComboKecamatan() {
        mainFrame.comboKecamatan.removeAllItems();
        String sql = "SELECT nama_kecamatan FROM tb_kecamatan";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mainFrame.comboKecamatan.addItem(rs.getString("nama_kecamatan"));
            }
        } catch (SQLException ex) {
            mainFrame.comboKecamatan.addItem("Turi");
            mainFrame.comboKecamatan.addItem("Cangkringan");
            mainFrame.comboKecamatan.addItem("Berbah");
        }
    }

    private void eksekusiPindaiRadar() {
        String kecamatanTerpilih = (String) mainFrame.comboKecamatan.getSelectedItem();
        if (kecamatanTerpilih == null) return;

        // 1. AMBIL KOORDINAT ASLI DARI DATABASE
        String sektorUtama = "Agrikultur";
        double lat = -7.68;  
        double lon = 110.42;
        
        String sql = "SELECT sektor_utama, latitude, longitude FROM tb_kecamatan WHERE nama_kecamatan = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kecamatanTerpilih);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sektorUtama = rs.getString("sektor_utama");
                    lat = rs.getDouble("latitude");
                    lon = rs.getDouble("longitude");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Gagal baca koordinat DB, menggunakan nilai default.");
        }

        // Variabel penampung data cuaca
        double suhu = 0.0;
        double kecepatanAngin = 0.0;
        double intensitasHujan = 0.0;
        boolean menggunakanApiAsli = false;

        // 2. TEMBAK API 1: DATA TELEMETRI CUACA (OPEN-METEO)
        try {
            String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current=temperature_2m,wind_speed_10m,rain&timezone=Asia%2FBangkok";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000); 
            
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String barisTeks;
                while ((barisTeks = in.readLine()) != null) {
                    jsonResponse.append(barisTeks);
                }
                in.close();
                
                String rawJson = jsonResponse.toString();
                
                // 🔥 PERBAIKAN UTAMA: Isolasi pencarian hanya di dalam objek "current":{...}
                int indexCurrent = rawJson.indexOf("\"current\":");
                if (indexCurrent != -1) {
                    String jsonKhususAngka = rawJson.substring(indexCurrent);
                    
                    suhu = ambilNilaiJson(jsonKhususAngka, "temperature_2m");
                    kecepatanAngin = ambilNilaiJson(jsonKhususAngka, "wind_speed_10m");
                    intensitasHujan = ambilNilaiJson(jsonKhususAngka, "rain");
                    menggunakanApiAsli = true;
                }
            } else {
                throw new Exception("API Error");
            }
        } catch (Exception ex) {
            // Fallback System jika internet mendadak putus
            Random rand = new Random();
            suhu = 22.0 + (rand.nextDouble() * 5.5);       
            kecepatanAngin = 1.2 + (rand.nextDouble() * 4.0); 
            intensitasHujan = rand.nextDouble() > 0.75 ? (rand.nextDouble() * 5.0) : 0.0;
        }

        // UPDATE LABEL ANGKA DI DASHBOARD UI
        mainFrame.lblSuhu.setText(String.format("%.1f °C", suhu));
        mainFrame.lblAngin.setText(String.format("%.1f km/h", kecepatanAngin));
        mainFrame.lblHujan.setText(String.format("%.1f mm", intensitasHujan));
        mainFrame.lblStatus.setText(menggunakanApiAsli ? "LIVE API SINKRON" : "SIMULASI OFFLINE AKTIF");

        // 3. TEMBAK API 2: LIVE CITRA SATELIT BERDASARKAN KOORDINAT (YANDEX MAP ENGINE)
        memuatCitraSatelitAsli(lat, lon);

        // 4. SISTEM PAKAR AGRO-ALERT EMERGENCY LOGIC
        StringBuilder alertLog = new StringBuilder();
        alertLog.append(String.format("[SUMUNAR CORE v2.6]: %s MODE\n", menggunakanApiAsli ? "ONLINE" : "FALLBACK SECURITY"));
        alertLog.append(String.format("Wilayah Evaluasi: %s (Lat: %.4f, Lon: %.4f)\n", kecamatanTerpilih.toUpperCase(), lat, lon));
        alertLog.append(String.format("Sektor Utama    : %s\n", sektorUtama.toUpperCase()));
        alertLog.append("--------------------------------------------------\n");
        
        if (intensitasHujan > 1.0) {
            alertLog.append("Analisis Logika : Terdeteksi presipitasi air hujan aktif di area koordinat.\n");
            alertLog.append("Rekomendasi     : Amankan aset agrikultur rentan atau siapkan rencana evakuasi tenda festival lampion.");
        } else if (kecepatanAngin > 12.0) {
            alertLog.append("Analisis Logika : Kecepatan angin di atas ambang batas normal struktur ringan.\n");
            alertLog.append("Rekomendasi     : Dilarang menerbangkan lampion ke udara demi keselamatan penerbangan & cegah kebakaran.");
        } else {
            alertLog.append("Analisis Logika : Seluruh parameter atmosfer dalam kondisi batas aman operasional.\n");
            alertLog.append("Rekomendasi     : Kondisi ideal. Aktivitas pertanian dan pementasan festival dapat dilaksanakan.");
        }
        mainFrame.txtPeringatan.setText(alertLog.toString());
    }

    // Fungsi pengurai data JSON manual
    private double ambilNilaiJson(String json, String key) {
        try {
            int indexKey = json.indexOf("\"" + key + "\":");
            if (indexKey != -1) {
                int start = indexKey + key.length() + 3;
                int end = json.indexOf(",", start);
                if (end == -1 || end > json.indexOf("}", start)) {
                    end = json.indexOf("}", start);
                }
                return Double.parseDouble(json.substring(start, end).replace("}", "").trim());
            }
        } catch (Exception e) {
            System.out.println("Eror parsing: " + key);
        }
        return 0.0;
    }

    // Logika penarik gambar satelit murni dari API Geografis
    private void memuatCitraSatelitAsli(double lat, double lon) {
        try {
            int lebarKotak = mainFrame.lblMap.getWidth() > 0 ? mainFrame.lblMap.getWidth() : 355;
            int tinggiKotak = mainFrame.lblMap.getHeight() > 0 ? mainFrame.lblMap.getHeight() : 175;
            
            String urlApiSatelit = "https://static-maps.yandex.ru/1.x/?ll=" + lon + "," + lat + "&z=13&l=sat&size=450,300";
            
            URL url = new URL(urlApiSatelit);
            ImageIcon iconMentah = new ImageIcon(url);
            
            Image imgSkala = iconMentah.getImage().getScaledInstance(lebarKotak, tinggiKotak, Image.SCALE_SMOOTH);
            
            mainFrame.lblMap.setText(""); 
            mainFrame.lblMap.setIcon(new ImageIcon(imgSkala));
            
        } catch (Exception ex) {
            mainFrame.lblMap.setText("<html><center>⚠️ [ LINK SATELIT OFFLINE ]<br>Gagal melakukan rendering citra koordinat.</center></html>");
            mainFrame.lblMap.setIcon(null);
        }
    }
}