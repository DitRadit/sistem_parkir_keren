/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javax.servlet.*;
import javax.servlet.http.*;

import util.JDBC;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class ValidasiTiketServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            String idTiket =
                req.getParameter("idTiket");

            Connection con =
                JDBC.getConnection();

            String sql =
                "SELECT * FROM tiket " +
                "WHERE id_tiket=?";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setString(1, idTiket);

            ResultSet rs =
                ps.executeQuery();

            if (rs.next()) {

                Timestamp masuk =
                    rs.getTimestamp(
                        "waktu_masuk"
                    );

                LocalDateTime waktuMasuk =
                    masuk.toLocalDateTime();

                Duration duration =
                    Duration.between(
                        waktuMasuk,
                        LocalDateTime.now()
                    );

                long jam =
                    duration.toHours();

                double total;

                if (jam <= 1) {

                    total = 5000;

                } else {

                    total =
                        5000 +
                        ((jam - 1) * 3000);
                }

                req.setAttribute(
                    "idTiket",
                    idTiket
                );

                req.setAttribute(
                    "total",
                    total
                );

                req.getRequestDispatcher(
                    "detail-tiket.jsp"
                ).forward(req, resp);

            } else {

                req.setAttribute(
                    "error",
                    "Tiket tidak ditemukan"
                );

                req.getRequestDispatcher(
                    "dashboard.jsp"
                ).forward(req, resp);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}