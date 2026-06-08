/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.PembayaranException;
import exception.TiketException;
import model.Tiket;
import util.JDBC;
import util.MidtransUtil;
import util.QRUtil;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

public class GenerateQRISServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        String idTiket     = req.getParameter("idTiket");
        String platNomor   = req.getParameter("platNomor");
        String jenis       = req.getParameter("jenis");
        String totalBiayaStr = req.getParameter("totalBiaya");
        String durasiJam   = req.getParameter("durasiJam");

        try {

            double totalBiaya =
                Double.parseDouble(totalBiayaStr);

            MidtransUtil.init();

            // Buat parameter Midtrans Snap
            Map<String, Object> params = new HashMap<>();

            Map<String, Object> txDetail = new HashMap<>();
            txDetail.put("order_id", "SQR-" + idTiket);
            txDetail.put("gross_amount", (long) totalBiaya);

            List<String> payments = new ArrayList<>();
            payments.add("qris");
            payments.add("gopay");

            params.put("transaction_details", txDetail);
            params.put("enabled_payments", payments);

            JSONObject result =
                SnapApi.createTransaction(params);
            String snapToken =
                result.getString("token");

            String redirectUrl =
                result.getString("redirect_url");

            // Generate QR image dari redirect URL
            String folderQR =
                getServletContext().getRealPath("/qr/");

            QRUtil.generateQRCode(
                redirectUrl,
                folderQR + "pay-" + idTiket + ".png"
            );

            // Update tiket di DB via Model
            Tiket tiket = new Tiket();
            tiket.setIdTiket(idTiket);
            tiket.setSnapToken(snapToken);
            tiket.setTotalBiaya(totalBiaya);
            tiket.update(); // UPDATE snap_token & total_biaya

            // Kirim ke qris.jsp
            req.setAttribute("idTiket",   idTiket);
            req.setAttribute("platNomor", platNomor);
            req.setAttribute("jenis",     jenis);
            req.setAttribute("totalBiaya", totalBiaya);
            req.setAttribute("durasiJam", durasiJam);
            req.setAttribute("snapToken", snapToken);
            req.setAttribute(
                "qrImage",
                req.getContextPath() +
                "/qr/pay-" + idTiket + ".png"
            );

            req.getRequestDispatcher("qris.jsp")
               .forward(req, resp);

        } catch (MidtransError e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Gagal membuat transaksi Midtrans: " +
                e.getMessage()
            );
            req.setAttribute("idTiket",    idTiket);
            req.setAttribute("platNomor",  platNomor);
            req.setAttribute("jenis",      jenis);
            req.setAttribute("totalBiaya", totalBiayaStr);
            req.setAttribute("durasiJam",  durasiJam);
            req.getRequestDispatcher("pembayaran.jsp")
               .forward(req, resp);

        } catch (DatabaseException e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Error database: " + e.getMessage()
            );
            req.getRequestDispatcher("pembayaran.jsp")
               .forward(req, resp);

        } catch (Exception e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Terjadi kesalahan: " + e.getMessage()
            );
            req.getRequestDispatcher("pembayaran.jsp")
               .forward(req, resp);
        }
    }

    private boolean isLoggedIn(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);

        if (session == null ||
                session.getAttribute("admin") == null) {

            resp.sendRedirect("login.jsp");
            return false;
        }

        return true;
    }
}
