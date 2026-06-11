package Controller;

import exception.DatabaseException;
import model.Tiket;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet; 
 
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    // Helper koneksi database untuk hitung omset
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sistem_parkir_qren", "root", "");
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        int omsetHariIni = 0;

        try {
            // 1. Ambil data kendaraan aktif dari Model Tiket
            Tiket tiketModel = new Tiket();
            ArrayList<Tiket> daftarTiket = tiketModel.get();
            int jumlahAktif = tiketModel.hitungAktif();
            int slotTersedia = Tiket.KAPASITAS_MAKS - jumlahAktif;

            // 2. QUERY HITUNG OMSET: Ambil total biaya kendaraan yang keluar HARI INI
            try (Connection conn = getConnection()) {
                String sql = "SELECT SUM(total_biaya) AS total FROM tiket WHERE status = 'SELESAI' AND DATE(waktu_keluar) = CURDATE()";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    omsetHariIni = rs.getInt("total"); // Jika null, otomatis diset ke 0 oleh Java
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3. Set semua attribute untuk dikirim ke dashboard.jsp
            req.setAttribute("daftarTiket",   daftarTiket);
            req.setAttribute("jumlahAktif",   jumlahAktif);
            req.setAttribute("kapasitasMaks", Tiket.KAPASITAS_MAKS);
            req.setAttribute("slotTersedia",  slotTersedia);
            req.setAttribute("parkirPenuh",   slotTersedia <= 0);
            req.setAttribute("omsetHariIni",  omsetHariIni); // Kirim data omset ke JSP

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat data: " + e.getMessage());
        }

        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }

    private boolean isLoggedIn(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }
}