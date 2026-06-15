/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.Tiket;
import util.MidtransUtil;

import com.midtrans.httpclient.CoreApi;
import com.midtrans.httpclient.error.MidtransError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *
 * @author NabilRapa
 */
@WebServlet(name = "KarcisHilangServlet", urlPatterns = {"/KarcisHilangServlet"})
public class KarcisHilangServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idTiket = request.getParameter("idTiket");

        // Kalau ada idTiket dari dashboard → langsung proses, bypass form
        if (idTiket != null && !idTiket.isBlank()) {
            prosesKarcisHilangById(idTiket, request, response);
            return;
        }

        // Kalau tidak ada parameter → tampilkan form input plat
        request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
    }
    
    private void prosesKarcisHilangById(String idTiket,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Cari tiket langsung by idTiket
            Tiket tiket = new Tiket();
            Tiket tiketDitemukan = tiket.find(idTiket);

            String platNomor = tiketDitemukan.getPlatNomor();
            String jenis = tiketDitemukan.getJenis();

            // Ambil total_biaya dari DB yang sudah dihitung CheckoutServlet
            // Kalau null (belum pernah checkout normal), hitung manual pakai logika yang SAMA dengan CheckoutServlet
            Double biayaParkirDB = tiketDitemukan.getTotalBiaya();
            long biayaParkir;

            if (biayaParkirDB != null && biayaParkirDB > 0) {
                // Sudah pernah di-checkout → pakai yang sudah ada di DB
                biayaParkir = biayaParkirDB.longValue();
            } else {
                // Belum pernah checkout → hitung manual, pakai logika SAMA seperti CheckoutServlet
                java.time.LocalDateTime masuk = tiketDitemukan.getWaktuMasuk();
                long selisihMenit = java.time.temporal.ChronoUnit.MINUTES.between(masuk, java.time.LocalDateTime.now());
                long totalJam = selisihMenit / 60;
                if (totalJam < 1) {
                    totalJam = 1;
                }
                int tarif = "Mobil".equalsIgnoreCase(tiketDitemukan.getJenis()) ? 5000 : 2000;
                biayaParkir = totalJam * tarif;
            }

            long totalBiaya = biayaParkir + Tiket.DENDA_KARCIS_HILANG;

            // Generate QRIS via Midtrans
            MidtransUtil.init();

            Map<String, Object> params = new HashMap<>();
            Map<String, Object> txDetail = new HashMap<>();
            String orderId = "SQR-HILANG-" + idTiket + "-" + System.currentTimeMillis();
            txDetail.put("order_id", orderId);
            txDetail.put("gross_amount", (long) totalBiaya);

            params.put("payment_type", "qris");
            params.put("transaction_details", txDetail);

            Map<String, Object> qrisDetail = new HashMap<>();
            qrisDetail.put("acquirer", "gopay");
            params.put("qris", qrisDetail);

            JSONObject result = CoreApi.chargeTransaction(params);

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
                throw new Exception("QR URL tidak ada di response Midtrans.");
            }

            // Simpan gambar QR
            String folderQR = getServletContext().getRealPath("/qr/");
            java.io.File dir = new java.io.File(folderQR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "pay-hilang-" + idTiket + ".png";
            java.io.File fullFile = new java.io.File(dir, fileName);

            try (java.io.InputStream in = new java.net.URL(qrUrl).openStream(); java.io.FileOutputStream out = new java.io.FileOutputStream(fullFile)) {
                in.transferTo(out);
            }

            String transactionId = result.getString("transaction_id");

            // Update DB
            tiketDitemukan.setSnapToken(orderId);
            tiketDitemukan.setTotalBiaya((double) totalBiaya);
            tiketDitemukan.update();

            // Forward ke qris_hilang.jsp (bukan qris.jsp biasa)
            request.setAttribute("idTiket", idTiket);
            request.setAttribute("platNomor", platNomor);
            request.setAttribute("jenis", jenis);
            request.setAttribute("biayaParkir", (int) biayaParkir);
            request.setAttribute("dendaKarcis", Tiket.DENDA_KARCIS_HILANG);
            request.setAttribute("totalBiaya", totalBiaya);
            request.setAttribute("snapToken", transactionId);
            request.setAttribute("qrCodeUrl", request.getContextPath() + "/qr/" + fileName);
            request.setAttribute("qrOriginalUrl", qrUrl);

            request.getRequestDispatcher("qris_karcis_hilang.jsp").forward(request, response);

        } catch (TiketException e) {
            request.setAttribute("error", "Tiket tidak ditemukan atau sudah tidak aktif: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        } catch (MidtransError e) {
            e.printStackTrace();
            request.setAttribute("error", "Gagal generate QRIS: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        } catch (DatabaseException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String platNomor = request.getParameter("platNomor");
        if (platNomor == null || platNomor.isBlank()) {
            request.setAttribute("error", "Plat nomor wajib diisi.");
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
            return;
        }

        platNomor = platNomor.trim().toUpperCase();

        try {
            // Cari tiket by plat
            Tiket tiket = new Tiket();
            Tiket tiketDitemukan = tiket.findByPlat(platNomor);

            // Setelah dapat idTiket-nya, delegate ke method yang sama
            prosesKarcisHilangById(tiketDitemukan.getIdTiket(), request, response);

        } catch (TiketException e) {
            request.setAttribute("error", "Tidak ada kendaraan aktif dengan plat tersebut: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        } catch (DatabaseException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
        }
    }
}