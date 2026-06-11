/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.Tiket;
import util.QRUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ScanKarcisServlet", urlPatterns = {"/ScanKarcisServlet"})
@MultipartConfig(
    maxFileSize    = 5 * 1024 * 1024,   // maks 5 MB per file
    maxRequestSize = 10 * 1024 * 1024   // maks 10 MB total request
)

/**
 *
 * @author NabilRapa
 */

/**
 * ScanKarcisServlet
 * Terima upload foto karcis → decode QR → forward ke CheckoutServlet
 */
public class ScanKarcisServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        req.getRequestDispatcher("scan_karcis.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        // FIX: nama field disesuaikan dengan form di scan_karcis.jsp (name="qrImage")
        Part filePart = req.getPart("qrImage");

        if (filePart == null ||
                filePart.getSize() == 0) {

            req.setAttribute(
                "error",
                "Pilih foto karcis terlebih dahulu."
            );
            req.getRequestDispatcher("scan_karcis.jsp")
               .forward(req, resp);
            return;
        }

        try {

            InputStream imageStream =
                filePart.getInputStream();

            // Decode QR dari gambar → dapat idTiket
            String idTiket =
                QRUtil.decodeQRCode(imageStream);

            if (idTiket == null || idTiket.isBlank()) {
                throw new Exception(
                    "QR Code tidak terbaca."
                );
            }

            // Forward ke CheckoutServlet untuk hitung tarif & generate QRIS
            resp.sendRedirect(req.getContextPath() + "/CheckoutServlet?idTiket=" + idTiket.trim());

        } catch (Exception e) {

            req.setAttribute(
                "error",
                "Gagal membaca QR Code: " +
                e.getMessage() +
                ". Pastikan foto jelas dan QR terlihat penuh."
            );
            req.getRequestDispatcher("scan_karcis.jsp")
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