/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import exception.DatabaseException;
import exception.TiketException;
import model.Tiket;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class ValidasiTiketServlet
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

        // idTiket bisa dari:
        // - req.getAttribute (forward dari ScanKarcisServlet)
        // - req.getParameter (input manual dari form)
        String idTiket =
            (String) req.getAttribute("idTiket");

        if (idTiket == null) {
            idTiket = req.getParameter("idTiket");
        }

        if (idTiket == null || idTiket.isBlank()) {

            req.setAttribute(
                "error", "ID Tiket tidak ditemukan."
            );
            req.getRequestDispatcher("scan_karcis.jsp")
               .forward(req, resp);
            return;
        }

        try {

            // Semua logika DB di Model Tiket
            Tiket tiket = new Tiket()
                .find(idTiket.trim());

            // Validasi via interface Validatable
            if (!tiket.validasiTiket()) {
                throw new TiketException(
                    "Tiket sudah tidak aktif " +
                    "(status: " + tiket.getStatus() + ").",
                    TiketException.TIKET_NOT_ACTIVE
                );
            }

            // Kalkulasi biaya via method di Model
            long durasiMenit = tiket.hitungDurasiMenit();
            long durasiJam   = tiket.hitungDurasiJam();
            double totalBiaya = tiket.kalkulasiBiaya();

            req.setAttribute("tiket",       tiket);
            req.setAttribute("idTiket",     tiket.getIdTiket());
            req.setAttribute("platNomor",   tiket.getPlatNomor());
            req.setAttribute("jenis",       tiket.getJenis());
            req.setAttribute("durasiMenit", durasiMenit);
            req.setAttribute("durasiJam",   durasiJam);
            req.setAttribute("totalBiaya",  totalBiaya);

            req.getRequestDispatcher("pembayaran.jsp")
               .forward(req, resp);

        } catch (TiketException e) {

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("scan_karcis.jsp")
               .forward(req, resp);

        } catch (DatabaseException e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Error database: " + e.getMessage()
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