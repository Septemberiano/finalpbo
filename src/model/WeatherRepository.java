/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author adityaseptemberiano
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.json.JSONObject;

public class WeatherRepository {
    private Connection conn;

    public WeatherRepository(Connection conn) {
        this.conn = conn;
    }

    public List<String> ambilSemuaNamaKecamatan() {
        List<String> daftar = new ArrayList<>();
        try {
            String sql = "SELECT nama_kecamatan FROM tb_kecamatan ORDER BY nama_kecamatan ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                daftar.add(rs.getString("nama_kecamatan"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal load data kecamatan: " + e.getMessage());
        }
        return daftar;
    }

    public KondisiUdara dapatkanCuacaSektor(String namaKecamatan) {
        try {
            String sql = "SELECT * FROM tb_kecamatan WHERE nama_kecamatan = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, namaKecamatan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Kecamatan kec = new Kecamatan(
                    rs.getInt("id_kecamatan"),
                    rs.getString("nama_kecamatan"),
                    rs.getDouble("latitude"),
                    rs.getDouble("longitude"),
                    rs.getString("sektor_utama"),
                    rs.getString("sub_sektor")
                );

                String urlStr = "https://api.open-meteo.com/v1/forecast?latitude=" + kec.getLatitude() +
                                "&longitude=" + kec.getLongitude() +
                                "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation,weather_code";

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder respon = new StringBuilder();
                    String baris;
                    while ((baris = reader.readLine()) != null) respon.append(baris);
                    reader.close();

                    JSONObject jsonCurrent = new JSONObject(respon.toString()).getJSONObject("current");
                    double suhu = jsonCurrent.getDouble("temperature_2m");
                    int kelembapan = jsonCurrent.getInt("relative_humidity_2m");
                    double angin = jsonCurrent.getDouble("wind_speed_10m");
                    double hujan = jsonCurrent.getDouble("precipitation");
                    int kodeCuaca = jsonCurrent.getInt("weather_code");

                    String deskripsi = kodeCuaca >= 80 ? "Hujan Deras" : kodeCuaca >= 61 ? "Hujan" : kodeCuaca == 0 ? "Cerah" : "Berawan";

                    simpanKeLog(kec.getId(), suhu, kelembapan, angin, hujan, deskripsi);

                    if (hujan > 5.0 || angin > 15.0 || kodeCuaca >= 80) {
                        return new CuacaEkstrem(kec, suhu, kelembapan, angin, hujan, deskripsi + " Ekstrem");
                    } else {
                        return new CuacaNormal(kec, suhu, kelembapan, angin, hujan, deskripsi);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal sinkronisasi API: " + e.getMessage());
        }
        return null;
    }

    private void simpanKeLog(int idKec, double suhu, int lembap, double angin, double hujan, String status) {
        try {
            String sql = "INSERT INTO tb_log_cuaca (id_kecamatan, suhu, kelembapan, kecepatan_angin, curah_hujan, status_cuaca) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idKec);
            ps.setDouble(2, suhu);
            ps.setInt(3, lembap);
            ps.setDouble(4, angin);
            ps.setDouble(5, hujan);
            ps.setString(6, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal menulis log ke database: " + e.getMessage());
        }
    }

    public ImageIcon unduhGambarPeta(String namaKecamatan, String statusCuaca) {
        try {
            String query = "SELECT latitude, longitude FROM tb_kecamatan WHERE nama_kecamatan = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, namaKecamatan);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");

                String markerStyle = "pm2gnm"; // Hijau (Normal)
                if (statusCuaca.contains("Ekstrem") || statusCuaca.contains("Hujan Deras")) {
                    markerStyle = "pm2rdm"; // Merah Neon (Bahaya)
                } else if (statusCuaca.contains("Hujan")) {
                    markerStyle = "pm2blm"; // Biru Neon (Hujan)
                }

                String mapUrl = "https://static-maps.yandex.ru/1.x/?ll=" + lon + "," + lat +
                                "&z=12&l=sat,skl&size=430,220&pt=" + lon + "," + lat + "," + markerStyle;

                URL url = new URL(mapUrl);
                HttpURLConnection connMap = (HttpURLConnection) url.openConnection();
                if (connMap.getResponseCode() == 200) {
                    InputStream is = connMap.getInputStream();
                    byte[] bytes = is.readAllBytes();
                    is.close();
                    return new ImageIcon(bytes);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat peta spasial: " + e.getMessage());
        }
        return null;
    }
}
