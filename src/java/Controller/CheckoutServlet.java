package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

    // Method gampang untuk koneksi database sistem_parkir_qren
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sistem_parkir_qren", "root", "");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Validasi Sesi Login Operator
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Ambil parameter idTiket yang dikirim dari tombol klik di dashboard
        String idTiket = request.getParameter("idTiket");
        if (idTiket == null || idTiket.isBlank()) {
            response.sendRedirect("dashboard");
            return;
        }

        try (Connection conn = getConnection()) {
            // 3. Query ambil data spesifik berdasarkan id_tiket
            String sql = "SELECT * FROM tiket WHERE id_tiket = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idTiket);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Tarik data asli dari kolom database
                String platNomor = rs.getString("plat_nomor");
                String jenis = rs.getString("jenis");
                Timestamp waktuMasuk = rs.getTimestamp("waktu_masuk");
                Timestamp waktuSekarang = new Timestamp(System.currentTimeMillis());

                // 4. Hitung Tarif Parkir Otomatis Berdasarkan Waktu
                long selisihMilidetik = waktuSekarang.getTime() - waktuMasuk.getTime();
                long totalJam = selisihMilidetik / (1000 * 60 * 60);
                
                // Jika parkir kurang dari 1 jam (seperti di testing kamu), bulatkan tetap dihitung 1 jam
                if (totalJam < 1) {
                    totalJam = 1;
                }

                // Set tarif mall standar: Mobil Rp 5.000/jam, Motor Rp 2.000/jam
                int tarifPerJam = jenis.equalsIgnoreCase("Mobil") ? 5000 : 2000;
                int totalBiaya = (int) (totalJam * tarifPerJam);

                // 5. Kirim data lengkap ke halaman qris.jsp agar tidak NULL lagi
                request.setAttribute("idTiket", idTiket);
                request.setAttribute("platNomor", platNomor);
                request.setAttribute("jenis", jenis);
                request.setAttribute("totalBiaya", totalBiaya);
                
                // INTEGRASI LIVE PREVIEW QRIS: Menggunakan Open API QR untuk simulasi QRIS dinamis di layar
                String mockQrisUrl = "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=QRIS_MALL_PAYMENT_" + idTiket + "_" + totalBiaya;
                request.setAttribute("qrCodeUrl", mockQrisUrl);

                // Lempar ke halaman tampilan qris.jsp
                request.getRequestDispatcher("qris.jsp").forward(request, response);

            } else {
                // Jika ID tiket misterius tidak ditemukan di DB
                request.setAttribute("error", "Data tiket tidak ditemukan di sistem.");
                request.getRequestDispatcher("dashboard").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}