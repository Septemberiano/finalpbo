/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author adityaseptemberiano
 */
import view.LoginFrame;
import view.MainFrame;
import database.DbConnection; // Pastikan package database sudah sesuai
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class LoginController {
    private LoginFrame loginFrame;

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        
        this.loginFrame.getBtnLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eksekusiLogin();
            }
        });
    }

    private void eksekusiLogin() {
        String username = loginFrame.getTxtUsername().getText();
        String password = new String(loginFrame.getTxtPassword().getPassword());

        // 1. Validasi Input Dasar
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            loginFrame.tampilkanPesanError("Username dan Password tidak boleh kosong!");
            return;
        }

        // 2. Query ke Database untuk Mencocokkan Kredensial & Mengambil Role
        String query = "SELECT role FROM tb_user WHERE username = ? AND password = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Ambil role yang terdaftar di database ('ADMIN', 'EO', atau 'AGRO')
                    String roleUserTerdeteksi = rs.getString("role");
                    
                    JOptionPane.showMessageDialog(loginFrame, "Otorisasi Berhasil! Login sebagai: " + roleUserTerdeteksi);
                    loginFrame.dispose(); 
                    
                    // 3. Alirkan ke MainFrame Dinamis Berbasis Role
                    // Sesuai arsitektur baru, semua role (termasuk ADMIN) menggunakan MainFrame yang sama
                    MainFrame dashboardUtama = new MainFrame(roleUserTerdeteksi);
                    dashboardUtama.setVisible(true);
                    
                } else {
                    // Jika username atau password tidak ditemukan di database
                    loginFrame.tampilkanPesanError("Akses Sistem Ditolak! Kredensial tidak valid.");
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(loginFrame, 
                "Gagal terhubung ke database lokasimu!\nError: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}