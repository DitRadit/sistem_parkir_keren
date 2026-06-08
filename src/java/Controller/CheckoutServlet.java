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

/**
 * CheckoutServlet
 * Cek status bayar LUNAS → buka palang → redirect dashboard
 */
public class CheckoutServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        String idTiket = req.getParameter("idTiket");

        try {

            // Cari tiket via Model
            Tiket tiket = new Tiket()
                .find(idTiket);

            // checkout() di Model sudah cek LUNAS
            // dan throw TiketException jika belum
            tiket.checkout();

            System.out.println(
                "[Checkout] Palang dibuka: " + idTiket
            );

            req.getSession().setAttribute(
                "sukses",
                "Kendaraan " + tiket.getPlatNomor() +
                " berhasil keluar. Palang dibuka!"
            );

            resp.sendRedirect(
                req.getContextPath() + "/dashboard"
            );

        } catch (TiketException e) {

            // Belum lunas atau tiket tidak ditemukan
            req.setAttribute("error", e.getMessage());
            req.setAttribute("idTiket", idTiket);
            req.getRequestDispatcher("qris.jsp")
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