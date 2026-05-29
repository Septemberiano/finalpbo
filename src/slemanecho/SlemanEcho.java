/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package slemanecho;
import controller.WeatherController;
import database.DbConnection;
import model.WeatherRepository;
import view.MainFrame;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author adityaseptemberiano
 */
public class SlemanEcho {
    public static void main(String[] args) {
        try {
            // 1. Ambil koneksi terpusat dari package database
            Connection koneksi = DbConnection.getConnection();
            System.out.println("Koneksi Database Sleman Sukses!");
            
            // 2. Injeksi koneksi ke repositori model
            WeatherRepository modelRepo = new WeatherRepository(koneksi);
            MainFrame viewFrame = new MainFrame();
            
            // 3. Pasangkan ke Controller
            WeatherController controller = new WeatherController(viewFrame, modelRepo);
            
            // 4. Nyalakan JFrame
            viewFrame.setLocationRelativeTo(null);
            viewFrame.setVisible(true);
            
        } catch (SQLException e) {
            System.err.println("Aplikasi gagal berjalan karena masalah database: " + e.getMessage());
        }
    }
}
