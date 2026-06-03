/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;
import controller.WeatherController;

public class MainFrame extends JFrame {
    // Komponen Global (Akses Public untuk Controller)
    public JComboBox<String> comboKecamatan;
    public JButton btnPindai;
    public JButton btnLogout; 
    public JLabel lblSuhu, lblHujan, lblAngin;
    public JTextArea txtRekomendasi;
    public MapPanel panelDenah;
    
    // Komponen Dinamis Berbasis Role
    public JPanel panelRoleSpesifik;
    public JComboBox<String> comboOpsiRole; 
    public JLabel lblRoleHeader;
    
    private String currentRole;
    private int idSektor; // Tambahan field untuk integrasi database relasional

    public MainFrame(String role) {
        this.currentRole = role;
        
        // Otomatis memetakan string Role login ke ID Sektor database
        if ("EO".equalsIgnoreCase(role)) {
            this.idSektor = 1;
        } else if ("AGRO".equalsIgnoreCase(role)) {
            this.idSektor = 2;
        } else {
            this.idSektor = 1; // Default fallback atau untuk ADMIN
        }
        
        initComponent();
        
        // Inisialisasi Controller secara Mandiri
        WeatherController controller = new WeatherController(this);
        controller.loadComboKecamatan();
    }

    private void initComponent() {
        setTitle("Sleman Eco-Weather & Agro-Alert Dashboard [" + currentRole + "]");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL UTARA: Kontrol Utama + Tombol Logout ---
        JPanel panelUtara = new JPanel(new BorderLayout(10, 10));
        panelUtara.setBackground(new Color(44, 62, 80));
        panelUtara.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel panelKontrolKiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelKontrolKiri.setOpaque(false);
        
        JLabel lblPilih = new JLabel("Wilayah Kecamatan:");
        lblPilih.setForeground(Color.WHITE);
        comboKecamatan = new JComboBox<>();
        comboKecamatan.setPreferredSize(new Dimension(150, 25));
        
        btnPindai = new JButton("Pindai Radar Cuaca");
        btnPindai.setBackground(new Color(46, 204, 113));
        btnPindai.setForeground(Color.WHITE);
        
        panelKontrolKiri.add(lblPilih);
        panelKontrolKiri.add(comboKecamatan);
        panelKontrolKiri.add(btnPindai);
        
        // Pembuatan Tombol Logout Sisi Kanan Navbar
        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        
        panelUtara.add(panelKontrolKiri, BorderLayout.WEST);
        panelUtara.add(btnLogout, BorderLayout.EAST);
        add(panelUtara, BorderLayout.NORTH);

        // --- PANEL BARAT: Telemetri & Input Data ---
        JPanel panelBarat = new JPanel();
        panelBarat.setLayout(new BoxLayout(panelBarat, BoxLayout.Y_AXIS));
        panelBarat.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panelBarat.setPreferredSize(new Dimension(320, 600));

        // Sub-Panel Angka Cuaca
        JPanel panelInfoCuaca = new JPanel(new GridLayout(3, 2, 5, 10));
        panelInfoCuaca.setBorder(BorderFactory.createTitledBorder("Telemetri Satelit Real-time"));
        panelInfoCuaca.add(new JLabel("Suhu Udara:")); lblSuhu = new JLabel("0.0 °C"); panelInfoCuaca.add(lblSuhu);
        panelInfoCuaca.add(new JLabel("Curah Hujan:")); lblHujan = new JLabel("0.0 mm"); panelInfoCuaca.add(lblHujan);
        panelInfoCuaca.add(new JLabel("Kecepatan Angin:")); lblAngin = new JLabel("0.0 km/h"); panelInfoCuaca.add(lblAngin);
        panelBarat.add(panelInfoCuaca);
        panelBarat.add(Box.createVerticalStrut(15));

        // Sub-Panel Konfigurasi Role Dinamis
        panelRoleSpesifik = new JPanel(new BorderLayout(5, 5));
        lblRoleHeader = new JLabel();
        lblRoleHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        comboOpsiRole = new JComboBox<>();
        
        panelRoleSpesifik.add(lblRoleHeader, BorderLayout.NORTH);
        panelRoleSpesifik.add(comboOpsiRole, BorderLayout.CENTER);
        panelBarat.add(panelRoleSpesifik);
        panelBarat.add(Box.createVerticalStrut(15));

        // Sub-Panel Output Sistem Pakar
        JPanel panelPakar = new JPanel(new BorderLayout());
        panelPakar.setBorder(BorderFactory.createTitledBorder("Rekomendasi Analisis Sistem Pakar"));
        txtRekomendasi = new JTextArea(8, 20);
        txtRekomendasi.setEditable(false);
        txtRekomendasi.setLineWrap(true);
        txtRekomendasi.setWrapStyleWord(true);
        panelPakar.add(new JScrollPane(txtRekomendasi), BorderLayout.CENTER);
        panelBarat.add(panelPakar);

        add(panelBarat, BorderLayout.WEST);

        // --- PANEL TENGAH: Kanvas Denah Spasial ---
        panelDenah = new MapPanel();
        add(panelDenah, BorderLayout.CENTER);

        konfigurasiRoleUI();
    }

    private void konfigurasiRoleUI() {
        if (currentRole.equals("ADMIN")) {
            lblRoleHeader.setText("Mode Kontrol Aturan Sistem (Admin)");
            comboOpsiRole.addItem("Ubah Threshold Parameter");
            comboOpsiRole.addItem("Manajemen Koordinat Geospasial");
        } else if (currentRole.equals("EO")) {
            lblRoleHeader.setText("Karakteristik Skala Keramaian Event:");
            comboOpsiRole.addItem("Skala Mikro (Pameran Dalam Tenda)");
            comboOpsiRole.addItem("Skala Makro (Megastruktur Panggung Outdoor)");
        } else if (currentRole.equals("AGRO")) {
            lblRoleHeader.setText("Komoditas Lahan Agraria Terpilih:");
            comboOpsiRole.addItem("Salak Pondoh Sleman");
            comboOpsiRole.addItem("Tanaman Cabai Hortikultura");
            comboOpsiRole.addItem("Komoditas Padi Sawah");
        }
    }

    public String getCurrentRole() {
        return this.currentRole;
    }

    // --- GETTER & SETTER ID SEKTOR UNTUK WEATHERCONTROLLER ---
    public void setIdSektor(int idSektor) {
        this.idSektor = idSektor;
    }

    public int getCurrentSektorId() {
        return this.idSektor;
    }

    // --- KELAS INTERNAL: Kanvas Grafis Denah Spasial Berlatar Belakang Peta ---
    public class MapPanel extends JPanel {
        private Color statusWarnaOverlay = new Color(46, 204, 113, 40); 
        private String teksNotifikasiPeta = "Status Lokasi: Aman Kondusif";
        private Image backgroundImage = null; // Penampung Citra Satelit Peta

        public MapPanel() {
            setBorder(BorderFactory.createTitledBorder("Visualisasi Zonasi Denah Spasial Lokasi Wilayah"));
            setBackground(new Color(236, 240, 241));
        }

        // Metode untuk menyuntikkan Gambar Peta dari Satelit secara Dinamis
        public void setBackgroundImage(Image img) {
            this.backgroundImage = img;
            repaint();
        }

        public void updateZonasiPeta(Color warnaBaru, String pesanStatus) {
            this.statusWarnaOverlay = warnaBaru;
            this.teksNotifikasiPeta = pesanStatus;
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 1. Gambar Citra Satelit Peta Asli jika berhasil terunduh
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 30, 40, width - 60, height - 90, this);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(30, 40, width - 60, height - 90);
                
                g2d.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2d.setColor(Color.GRAY);
                g2d.drawString("Tekan 'Pindai Radar' untuk memuat citra satelit wilayah...", (width/2) - 150, height/2);
            }

            // 2. Gambar Garis Grid Vektor Overlay Layout Infrastruktur Acara/Lahan
            g2d.setColor(new Color(149, 165, 166, 120)); // Garis semi transparan agar peta asli tetap kelihatan
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(width / 3, 40, width / 3, height - 50);
            g2d.drawLine(2 * width / 3, 40, 2 * width / 3, height - 50);
            g2d.drawLine(30, height / 2, width - 30, height / 2);

            // Labeling Teks Grid Lapisan Atas Peta
            g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2d.setColor(new Color(44, 62, 80));
            g2d.drawString("[ZONA UTARA: LOGISTIK & STRUKTUR]", 45, 65);
            g2d.drawString("[ZONA TENGAH: AREA INSTALASI ELEKTRIKAL]", 45, (height / 2) + 20);

            // 3. Render Efek Warna Deteksi Cuaca Ekstrem
            g2d.setColor(statusWarnaOverlay);
            g2d.fillRect(30, 40, width - 60, height - 90);

            // 4. Render Informasi Bar Notifikasi Status Bawah Panel
            g2d.setColor(new Color(52, 73, 94));
            g2d.fillRect(40, height - 110, width - 80, 40);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.setColor(Color.WHITE);
            g2d.drawString(teksNotifikasiPeta, 55, height - 85);
        }
    }
}