/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.Tiket;
import util.QRUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

public class KendaraanMasukServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        try {

            Tiket tiketModel = new Tiket();
            int jumlahAktif  =
                tiketModel.hitungAktif();

            req.setAttribute("jumlahAktif",   jumlahAktif);
            req.setAttribute("kapasitasMaks", Tiket.KAPASITAS_MAKS);
            req.setAttribute("parkirPenuh",
                jumlahAktif >= Tiket.KAPASITAS_MAKS);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("kendaraan_masuk.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        String platNomor =
            req.getParameter("platNomor");
        String jenis =
            req.getParameter("jenis");

        if (platNomor == null || platNomor.isBlank() ||
                jenis == null || jenis.isBlank()) {

            req.setAttribute(
                "error",
                "Plat nomor dan jenis wajib diisi."
            );
            doGet(req, resp);
            return;
        }

        try {

            // Generate ID tiket unik
            String idTiket =
                UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();

            // Buat & simpan tiket via Model
            // cekKapasitas sudah ada di Tiket.insert()
            Tiket tiket = new Tiket(
                idTiket,
                platNomor.toUpperCase(),
                jenis
            );

            tiket.insert(); // throws TiketException jika penuh

            // Generate QR Code (isi = idTiket)
            String folderQR =
                getServletContext().getRealPath("/qr/");

            QRUtil.generateQRCode(
                idTiket,
                folderQR + idTiket + ".png"
            );

            // Kirim ke karcis.jsp
            req.setAttribute("idTiket",   idTiket);
            req.setAttribute("platNomor", platNomor.toUpperCase());
            req.setAttribute("jenis",     jenis);
            req.setAttribute(
                "qrImage",
                req.getContextPath() +
                "/qr/" + idTiket + ".png"
            );

            req.getRequestDispatcher("karcis.jsp")
               .forward(req, resp);

        } catch (TiketException e) {

            // Parkir penuh atau tiket bermasalah
            req.setAttribute("error", e.getMessage());
            req.setAttribute(
                "parkirPenuh",
                e.isParkingFull()
            );
            req.getRequestDispatcher("kendaraan_masuk.jsp")
               .forward(req, resp);

        } catch (DatabaseException e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Gagal menyimpan tiket: " +
                e.getMessage()
            );
            req.getRequestDispatcher("kendaraan_masuk.jsp")
               .forward(req, resp);

        } catch (Exception e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Gagal generate QR Code: " +
                e.getMessage()
            );
            req.getRequestDispatcher("kendaraan_masuk.jsp")
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