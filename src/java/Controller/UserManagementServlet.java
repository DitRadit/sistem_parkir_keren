package Controller;

import exception.DatabaseException;
import model.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * UserManagementServlet - CRUD Admin/Operator
 * Hanya bisa diakses oleh role ADMIN.
 *   GET  /UserManagementServlet          → tampilkan daftar user
 *   POST /UserManagementServlet?act=add  → tambah user baru
 *   POST /UserManagementServlet?act=edit → edit user
 *   POST /UserManagementServlet?act=delete → hapus user
 *
 * Kelompok PenghancurBiji - SQR Parking System
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/UserManagementServlet"})
public class UserManagementServlet extends HttpServlet {

    // ===== Cek login & role ADMIN =====
    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect("login.jsp");
            return false;
        }
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            req.getSession().setAttribute("flashError", "Akses ditolak! Hanya ADMIN yang dapat mengelola user.");
            try { resp.sendRedirect("dashboard"); } catch (Exception ignored) {}
            return false;
        }
        return true;
    }

    // ===== GET: tampilkan daftar user =====
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAdmin(req, resp)) return;

        try {
            Admin adminModel = new Admin();
            ArrayList<Admin> daftarUser = adminModel.get();
            req.setAttribute("daftarUser", daftarUser);

            // Edit mode: load data user yang akan diedit
            String editId = req.getParameter("editId");
            if (editId != null && !editId.isBlank()) {
                Admin target = adminModel.find(editId);
                req.setAttribute("editUser", target);
            }

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.setAttribute("errorPage", "Gagal memuat data user: " + e.getMessage());
        }

        req.getRequestDispatcher("user_management.jsp").forward(req, resp);
    }

    // ===== POST: handle semua aksi CRUD =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAdmin(req, resp)) return;

        String act = req.getParameter("act");
        if (act == null) act = "";

        switch (act) {
            case "add":
                handleAdd(req, resp);
                break;
            case "edit":
                handleEdit(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            default:
                resp.sendRedirect("UserManagementServlet");
        }
    }

    // ===== TAMBAH USER =====
    private void handleAdd(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String nama     = req.getParameter("nama");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String roleStr  = req.getParameter("role");

        // Validasi
        if (nama == null || nama.isBlank() ||
            username == null || username.isBlank() ||
            password == null || password.isBlank() ||
            roleStr == null || roleStr.isBlank()) {
            req.getSession().setAttribute("flashError", "Semua field wajib diisi!");
            resp.sendRedirect("UserManagementServlet");
            return;
        }

        try {
            // Cek duplikat username
            if (isUsernameTaken(username, null)) {
                req.getSession().setAttribute("flashError", "Username '" + username + "' sudah digunakan!");
                resp.sendRedirect("UserManagementServlet");
                return;
            }

            String idAdmin = "ADM-" + UUID.randomUUID()
                    .toString().replace("-", "").substring(0, 8).toUpperCase();

            Admin newAdmin = new Admin(
                idAdmin,
                nama.trim(),
                username.trim(),
                password,
                Admin.Role.valueOf(roleStr)
            );
            newAdmin.insert();

            req.getSession().setAttribute("flashSuccess",
                "User '" + nama + "' berhasil ditambahkan sebagai " + roleStr + "!");
            resp.sendRedirect("UserManagementServlet");

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Gagal menambah user: " + e.getMessage());
            resp.sendRedirect("UserManagementServlet");
        }
    }

    // ===== EDIT USER =====
    private void handleEdit(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String idAdmin  = req.getParameter("idAdmin");
        String nama     = req.getParameter("nama");
        String username = req.getParameter("username");
        String password = req.getParameter("password"); // boleh kosong = tidak ganti password
        String roleStr  = req.getParameter("role");

        if (idAdmin == null || idAdmin.isBlank() ||
            nama == null || nama.isBlank() ||
            username == null || username.isBlank() ||
            roleStr == null || roleStr.isBlank()) {
            req.getSession().setAttribute("flashError", "Data tidak lengkap untuk update!");
            resp.sendRedirect("UserManagementServlet");
            return;
        }

        // Cek sesi yang login agar tidak hapus role ADMIN diri sendiri
        HttpSession session = req.getSession(false);
        String currentAdminId = (String) session.getAttribute("adminId");

        try {
            // Cek duplikat username (kecuali milik sendiri)
            if (isUsernameTaken(username, idAdmin)) {
                req.getSession().setAttribute("flashError", "Username '" + username + "' sudah digunakan user lain!");
                resp.sendRedirect("UserManagementServlet?editId=" + idAdmin);
                return;
            }

            Admin adminModel = new Admin();
            Admin existing = adminModel.find(idAdmin);
            if (existing == null) {
                req.getSession().setAttribute("flashError", "User tidak ditemukan!");
                resp.sendRedirect("UserManagementServlet");
                return;
            }

            existing.setNama(nama.trim());
            existing.setUsername(username.trim());
            existing.setRole(Admin.Role.valueOf(roleStr));

            // Ganti password hanya jika diisi
            if (password != null && !password.isBlank()) {
                existing.setPassword(password);
            } else {
                existing.setPassword(""); // kosong = tidak update password (ditangani di update())
            }

            existing.update();

            req.getSession().setAttribute("flashSuccess", "Data user '" + nama + "' berhasil diperbarui!");
            resp.sendRedirect("UserManagementServlet");

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Gagal update user: " + e.getMessage());
            resp.sendRedirect("UserManagementServlet");
        }
    }

    // ===== HAPUS USER =====
    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String idAdmin = req.getParameter("idAdmin");
        HttpSession session = req.getSession(false);
        String currentAdminId = (String) session.getAttribute("adminId");

        if (idAdmin == null || idAdmin.isBlank()) {
            req.getSession().setAttribute("flashError", "ID user tidak valid!");
            resp.sendRedirect("UserManagementServlet");
            return;
        }

        // Cegah hapus akun sendiri
        if (idAdmin.equals(currentAdminId)) {
            req.getSession().setAttribute("flashError", "Tidak bisa menghapus akun yang sedang login!");
            resp.sendRedirect("UserManagementServlet");
            return;
        }

        try {
            Admin adminModel = new Admin();
            Admin target = adminModel.find(idAdmin);
            if (target == null) {
                req.getSession().setAttribute("flashError", "User tidak ditemukan!");
                resp.sendRedirect("UserManagementServlet");
                return;
            }

            adminModel.delete(idAdmin);
            req.getSession().setAttribute("flashSuccess", "User '" + target.getNama() + "' berhasil dihapus!");
            resp.sendRedirect("UserManagementServlet");

        } catch (DatabaseException e) {
            e.printStackTrace();
            req.getSession().setAttribute("flashError", "Gagal menghapus user: " + e.getMessage());
            resp.sendRedirect("UserManagementServlet");
        }
    }

    // ===== HELPER: cek duplikat username =====
    private boolean isUsernameTaken(String username, String excludeId)
            throws DatabaseException {
        Admin adminModel = new Admin();
        ArrayList<Admin> semua = adminModel.get();
        for (Admin a : semua) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                if (excludeId == null || !a.getIdAdmin().equals(excludeId)) {
                    return true;
                }
            }
        }
        return false;
    }
}