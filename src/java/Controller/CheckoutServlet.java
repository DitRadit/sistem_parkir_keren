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
import java.time.LocalDateTime;

public class CheckoutServlet
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
                "SELECT * FROM pembayaran " +
                "WHERE id_tiket=?";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setString(1, idTiket);

            ResultSet rs =
                ps.executeQuery();

            if (rs.next()) {

                String status =
                    rs.getString(
                        "status_bayar"
                    );

                if (status.equals("LUNAS")) {

                    String update =
                        "UPDATE tiket " +
                        "SET waktu_keluar=?, " +
                        "status='SELESAI' " +
                        "WHERE id_tiket=?";

                    PreparedStatement ps2 =
                        con.prepareStatement(
                            update
                        );

                    ps2.setTimestamp(
                        1,
                        Timestamp.valueOf(
                            LocalDateTime.now()
                        )
                    );

                    ps2.setString(
                        2,
                        idTiket
                    );

                    ps2.executeUpdate();

                    req.setAttribute(
                        "message",
                        "Palang terbuka"
                    );

                    req.getRequestDispatcher(
                        "success.jsp"
                    ).forward(req, resp);

                } else {

                    req.setAttribute(
                        "error",
                        "Belum bayar"
                    );

                    req.getRequestDispatcher(
                        "dashboard.jsp"
                    ).forward(req, resp);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}