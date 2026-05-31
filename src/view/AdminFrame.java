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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminFrame extends JFrame {
    public JTable tabelKecamatan;
    public DefaultTableModel tableModel;
    public JButton btnTambah, btnUbah, btnHapus, btnLogout;
    public JTextField txtNamaKec, txtSektor, txtLat, txtLong;

    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color SURFACE_DARK = new Color(30, 30, 30);
    private final Color TEXT_WHITE = new Color(245, 245, 245);
    private final Color ACCENT_CYAN = new Color(0, 229, 255);

    public AdminFrame() {
        setTitle("Sleman Eco-Weather - Admin Command Center ⚙️");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(15, 15));
        setSize(880, 500);

        // Header Control
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(SURFACE_DARK);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("DATABASE MANAGEMENT CONTROL PANEL (ROLE: ADMIN)", JLabel.LEFT);
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTitle.setForeground(ACCENT_CYAN);
        
        btnLogout = new JButton("LOGOUT 🚪");
        btnLogout.setBackground(new Color(255, 85, 85));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(btnLogout, BorderLayout.EAST);

        // Konten Tengah
        JPanel panelKonten = new JPanel(new GridLayout(1, 2, 15, 0));
        panelKonten.setOpaque(false);
        panelKonten.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        // Sub-Panel Input Data Form
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 20));
        panelForm.setOpaque(false);
        TitledBorder borderForm = BorderFactory.createTitledBorder("MANIPULASI DATA WILAYAH");
        borderForm.setTitleColor(TEXT_WHITE);
        panelForm.setBorder(borderForm);

        txtNamaKec = createStyledField();
        txtSektor = createStyledField();
        txtLat = createStyledField();
        txtLong = createStyledField();

        panelForm.add(createFormLabel("NAMA KECAMATAN:")); panelForm.add(txtNamaKec);
        panelForm.add(createFormLabel("SEKTOR UTAMA:")); panelForm.add(txtSektor);
        panelForm.add(createFormLabel("KOORDINAT LAT:")); panelForm.add(txtLat);
        panelForm.add(createFormLabel("KOORDINAT LONG:")); panelForm.add(txtLong);

        // Sub-Panel Komponen Tabel
        String[] kolom = {"ID", "Kecamatan", "Sektor", "Latitude", "Longitude"};
        tableModel = new DefaultTableModel(kolom, 0);
        tabelKecamatan = new JTable(tableModel);
        tabelKecamatan.setBackground(SURFACE_DARK);
        tabelKecamatan.setForeground(TEXT_WHITE);
        tabelKecamatan.setGridColor(new Color(50, 50, 50));
        
        JScrollPane scrollTable = new JScrollPane(tabelKecamatan);
        scrollTable.getViewport().setBackground(BG_DARK);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)), 
            "DAFTAR DATA KECAMATAN AKTIF", 
            TitledBorder.LEFT, TitledBorder.TOP, null, ACCENT_CYAN
        ));

        panelKonten.add(panelForm);
        panelKonten.add(scrollTable);

        // Panel Tombol Aksi Kendali CRUD
        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelAksi.setBackground(SURFACE_DARK);
        
        btnTambah = createCrudButton("TAMBAH DATA ➕", ACCENT_CYAN, Color.BLACK);
        btnUbah = createCrudButton("PERBARUI DATA 🔄", Color.LIGHT_GRAY, Color.BLACK);
        btnHapus = createCrudButton("HAPUS DATA ❌", new Color(200, 50, 50), Color.WHITE);

        panelAksi.add(btnTambah);
        panelAksi.add(btnUbah);
        panelAksi.add(btnHapus);

        add(panelHeader, BorderLayout.NORTH);
        add(panelKonten, BorderLayout.CENTER);
        add(panelAksi, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setBackground(SURFACE_DARK);
        f.setForeground(TEXT_WHITE);
        f.setCaretColor(TEXT_WHITE);
        f.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        return f;
    }

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.GRAY);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        return l;
    }

    private JButton createCrudButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        return b;
    }
    public javax.swing.JButton getBtnTambah() { return btnTambah; }
public javax.swing.JButton getBtnUbah() { return btnUbah; }
public javax.swing.JButton getBtnHapus() { return btnHapus; }
public javax.swing.JButton getBtnLogout() { return btnLogout; }
public javax.swing.JTable getTabelKecamatan() { return tabelKecamatan; }
public javax.swing.JTextField getTxtNamaKec() { return txtNamaKec; }
public javax.swing.JTextField getTxtSektor() { return txtSektor; }
public javax.swing.JTextField getTxtLat() { return txtLat; }
public javax.swing.JTextField getTxtLong() { return txtLong; }
}