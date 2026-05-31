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
import view.AdminFrame;
import view.MainFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(loginFrame, "Otorisasi Tingkat Tinggi Berhasil! Membuka Terminal Admin...");
            loginFrame.dispose(); 
            
            AdminFrame adminView = new AdminFrame();
            new AdminController(adminView);
            adminView.setVisible(true);

        } else if (username.equalsIgnoreCase("user") && password.equals("user123")) {
            JOptionPane.showMessageDialog(loginFrame, "Akses Publik Diizinkan! Membuka Dasbor Telemetri...");
            loginFrame.dispose();

            // 1. Buka View Layar Utama User Dashboard
            MainFrame userView = new MainFrame(username);
            
            // 2. SUNTIKKAN WEATHER CONTROLLER DI SINI AGAR SENSOR BERFUNGSI AKTIF ⚡
            new WeatherController(userView);

            // 3. Tampilkan UI ke Monitor Layar Laptop
            userView.setVisible(true);

        } else {
            loginFrame.tampilkanPesanError("Akses Sistem Ditolak! Kredensial tidak valid.");
        }
    }
}