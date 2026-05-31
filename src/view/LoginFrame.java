/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author adityaseptemberiano
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // Palet Warna Cyberpunk
    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color SURFACE_DARK = new Color(30, 30, 30);
    private final Color TEXT_WHITE = new Color(245, 245, 245);
    private final Color ACCENT_CYAN = new Color(0, 229, 255);

    public LoginFrame() {
        setTitle("Sleman Eco-Weather - Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelUtama = new JPanel();
        panelUtama.setBackground(BG_DARK);
        panelUtama.setLayout(null);
        add(panelUtama);

        JLabel lblTitle = new JLabel("CORE SYSTEM ACCESS", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitle.setForeground(ACCENT_CYAN);
        lblTitle.setBounds(20, 20, 360, 30);
        panelUtama.add(lblTitle);

        JLabel lblUser = new JLabel("USERNAME:");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblUser.setForeground(Color.GRAY);
        lblUser.setBounds(40, 70, 100, 25);
        panelUtama.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBackground(SURFACE_DARK);
        txtUsername.setForeground(TEXT_WHITE);
        txtUsername.setCaretColor(TEXT_WHITE);
        txtUsername.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsername.setBounds(40, 95, 300, 30);
        panelUtama.add(txtUsername);

        JLabel lblPass = new JLabel("PASSWORD:");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPass.setForeground(Color.GRAY);
        lblPass.setBounds(40, 135, 100, 25);
        panelUtama.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBackground(SURFACE_DARK);
        txtPassword.setForeground(TEXT_WHITE);
        txtPassword.setCaretColor(TEXT_WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setBounds(40, 160, 300, 30);
        panelUtama.add(txtPassword);

        btnLogin = new JButton("AUTHENTICATE 🔓");
        btnLogin.setBackground(ACCENT_CYAN);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(40, 205, 300, 35);
        panelUtama.add(btnLogin);
    }

    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void tampilkanPesanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Authentication Failed", JOptionPane.ERROR_MESSAGE);
    }
    public javax.swing.JButton getBtnLogin() { return btnLogin; }
public javax.swing.JTextField getTxtUsername() { return txtUsername; }
public javax.swing.JPasswordField getTxtPassword() { return txtPassword; }
}