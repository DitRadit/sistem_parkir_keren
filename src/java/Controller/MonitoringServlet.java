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

public class MonitoringServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            Connection con =
                JDBC.getConnection();

            String sql =
                "SELECT COUNT(*) total " +
                "FROM tiket " +
                "WHERE status='AKTIF'";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ResultSet rs =
                ps.executeQuery();

            if (rs.next()) {

                req.setAttribute(
                    "jumlah",
                    rs.getInt("total")
                );
            }

            req.getRequestDispatcher(
                "dashboard.jsp"
            ).forward(req, resp);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}