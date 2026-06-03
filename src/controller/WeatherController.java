package controller;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import database.DbConnection;
import view.MainFrame;
import view.LoginFrame;

public class WeatherController {
    private MainFrame view;

    public WeatherController(MainFrame view) {
        this.view = view;
        
        // Listener Tombol Pindai Cuaca
        this.view.btnPindai.addActionListener(e -> {
            new Thread(() -> eksekusiPindaiRadar()).start();
        });

        // Listener Tombol Logout
        this.view.btnLogout.addActionListener(e -> {
            int opsi = JOptionPane.showConfirmDialog(view, 
                    "Apakah Anda yakin ingin logout?", 
                    "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
            
            if (opsi == JOptionPane.YES_OPTION) {
                view.dispose();
                LoginFrame loginView = new LoginFrame();
                new LoginController(loginView);
                loginView.setVisible(true);
            }
        });
    }

    public void loadComboKecamatan() {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nama_kecamatan FROM tb_kecamatan ORDER BY nama_kecamatan ASC");
             ResultSet rs = ps.executeQuery()) {
            
            view.comboKecamatan.removeAllItems();
            while (rs.next()) {
                view.comboKecamatan.addItem(rs.getString("nama_kecamatan"));
            }
        } catch (SQLException e) {
            view.comboKecamatan.addItem("Turi");
            view.comboKecamatan.addItem("Kalasan");
        }
    }

    public void eksekusiPindaiRadar() {
        String wilayah = (String) view.comboKecamatan.getSelectedItem();
        double lat = -7.6322, lon = 110.3752; 
        int idKecamatanTerdeteksi = 0;

        // Ambil Data Geospasial sekaligus ID Primary Key Kecamatan
        String queryKecamatan = "SELECT id_kecamatan, latitude, longitude FROM tb_kecamatan WHERE nama_kecamatan = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryKecamatan)) {
            ps.setString(1, wilayah);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idKecamatanTerdeteksi = rs.getInt("id_kecamatan");
                    lat = rs.getDouble("latitude");
                    lon = rs.getDouble("longitude");
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal memuat koordinat DB lokal.");
        }

        Image citraSatelitPeta = null;
        try {
            String urlPath = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current=temperature_2m,rain,wind_speed_10m";
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(4000); 

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawJson = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                rawJson.append(line);
            }
            reader.close();

            String jsonOutput = rawJson.toString();
            int cutPoint = jsonOutput.indexOf("\"current\":");
            String cleanJson = jsonOutput.substring(cutPoint);

            double suhu = ambilNilaiJson(cleanJson, "\"temperature_2m\":");
            double hujan = ambilNilaiJson(cleanJson, "\"rain\":");
            double angin = ambilNilaiJson(cleanJson, "\"wind_speed_10m\":");

            // Tulis Transaksi Log ke DB dan dapatkan ID log barunya untuk rekam histori alert
            int idLogBaru = 0;
            if (idKecamatanTerdeteksi != 0) {
                idLogBaru = simpanLogPindaian(idKecamatanTerdeteksi, suhu, hujan, angin);
            }

            try {
                String yandexMapUrl = "https://static-maps.yandex.ru/1.x/?ll=" + lon + "," + lat + "&z=13&l=sat&size=650,450";
                citraSatelitPeta = ImageIO.read(new URL(yandexMapUrl));
            } catch (Exception ex) {
                System.out.println("Gagal memuat gambar peta geospasial.");
            }

            final double finalSuhu = suhu;
            final double finalHujan = hujan;
            final double finalAngin = angin;
            final Image finalPeta = citraSatelitPeta;
            final int finalIdLog = idLogBaru;

            javax.swing.SwingUtilities.invokeLater(() -> {
                view.lblSuhu.setText(finalSuhu + " °C");
                view.lblHujan.setText(finalHujan + " mm");
                view.lblAngin.setText(finalAngin + " km/h");
                
                if (finalPeta != null) {
                    view.panelDenah.setBackgroundImage(finalPeta);
                }
                
                // Proses mesin sistem pakar terintegrasi tabel jembatan audit trail
                prosesSistemPakarDariDatabase(finalIdLog, finalSuhu, finalHujan, finalAngin);
            });

        } catch (Exception ex) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                view.txtRekomendasi.setText("Koneksi instrumen satelit terputus!");
                view.panelDenah.updateZonasiPeta(new Color(241, 196, 15, 40), "ERROR NET: Gagal Menjangkau Server Peta");
            });
        }
    }

    private int simpanLogPindaian(int idKecamatan, double suhu, double hujan, double angin) {
        String queryLog = "INSERT INTO tb_log_pindaian (id_kecamatan, suhu_tercatat, hujan_tercatat, angin_tercatat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryLog, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idKecamatan);
            ps.setDouble(2, suhu);
            ps.setDouble(3, hujan);
            ps.setDouble(4, angin);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return ID Log yang baru masuk untuk tabel jembatan
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal menulis log transaksi: " + e.getMessage());
        }
        return 0;
    }

    private void prosesSistemPakarDariDatabase(int idLog, double suhu, double hujan, double angin) {
        int idSektorAktif = view.getCurrentSektorId(); // Mengambil ID dari login (1 = EO, 2 = AGRO)
        StringBuilder akumulasiRekomendasi = new StringBuilder();
        
        Color warnaOverlay = new Color(46, 204, 113, 40); // Default Hijau Aman
        String statusPeta = "Status Lokasi: Aman Kondusif";
        boolean terdeteksiBahaya = false;

        String queryAturan = "SELECT a.id_aturan, a.parameter_cuaca, a.operator, a.nilai_ambang, a.status_risiko, r.teks_rekomendasi " +
                             "FROM tb_aturan_dampak a " +
                             "JOIN tb_rekomendasi_tindakan r ON a.id_aturan = r.id_aturan " +
                             "WHERE a.id_sektor = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryAturan)) {
            
            ps.setInt(1, idSektorAktif);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idAturan = rs.getInt("id_aturan");
                    String param = rs.getString("parameter_cuaca");
                    String op = rs.getString("operator");
                    double threshold = rs.getDouble("nilai_ambang");
                    String pesanRisiko = rs.getString("status_risiko");
                    String teksRekomendasi = rs.getString("teks_rekomendasi");

                    double nilaiAktif = 0.0;
                    if (param.equals("suhu")) nilaiAktif = suhu;
                    else if (param.equals("curah_hujan")) nilaiAktif = hujan;
                    else if (param.equals("kecepatan_angin")) nilaiAktif = angin;

                    boolean isTriggered = false;
                    if (op.equals(">") && nilaiAktif > threshold) isTriggered = true;
                    else if (op.equals("<") && nilaiAktif < threshold) isTriggered = true;
                    else if (op.equals(">=") && nilaiAktif >= threshold) isTriggered = true;
                    else if (op.equals("<=") && nilaiAktif <= threshold) isTriggered = true;

                    if (isTriggered) {
                        terdeteksiBahaya = true;
                        warnaOverlay = new Color(231, 76, 60, 50); // Red Alert
                        statusPeta = pesanRisiko;
                        akumulasiRekomendasi.append(teksRekomendasi).append("\n\n");
                        
                        // Kunci pelanggaran historis ke tabel jembatan log_alert jika idLog valid
                        if (idLog != 0) {
                            String queryIkatAlert = "INSERT INTO tb_log_alert (id_log, id_aturan) VALUES (?, ?)";
                            try (PreparedStatement psAlert = conn.prepareStatement(queryIkatAlert)) {
                                psAlert.setInt(1, idLog);
                                psAlert.setInt(2, idAturan);
                                psAlert.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            akumulasiRekomendasi.append("Gagal memuat aturan validasi dari database.");
        }

        if (!terdeteksiBahaya) {
            if (idSektorAktif == 1) {
                akumulasiRekomendasi.append("✅ Cuaca aman berdasarkan standardisasi DB Sektor EO. Kegiatan lapangan aman dilaksanakan.");
            } else if (idSektorAktif == 2) {
                akumulasiRekomendasi.append("✅ Indikator cuaca optimal berdasarkan batas aman agrikultur. Lahan aman untuk pemeliharaan.");
            } else {
                akumulasiRekomendasi.append("Sistem normal. Mode Pengawasan Geospasial Sleman Aktif.");
            }
        }

        view.txtRekomendasi.setText(akumulasiRekomendasi.toString());
        view.panelDenah.updateZonasiPeta(warnaOverlay, statusPeta);
    }

    public double ambilNilaiJson(String json, String key) {
        try {
            int startIdx = json.indexOf(key) + key.length();
            int endIdxComma = json.indexOf(",", startIdx);
            int endIdxBracket = json.indexOf("}", startIdx);
            int finalEndIdx = (endIdxComma < endIdxBracket && endIdxComma != -1) ? endIdxComma : endIdxBracket;
            
            String token = json.substring(startIdx, finalEndIdx).trim();
            return Double.parseDouble(token);
        } catch (Exception e) {
            return 0.0; 
        }
    }
}