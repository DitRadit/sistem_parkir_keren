/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 package Controller;

import exception.DatabaseException;
import model.Tiket;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;

public class DashboardServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req, resp)) return;

        try {

            // Semua logika DB ada di Model Tiket
            Tiket tiketModel = new Tiket();

            ArrayList<Tiket> daftarTiket =
                tiketModel.get();

            int jumlahAktif =
                tiketModel.hitungAktif();

            int slotTersedia =
                Tiket.KAPASITAS_MAKS - jumlahAktif;

            req.setAttribute("daftarTiket",   daftarTiket);
            req.setAttribute("jumlahAktif",   jumlahAktif);
            req.setAttribute("kapasitasMaks", Tiket.KAPASITAS_MAKS);
            req.setAttribute("slotTersedia",  slotTersedia);
            req.setAttribute("parkirPenuh",   slotTersedia <= 0);

        } catch (DatabaseException e) {

            e.printStackTrace();
            req.setAttribute(
                "error",
                "Gagal memuat data: " + e.getMessage()
            );
        }

        req.getRequestDispatcher("dashboard.jsp")
           .forward(req, resp);
    }

    // ===== Guard: cek session login =====
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
