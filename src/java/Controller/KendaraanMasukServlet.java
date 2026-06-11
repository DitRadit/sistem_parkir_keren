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
import java.io.File;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "KendaraanMasukServlet", urlPatterns = {"/KendaraanMasukServlet"})
public class KendaraanMasukServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        try {
            Tiket tiketModel = new Tiket();
            int jumlahAktif  = tiketModel.hitungAktif();

            req.setAttribute("jumlahAktif",   jumlahAktif);
            req.setAttribute("kapasitasMaks", Tiket.KAPASITAS_MAKS);
            req.setAttribute("parkirPenuh",   jumlahAktif >= Tiket.KAPASITAS_MAKS);

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat status kapasitas parkir.");
        }

        req.getRequestDispatcher("kendaraan_masuk.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        String platNomor = req.getParameter("platNomor");
        String jenis     = req.getParameter("jenis");

        if (platNomor == null || platNomor.isBlank() ||
                jenis == null || jenis.isBlank()) {

            req.setAttribute("error", "Plat nomor dan jenis wajib diisi.");
            doGet(req, resp);
            return;
        }

        try {
            // 1. Generate ID tiket unik (12 karakter)
            String idTiket = UUID.randomUUID()
                                 .toString()
                                 .replace("-", "")
                                 .substring(0, 12)
                                 .toUpperCase();

            // 2. Simpan tiket ke DB
            Tiket tiket = new Tiket(
                idTiket,
                platNomor.toUpperCase().trim(),
                jenis
            );
            tiket.insert();

            // 3. FIX: Resolve folder /qr/ dengan fallback aman
            String webRoot = getServletContext().getRealPath("/");
            if (webRoot == null) {
                webRoot = System.getProperty("catalina.home") + "/webapps/ROOT/";
            }
            String folderQR = webRoot + "qr";

            File directory = new File(folderQR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 4. Generate QR Code ke folder
            String fullPathQR = folderQR + File.separator + idTiket + ".png";
            QRUtil.generateQRCode(idTiket, fullPathQR);

            // 5. Kirim data ke karcis.jsp
            req.setAttribute("idTiket",   idTiket);
            req.setAttribute("platNomor", platNomor.toUpperCase().trim());
            req.setAttribute("jenis",     jenis);
            req.setAttribute("qrImage",   req.getContextPath() + "/qr/" + idTiket + ".png");

            req.getRequestDispatcher("karcis.jsp").forward(req, resp);

        } catch (TiketException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("parkirPenuh", e.isParkingFull());
            doGet(req, resp);

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal menyimpan transaksi ke database: " + e.getMessage());
            doGet(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Sistem gagal generate QR Code: " + e.getMessage());
            doGet(req, resp);
        }
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