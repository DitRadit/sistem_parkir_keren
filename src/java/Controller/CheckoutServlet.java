package Controller;

import exception.DatabaseException;
import model.Tiket;
import util.MidtransUtil;
import util.QRUtil;

import com.midtrans.httpclient.CoreApi;
import com.midtrans.httpclient.error.MidtransError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/sistem_parkir_qren", "root", "");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idTiket = request.getParameter("idTiket");
        if (idTiket == null || idTiket.isBlank()) {
            response.sendRedirect("dashboard");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM tiket WHERE id_tiket = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idTiket);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String platNomor = rs.getString("plat_nomor");
                String jenis = rs.getString("jenis");
                Timestamp waktuMasuk = rs.getTimestamp("waktu_masuk");
                Timestamp waktuSekarang = new Timestamp(System.currentTimeMillis());

                long selisihMilidetik = waktuSekarang.getTime() - waktuMasuk.getTime();
                long totalJam = selisihMilidetik / (1000 * 60 * 60);
                if (totalJam < 1) totalJam = 1;

                int tarifPerJam = jenis.equalsIgnoreCase("Mobil") ? 5000 : 2000;
                int totalBiaya = (int) (totalJam * tarifPerJam);

                // ==== GENERATE QRIS VIA CORE API CHARGE ====
                MidtransUtil.init();

                Map<String, Object> params = new HashMap<>();

                Map<String, Object> txDetail = new HashMap<>();
                txDetail.put("order_id", "SQR-" + idTiket + "-" + System.currentTimeMillis());
                txDetail.put("gross_amount", (long) totalBiaya);

                params.put("payment_type", "qris");
                params.put("transaction_details", txDetail);

                Map<String, Object> qrisDetail = new HashMap<>();
                qrisDetail.put("acquirer", "gopay");
                params.put("qris", qrisDetail);

                JSONObject result = CoreApi.chargeTransaction(params);

                System.out.println("=== MIDTRANS RAW RESULT ===");
                System.out.println(result.toString());
                System.out.println("===========================");

                // Ambil URL gambar QR dari response actions
                String qrUrl = null;
                JSONArray actions = result.getJSONArray("actions");
                for (int i = 0; i < actions.length(); i++) {
                    JSONObject action = actions.getJSONObject(i);
                    if ("generate-qr-code".equals(action.getString("name"))) {
                        qrUrl = action.getString("url");
                        break;
                    }
                }

                if (qrUrl == null) {
                    throw new Exception("Tidak ada QR code di response Midtrans.");
                }

                System.out.println("=== MIDTRANS CHARGE RESULT ===");
                System.out.println("QR URL    : " + qrUrl);
                System.out.println("Order ID  : " + txDetail.get("order_id"));
                System.out.println("Trx ID    : " + result.getString("transaction_id"));
                System.out.println("===============================");

                // Download gambar QR dari Midtrans, simpan ke folder /qr/
                String folderQR = getServletContext().getRealPath("/qr/");
                java.io.File dir = new java.io.File(folderQR);
                if (!dir.exists()) dir.mkdirs();

                String fileName = "pay-" + idTiket + ".png";
                java.io.File fullFile = new java.io.File(dir, fileName);

                System.out.println("Saving QR to: " + fullFile.getAbsolutePath());

                try (java.io.InputStream in = new java.net.URL(qrUrl).openStream();
                     java.io.FileOutputStream out = new java.io.FileOutputStream(fullFile)) {
                    in.transferTo(out);
                }

                String orderId = (String) txDetail.get("order_id"); 
                String transactionId = result.getString("transaction_id");

                Tiket tiket = new Tiket();
                tiket.setIdTiket(idTiket);
                tiket.setSnapToken(orderId); 
                tiket.setTotalBiaya((double) totalBiaya);
                tiket.update();
                System.out.println("=== SIMPAN KE DB ===");
                System.out.println("orderId yang akan disimpan: " + orderId);
                tiket.setSnapToken(orderId);
                tiket.update();
                System.out.println("Update DB selesai.");

                // tetap kirim transaction_id ke JSP untuk display jika perlu
                request.setAttribute("snapToken", transactionId);

                // Kirim ke qris.jsp
                request.setAttribute("idTiket", idTiket);
                request.setAttribute("platNomor", platNomor);
                request.setAttribute("jenis", jenis);
                request.setAttribute("totalBiaya", totalBiaya);
                request.setAttribute("snapToken", transactionId);
                request.setAttribute("qrCodeUrl", request.getContextPath() + "/qr/" + fileName);
                request.setAttribute("qrOriginalUrl", qrUrl); // URL asli Midtrans untuk simulator sandbox

                request.getRequestDispatcher("qris.jsp").forward(request, response);

            } else {
                request.setAttribute("error", "Data tiket tidak ditemukan di sistem.");
                request.getRequestDispatcher("dashboard").forward(request, response);
            }

        } catch (MidtransError e) {
            e.printStackTrace();
            response.sendRedirect("dashboard");
        } catch (DatabaseException e) {
            e.printStackTrace();
            response.sendRedirect("dashboard");
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