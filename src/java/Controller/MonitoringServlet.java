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

/**
 * MonitoringServlet
 * Polling JSON dari qris.jsp untuk cek status bayar
 */
public class MonitoringServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String idTiket = req.getParameter("idTiket");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (idTiket == null || idTiket.isBlank()) {
            resp.getWriter().write(
                "{\"statusBayar\":\"ERROR\"}"
            );
            return;
        }

        try {

            // Cari tiket via Model
            Tiket tiket = new Tiket()
                .find(idTiket.trim());

            String statusBayar =
                tiket.getStatusBayar();

            if (statusBayar == null) {
                statusBayar = "BELUM_BAYAR";
            }

            resp.getWriter().write(
                "{\"statusBayar\":\"" +
                statusBayar + "\"}"
            );

        } catch (Exception e) {

            // Tiket tidak ditemukan atau DB error
            resp.getWriter().write(
                "{\"statusBayar\":\"ERROR\"}"
            );
        }
    }
}