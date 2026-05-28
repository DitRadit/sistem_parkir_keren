/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javax.servlet.*;
import javax.servlet.http.*;

import util.JDBC;
import util.QRUtil;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class KendaraanMasukServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            Connection con =
                JDBC.getConnection();

            String idTiket =
                UUID.randomUUID().toString();

            String jenis =
                req.getParameter("jenis");

            String platNomor =
                req.getParameter("platNomor");

            String sql =
                "INSERT INTO tiket " +
                "(id_tiket, plat_nomor, jenis, waktu_masuk, status) " +
                "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setString(1, idTiket);
            ps.setString(2, platNomor);
            ps.setString(3, jenis);

            ps.setTimestamp(
                4,
                Timestamp.valueOf(
                    LocalDateTime.now()
                )
            );

            ps.setString(5, "AKTIF");

            ps.executeUpdate();

            String path =
                getServletContext()
                .getRealPath("/qr/")
                + idTiket + ".png";

            QRUtil.generateQRCode(
                idTiket,
                path
            );

            req.setAttribute(
                "idTiket",
                idTiket
            );

            req.getRequestDispatcher(
                "karcis.jsp"
            ).forward(req, resp);

            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}