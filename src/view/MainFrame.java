/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import javax.swing.*;
import java.awt.*;
import controller.WeatherController;

public class MainFrame extends JFrame {
    // Mengekspos komponen visual agar dapat dikendalikan secara dinamis oleh Controller Layer
    public JComboBox<String> comboKecamatan;
    public JButton btnCekCuaca;
    public JLabel lblSuhu;
    public JLabel lblKecepatanAngin;
    public JLabel lblCurahHujan;
    public JLabel lblSektor;
    public JLabel lblMap;
    public JTextArea txtPeringatan;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sleman Eco-Weather & Agro-Alert System");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- LAPISAN PRESENTASI ATAS (INPUT PANEL) ---
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        panelInput.add(new JLabel("Pilih Wilayah Kecamatan: "));
        comboKecamatan = new JComboBox<>();
        comboKecamatan.setPreferredSize(new Dimension(180, 25));
        panelInput.add(comboKecamatan);

        btnCekCuaca = new JButton("Pindai Radar Cuaca");
        btnCekCuaca.setFont(new Font("SansSerif", Font.BOLD, 12));
        panelInput.add(btnCekCuaca);
        add(panelInput, BorderLayout.NORTH);

        // --- LAPISAN PRESENTASI TENGAH (UTAMA) ---
        JPanel panelUtama = new JPanel(new GridLayout(1, 2, 15, 15));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Sub-Panel Kiri: Informasi Telemetri & Agro-Alert Log
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Data Metrik Atmosferik"));

        lblSektor = new JLabel("Sektor Utama: -");
        lblSuhu = new JLabel("Suhu Udara: - °C");
        lblKecepatanAngin = new JLabel("Kecepatan Angin: - km/h");
        lblCurahHujan = new JLabel("Estimasi Presipitasi: - mm");

        Font fontMetrik = new Font("SansSerif", Font.BOLD, 14);
        lblSektor.setFont(fontMetrik);
        lblSuhu.setFont(fontMetrik);
        lblKecepatanAngin.setFont(fontMetrik);
        lblCurahHujan.setFont(fontMetrik);

        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblSektor);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblSuhu);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblKecepatanAngin);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblCurahHujan);
        panelInfo.add(Box.createVerticalStrut(20));

        panelInfo.add(new JLabel("Pusat Log Peringatan Dini Sistem Pakar:"));
        panelInfo.add(Box.createVerticalStrut(5));
        txtPeringatan = new JTextArea(8, 20);
        txtPeringatan.setEditable(false);
        txtPeringatan.setLineWrap(true);
        txtPeringatan.setWrapStyleWord(true);
        txtPeringatan.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(txtPeringatan);
        panelInfo.add(scrollLog);

        panelUtama.add(panelInfo);

        // Sub-Panel Kanan: Citra Satelit Geospasial
        JPanel panelMap = new JPanel(new BorderLayout());
        panelMap.setBorder(BorderFactory.createTitledBorder("Citra Topografi Satelit Terkini"));
        
        lblMap = new JLabel("Tekan 'Pindai Radar' untuk memuat citra satelit", SwingConstants.CENTER);
        lblMap.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelMap.add(lblMap, BorderLayout.CENTER);

        panelUtama.add(panelMap);
        add(panelUtama, BorderLayout.CENTER);

        // --- ARSITEKTUR INJEKSI OTOMATIS (CONTROLLER INJECTION) ---
        WeatherController controller = new WeatherController(this);
        controller.loadComboKecamatan(); // Sinkronisasi awal dengan database

        // Pemasangan Event Listener tunggal
        btnCekCuaca.addActionListener(e -> controller.eksekusiPindaiRadar());
    }

    public static void main(String[] args) {
        // Memaksa sistem mengikuti look and feel sistem operasi bawaan agar rapi
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}