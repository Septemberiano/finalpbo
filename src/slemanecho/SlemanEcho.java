package slemanecho;

import java.sql.Connection;
import java.sql.SQLException;
import view.LoginFrame;
import controller.LoginController;

/**
 * @author adityaseptemberiano
 */
public class SlemanEcho {

    public static void main(String[] args) {
        // Mengatur tampilan agar mengikuti tema sistem operasi jika mendukung
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Mengabaikan error jika Nimbus gagal dimuat
        }

        // Inisialisasi gerbang login utama
        LoginFrame loginWindow = new LoginFrame();
        LoginController controller = new LoginController(loginWindow);
        
        // Membuka layar login ke user
        loginWindow.setVisible(true);
    }
}