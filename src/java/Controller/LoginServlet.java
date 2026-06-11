/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import exception.AuthException;
import exception.DatabaseException;
import model.Admin;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet; 

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})

public class LoginServlet
        extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        // Kalau sudah login, langsung ke dashboard
        if (req.getSession(false) != null &&
                req.getSession(false)
                   .getAttribute("admin") != null) {

            resp.sendRedirect("dashboard");
            return;
        }

        req.getRequestDispatcher("login.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        String username =
            req.getParameter("username");
        String password =
            req.getParameter("password");

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {

            req.setAttribute(
                "error",
                "Username dan password wajib diisi."
            );
            req.getRequestDispatcher("login.jsp")
               .forward(req, resp);
            return;
        }

        try {

            // Autentikasi via Model Admin
            // Logika DB ada di model, bukan di sini
            Admin admin = new Admin()
                .login(username.trim(), password.trim());

            // Login berhasil: simpan ke session
            req.getSession(true)
               .setAttribute("admin", admin.getNama());

            req.getSession()
               .setAttribute("role", admin.getRole().name());

            resp.sendRedirect("dashboard");

        } catch (AuthException e) {

            // Login gagal (username/password salah)
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("login.jsp")
               .forward(req, resp);

        } catch (DatabaseException e) {

            // Error koneksi database
            e.printStackTrace();
            req.setAttribute(
                "error",
                "Terjadi masalah pada server database. " +
                "Coba lagi nanti."
            );
            req.getRequestDispatcher("login.jsp")
               .forward(req, resp);
        }
    }
}