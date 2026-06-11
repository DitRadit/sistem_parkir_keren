package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ValidasiTiketServlet", urlPatterns = {"/ValidasiTiketServlet"})
public class ValidasiTiketServlet extends HttpServlet {

    // Method helper untuk koneksi ke database sistem_parkir_qren
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sistem_parkir_qren", "root", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. Validasi Keamanan Sesi Operator Gerbang
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // 2. Tangkap data ID Tiket dan Total Biaya yang dikirim dari form pembayaran.jsp
        String idTiket = req.getParameter("idTiket");
        String totalBiayaStr = req.getParameter("totalBiaya");

        if (idTiket == null || idTiket.isBlank()) {
            resp.sendRedirect("dashboard");
            return;
        }

        try (Connection conn = getConnection()) {
            // 3. Query SQL untuk menutup sesi parkir kendaraan di mall
            // Mengubah status menjadi 'SELESAI', mengisi waktu_keluar dengan jam sekarang (NOW()), 
            // menyimpan total biaya, serta mengubah status_bayar menjadi 'SUKSES'.
            String sql = "UPDATE tiket SET waktu_keluar = NOW(), total_biaya = ?, status = 'SELESAI', status_bayar = 'SUKSES' WHERE id_tiket = ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            
            // Konversi total biaya kembali ke integer angka murni untuk disave ke database
            int totalBiaya = (totalBiayaStr != null) ? Integer.parseInt(totalBiayaStr) : 0;
            ps.setInt(1, totalBiaya);
            ps.setString(2, idTiket);
            
            // Eksekusi perubahan ke database database
            ps.executeUpdate();

            // 4. SELESAI & REDIRECT: Langsung tendang balik ke Dashboard Utama Mall
            // Menggunakan sendRedirect agar DashboardServlet memicu ulang perhitungan hitungAktif()
            resp.sendRedirect("dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            // Jika ada ganjalan database error, amankan dengan melempar balik ke dashboard
            resp.sendRedirect("dashboard");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Jika ada yang iseng akses lewat URL biasa, luruskan dengan mengarahkan ke doPost
        doPost(req, resp);
    }
}