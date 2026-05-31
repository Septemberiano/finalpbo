/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author adityaseptemberiano
 */
import view.AdminFrame;
import view.LoginFrame;
import database.DbConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminController {
    private AdminFrame adminFrame;

    public AdminController(AdminFrame adminFrame) {
        this.adminFrame = adminFrame;
        
        // Muat data wilayah saat panel admin pertama kali terbuka
        loadDataKecamatan();

        // Listener Tombol CRUD berbasis Getter
        this.adminFrame.getBtnTambah().addActionListener(e -> prosesTambah());
        this.adminFrame.getBtnUbah().addActionListener(e -> prosesUbah());
        this.adminFrame.getBtnHapus().addActionListener(e -> prosesHapus());
        this.adminFrame.getBtnLogout().addActionListener(e -> prosesLogout());

        // Jalankan sinkronisasi klik baris tabel ke textfield formulir
        this.adminFrame.getTabelKecamatan().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && adminFrame.getTabelKecamatan().getSelectedRow() != -1) {
                int baris = adminFrame.getTabelKecamatan().getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) adminFrame.getTabelKecamatan().getModel();
                adminFrame.getTxtNamaKec().setText(model.getValueAt(baris, 1).toString());
                adminFrame.getTxtSektor().setText(model.getValueAt(baris, 2).toString());
                adminFrame.getTxtLat().setText(model.getValueAt(baris, 3).toString());
                adminFrame.getTxtLong().setText(model.getValueAt(baris, 4).toString());
            }
        });
    }

    public void loadDataKecamatan() {
        DefaultTableModel model = (DefaultTableModel) adminFrame.getTabelKecamatan().getModel();
        model.setRowCount(0); 
        String sql = "SELECT * FROM tb_kecamatan";
        
        try (Connection conn = DbConnection.getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_kecamatan"),
                    rs.getString("nama_kecamatan"),
                    rs.getString("sektor_utama"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude")
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(adminFrame, "Gagal memuat database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesTambah() {
        String nama = adminFrame.getTxtNamaKec().getText().trim();
        String sektor = adminFrame.getTxtSektor().getText().trim();
        String latStr = adminFrame.getTxtLat().getText().trim();
        String longStr = adminFrame.getTxtLong().getText().trim();

        if (nama.isEmpty() || sektor.isEmpty() || latStr.isEmpty() || longStr.isEmpty()) {
            JOptionPane.showMessageDialog(adminFrame, "Semua form input wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO tb_kecamatan (nama_kecamatan, sektor_utama, latitude, longitude) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, sektor);
            ps.setDouble(3, Double.parseDouble(latStr));
            ps.setDouble(4, Double.parseDouble(longStr));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(adminFrame, "Data Wilayah Sukses Ditambahkan!");
            bersihkanForm();
            loadDataKecamatan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(adminFrame, "Gagal simpan: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesUbah() {
        int baris = adminFrame.getTabelKecamatan().getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(adminFrame, "Pilih baris tabel dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) adminFrame.getTabelKecamatan().getModel();
        int id = (int) model.getValueAt(baris, 0);

        String sql = "UPDATE tb_kecamatan SET nama_kecamatan=?, sektor_utama=?, latitude=?, longitude=? WHERE id_kecamatan=?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminFrame.getTxtNamaKec().getText().trim());
            ps.setString(2, adminFrame.getTxtSektor().getText().trim());
            ps.setDouble(3, Double.parseDouble(adminFrame.getTxtLat().getText().trim()));
            ps.setDouble(4, Double.parseDouble(adminFrame.getTxtLong().getText().trim()));
            ps.setInt(5, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(adminFrame, "Data Wilayah Berhasil Diperbarui!");
            bersihkanForm();
            loadDataKecamatan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(adminFrame, "Gagal ubah data: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesHapus() {
        int baris = adminFrame.getTabelKecamatan().getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(adminFrame, "Pilih data yang mau dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) adminFrame.getTabelKecamatan().getModel();
        int id = (int) model.getValueAt(baris, 0);
        int konfirmasi = JOptionPane.showConfirmDialog(adminFrame, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM tb_kecamatan WHERE id_kecamatan=?";
            try (Connection conn = DbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(adminFrame, "Data Sukses Dihapus!");
                bersihkanForm();
                loadDataKecamatan();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Gagal hapus: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void prosesLogout() {
        adminFrame.dispose();
        LoginFrame jendelaLogin = new LoginFrame();
        new LoginController(jendelaLogin);
        jendelaLogin.setVisible(true);
    }

    private void bersihkanForm() {
        adminFrame.getTxtNamaKec().setText("");
        adminFrame.getTxtSektor().setText("");
        adminFrame.getTxtLat().setText("");
        adminFrame.getTxtLong().setText("");
        adminFrame.getTabelKecamatan().clearSelection();
    }
}