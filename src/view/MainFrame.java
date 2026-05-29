/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    public JComboBox<String> comboKecamatan;
    public JButton btnCekCuaca;
    public JLabel lblSuhu, lblAngin, lblHujan, lblStatus, lblMap;
    public JTextArea txtPeringatan;
    public JPanel panelAlert;

    // Palet Warna UI Modern (Cyberpunk Dark Mode)
    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color SURFACE_DARK = new Color(30, 30, 30);
    private final Color TEXT_WHITE = new Color(245, 245, 245);
    private final Color ACCENT_CYAN = new Color(0, 229, 255);

    public MainFrame() {
        setTitle("Sleman Eco-Weather & Agro-Alert System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(15, 15));
        setSize(880, 460);

        // --- PANEL ATAS ---
        JPanel panelAtas = new JPanel();
        panelAtas.setBackground(SURFACE_DARK);
        panelAtas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblPilih = new JLabel("TARGET MONITORING: ");
        lblPilih.setForeground(TEXT_WHITE);
        lblPilih.setFont(new Font("SansSerif", Font.BOLD, 12));

        comboKecamatan = new JComboBox<>();
        comboKecamatan.setBackground(BG_DARK);
        comboKecamatan.setForeground(TEXT_WHITE);
        comboKecamatan.setFont(new Font("SansSerif", Font.PLAIN, 12));

        btnCekCuaca = new JButton("PINDAI RADAR 📡");
        btnCekCuaca.setBackground(ACCENT_CYAN);
        btnCekCuaca.setForeground(Color.BLACK);
        btnCekCuaca.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCekCuaca.setFocusPainted(false);

        panelAtas.add(lblPilih);
        panelAtas.add(comboKecamatan);
        panelAtas.add(btnCekCuaca);

        // --- PANEL TENGAH ---
        JPanel panelKontenUtama = new JPanel(new GridLayout(1, 2, 20, 0));
        panelKontenUtama.setOpaque(false);
        panelKontenUtama.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Sub-Panel Data Telemetri
        JPanel panelData = new JPanel(new GridLayout(4, 2, 10, 15));
        panelData.setOpaque(false);
        
        lblSuhu = createNeonLabel("-", ACCENT_CYAN, 24);
        lblAngin = createNeonLabel("-", TEXT_WHITE, 16);
        lblHujan = createNeonLabel("-", TEXT_WHITE, 16);
        lblStatus = createNeonLabel("STANDBY", Color.LIGHT_GRAY, 12);
        
        panelData.add(createFormLabel("🌡️ SUHU UDARA")); panelData.add(lblSuhu);
        panelData.add(createFormLabel("💨 KECEPATAN ANGIN")); panelData.add(lblAngin);
        panelData.add(createFormLabel("🌧️ INTENSITAS HUJAN")); panelData.add(lblHujan);
        panelData.add(createFormLabel("📡 TELEMETRI CORE")); panelData.add(lblStatus);

        // Sub-Panel Peta Satelit
        JPanel panelPetaVisual = new JPanel(new BorderLayout());
        panelPetaVisual.setBackground(SURFACE_DARK);
        
        TitledBorder borderPeta = BorderFactory.createTitledBorder("CITRA SATELIT GEOGRAFIS");
        borderPeta.setTitleColor(ACCENT_CYAN);
        borderPeta.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        panelPetaVisual.setBorder(borderPeta);
        
        lblMap = new JLabel("WEATHER MAP READY TO SCAN", SwingConstants.CENTER);
        lblMap.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblMap.setForeground(Color.GRAY);
        panelPetaVisual.add(lblMap, BorderLayout.CENTER);

        panelKontenUtama.add(panelData);
        panelKontenUtama.add(panelPetaVisual);

        // --- PANEL ALERT BAWAH (OPTIMIZED TERMINAL) ---
        panelAlert = new JPanel(new BorderLayout());
        panelAlert.setBackground(BG_DARK); // Disamakan dengan background dasar agar menyatu
        panelAlert.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)), // Garis pembatas yang smooth
            " AGRO-ALERT EMERGENCIES TERMINAL ", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Monospaced", Font.BOLD, 11), ACCENT_CYAN // Judul panel menggunakan Cyan menyala
        ));
        
        txtPeringatan = new JTextArea(4, 20);
        txtPeringatan.setEditable(false);
     txtPeringatan.setBackground(new Color(55, 55, 55));      // Hitam murni untuk kedalaman kontras
        txtPeringatan.setForeground(Color.WHITE);                    // ✨ Selesai! Sekarang warna teks menjadi PUTIH BERSIH
        txtPeringatan.setFont(new Font("Monospaced", Font.BOLD, 13)); // Menggunakan BOLD ukuran 13 agar teks tebal dan tegas
        txtPeringatan.setMargin(new Insets(10, 12, 10, 12));         // Jarak padding teks dalam terminal
        
        // Membungkus JTextArea ke JScrollPane dan membersihkan border bawaannya
        JScrollPane scrollPane = new JScrollPane(txtPeringatan);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        panelAlert.add(scrollPane, BorderLayout.CENTER);

        add(panelAtas, BorderLayout.NORTH);
        add(panelKontenUtama, BorderLayout.CENTER);
        add(panelAlert, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        return label;
    }

    private JLabel createNeonLabel(String text, Color color, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        return label;
    }
}