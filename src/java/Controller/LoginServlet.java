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

public class LoginServlet
        extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String username =
            req.getParameter("username");

        String password =
            req.getParameter("password");

        try {

            Connection con =
                JDBC.getConnection();

            String sql =
                "SELECT * FROM admin " +
                "WHERE username=? " +
                "AND password=?";

            PreparedStatement ps =
                con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs =
                ps.executeQuery();

            if (rs.next()) {

                HttpSession session =
                    req.getSession();

                session.setAttribute(
                    "admin",
                    rs.getString("nama")
                );

                resp.sendRedirect(
                    "dashboard"
                );

            } else {

                req.setAttribute(
                    "error",
                    "Login gagal"
                );

                req.getRequestDispatcher(
                    "login.jsp"
                ).forward(req, resp);
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}