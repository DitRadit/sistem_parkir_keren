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

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

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

        try {
            // Ambil tiket lewat Model (sekaligus validasi via interface Validatable)
            Tiket tiket = new Tiket().find(idTiket);

            if (!tiket.validasiTiket()) {
                request.setAttribute("error", "Tiket sudah tidak aktif / sudah diproses sebelumnya.");
                request.getRequestDispatcher("dashboard").forward(request, response);
                return;
            }

            String platNomor = tiket.getPlatNomor();
            String jenis = tiket.getJenis();

            // Hitung tarif lewat polymorphism Kendaraan (Mobil/Motor)
            int totalBiaya = (int) tiket.kalkulasiBiaya();

            // ==== GENERATE QRIS VIA CORE API CHARGE (dibungkus class QRPayment) ====
            QRPayment qrPayment = new QRPayment(totalBiaya);

            String folderQR = getServletContext().getRealPath("/qr/");
            String fileName = "pay-" + idTiket + ".png";

            qrPayment.setDetailTransaksi("SQR", idTiket, folderQR, fileName);
            qrPayment.prosesPembayaran();

            String orderId = qrPayment.getOrderId();
            String transactionId = qrPayment.getTransactionId();

            tiket.setSnapToken(orderId);
            tiket.setTotalBiaya((double) totalBiaya);
            tiket.update();

            // Kirim ke qris.jsp
            request.setAttribute("idTiket", idTiket);
            request.setAttribute("platNomor", platNomor);
            request.setAttribute("jenis", jenis);
            request.setAttribute("totalBiaya", totalBiaya);
            request.setAttribute("snapToken", transactionId);
            request.setAttribute("qrCodeUrl", request.getContextPath() + "/qr/" + fileName);
            request.setAttribute("qrOriginalUrl", qrPayment.getQrCode().getDataQR());

            request.getRequestDispatcher("qris.jsp").forward(request, response);

        } catch (TiketException e) {
            request.setAttribute("error", "Data tiket tidak ditemukan di sistem.");
            request.getRequestDispatcher("dashboard").forward(request, response);

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