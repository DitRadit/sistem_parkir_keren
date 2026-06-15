package Controller;

import com.midtrans.httpclient.TransactionApi;
import model.Tiket;
import org.json.JSONObject;
import util.MidtransUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ValidasiTiketServlet", urlPatterns = {"/ValidasiTiketServlet"})
public class ValidasiTiketServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sistem_parkir_qren", "root", "");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String idTiket = req.getParameter("idTiket");
        if (idTiket == null || idTiket.isBlank()) {
            resp.sendRedirect("dashboard");
            return;
        }

        try (Connection conn = getConnection()) {

            String transactionId = null;
            int totalBiaya = 0;

            String selectSql = "SELECT snap_token, total_biaya FROM tiket WHERE id_tiket = ?";
            PreparedStatement selectPs = conn.prepareStatement(selectSql);
            selectPs.setString(1, idTiket);
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                transactionId = rs.getString("snap_token");
                totalBiaya = rs.getInt("total_biaya");
            }

            if (transactionId == null || transactionId.isBlank()) {
                req.setAttribute("error", "Transaksi pembayaran belum dibuat.");
                resp.sendRedirect("CheckoutServlet?idTiket=" + idTiket);
                return;
            }

            MidtransUtil.init();

            System.out.println("=== VALIDASI ===");
            System.out.println("transactionId dari DB : " + transactionId);

            JSONObject status = TransactionApi.checkTransaction(transactionId);

            System.out.println("=== CEK STATUS MIDTRANS ===");
            System.out.println(status.toString());
            System.out.println("============================");

            String transactionStatus = status.getString("transaction_status");
            System.out.println("transaction_status : " + transactionStatus);

            if (transactionStatus.equals("settlement") || transactionStatus.equals("capture")) {

                // Kalau karcis hilang, totalBiaya di DB = biaya parkir murni
                // → tambahkan denda sekarang saat disimpan sebagai SELESAI
                int totalFinal;
                if (transactionId.startsWith("SQR-HILANG-")) {
                    totalFinal = totalBiaya + Tiket.DENDA_KARCIS_HILANG;
                } else {
                    totalFinal = totalBiaya;
                }

                String updateSql = "UPDATE tiket SET waktu_keluar = NOW(), total_biaya = ?, status = 'SELESAI', status_bayar = 'LUNAS' WHERE id_tiket = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, totalFinal);
                updatePs.setString(2, idTiket);
                updatePs.executeUpdate();

                resp.sendRedirect("dashboard");

            } else if (transactionStatus.equals("pending")) {

                req.setAttribute("info", "Pembayaran belum diterima. Silakan scan QRIS terlebih dahulu.");
                if (transactionId.startsWith("SQR-HILANG-")) {
                    resp.sendRedirect("KarcisHilangServlet?idTiket=" + idTiket);
                } else {
                    resp.sendRedirect("CheckoutServlet?idTiket=" + idTiket);
                }

            } else {

                req.setAttribute("error", "Pembayaran gagal/expired (" + transactionStatus + "). Silakan ulangi.");
                if (transactionId.startsWith("SQR-HILANG-")) {
                    resp.sendRedirect("KarcisHilangServlet?idTiket=" + idTiket);
                } else {
                    resp.sendRedirect("CheckoutServlet?idTiket=" + idTiket);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("dashboard");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}