/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.QRPayment;
import model.Tiket;

import java.io.IOException;
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
            Tiket tiket = new Tiket();
            Tiket tiketDitemukan = tiket.find(idTiket);

            if (!tiketDitemukan.validasiTiket()) {
                request.setAttribute("error", "Tiket sudah tidak aktif / sudah diproses sebelumnya.");
                request.getRequestDispatcher("karcis_hilang.jsp").forward(request, response);
                return;
            }

            String platNomor = tiketDitemukan.getPlatNomor();
            String jenis = tiketDitemukan.getJenis();

            // Kalau sudah pernah checkout normal, pakai total_biaya dari DB.
            // Kalau belum, hitung lewat Tiket.kalkulasiBiaya() (sudah polymorphic Kendaraan)
            Double biayaParkirDB = tiketDitemukan.getTotalBiaya();
            long biayaParkir = (biayaParkirDB != null && biayaParkirDB > 0)
                    ? biayaParkirDB.longValue()
                    : (long) tiketDitemukan.kalkulasiBiaya();

            long totalBiaya = biayaParkir + Tiket.DENDA_KARCIS_HILANG;

            // ==== GENERATE QRIS VIA CORE API CHARGE (dibungkus class QRPayment) ====
            QRPayment qrPayment = new QRPayment(totalBiaya);

            String folderQR = getServletContext().getRealPath("/qr/");
            String fileName = "pay-hilang-" + idTiket + ".png";

            qrPayment.setDetailTransaksi("SQR-HILANG", idTiket, folderQR, fileName);
            qrPayment.prosesPembayaran();

            tiketDitemukan.setSnapToken(qrPayment.getOrderId());
            tiketDitemukan.setTotalBiaya((double) biayaParkir);
            tiketDitemukan.update();

            request.setAttribute("idTiket", idTiket);
            request.setAttribute("platNomor", platNomor);
            request.setAttribute("jenis", jenis);
            request.setAttribute("biayaParkir", (int) biayaParkir);
            request.setAttribute("dendaKarcis", Tiket.DENDA_KARCIS_HILANG);
            request.setAttribute("totalBiaya", totalBiaya);
            request.setAttribute("snapToken", qrPayment.getTransactionId());
            request.setAttribute("qrCodeUrl", request.getContextPath() + "/qr/" + fileName);
            request.setAttribute("qrOriginalUrl", qrPayment.getQrCode().getDataQR());

            request.getRequestDispatcher("qris_karcis_hilang.jsp").forward(request, response);

        } catch (TiketException e) {
            request.setAttribute("error", "Tiket tidak ditemukan atau sudah tidak aktif: " + e.getMessage());
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