package model;

import exception.AuthException;
import exception.DatabaseException;
import util.JDBC;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Model Admin - extends JDBC
 * Berisi atribut, toModel(), get(), find(),
 * insert(), update(), delete() sesuai pola
 * slide dosen PBO Tel-U.
 *
 * Relasi OOP:
 *   - Memiliki RBAC: ADMIN dan OPERATOR
 *   - Association ke Tiket (untuk validasi)
 *
 * Kelompok PenghancurBiji - SQR Parking System
 */
public class Admin extends JDBC {

    // ===== ENUM ROLE =====
    public enum Role {
        ADMIN, OPERATOR
    }

    // ===== ATRIBUT =====
    private String idAdmin;
    private String nama;
    private String username;
    private String password;
    private Role   role;

    // ===== CONSTRUCTOR =====

    public Admin() {
        // Default constructor (wajib ada sesuai slide dosen)
    }

    public Admin(
            String idAdmin,
            String nama,
            String username,
            String password,
            Role role) {
        this.idAdmin  = idAdmin;
        this.nama     = nama;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // ===== toModel() - mapping ResultSet ke Admin =====
    // Sesuai slide dosen hal. 20

    public Admin toModel(ResultSet rs) {
        try {
            return new Admin(
                rs.getString("id_admin"),
                rs.getString("nama"),
                rs.getString("username"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role"))
            );
        } catch (Exception e) {
            setMessage(e.getMessage());
            return null;
        }
    }

    // ===== get() - ambil semua admin =====
    // Sesuai slide dosen hal. 21

    public ArrayList<Admin> get()
            throws DatabaseException {

        ArrayList<Admin> res = new ArrayList<>();

        ResultSet rs = getData(
            "SELECT * FROM admin"
        );

        try {
            while (rs.next()) {
                res.add(toModel(rs));
            }
            rs.close();
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mengambil data admin.",
                e
            );
        }

        return res;
    }

    // ===== find() - cari admin berdasarkan id =====
    // Sesuai slide dosen hal. 30

    public Admin find(String idAdmin)
            throws DatabaseException {

        ResultSet rs = getData(
            "SELECT * FROM admin " +
            "WHERE id_admin = '" + idAdmin + "'"
        );

        try {
            if (rs.next()) {
                return toModel(rs);
            }
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mencari admin: " + idAdmin,
                e
            );
        }

        return null;
    }

    // ===== login() - autentikasi admin =====

    public Admin login(
            String username,
            String password)
            throws DatabaseException,
                   AuthException {

        ResultSet rs = getData(
            "SELECT * FROM admin " +
            "WHERE username = '" + username + "' " +
            "AND password = '" + password + "'"
        );

        try {
            if (rs.next()) {
                return toModel(rs);
            }
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal proses login.",
                e
            );
        }

        // Username/password tidak cocok
        throw new AuthException(
            "Username atau password salah."
        );
    }

    // ===== insert() - tambah admin baru =====
    // Sesuai slide dosen hal. 30

    public void insert()
            throws DatabaseException {

        runQuery(
            "INSERT INTO admin " +
            "(id_admin, nama, username, " +
            "password, role) VALUES " +
            "('" + idAdmin  + "', " +
            "'"  + nama     + "', " +
            "'"  + username + "', " +
            "'"  + password + "', " +
            "'"  + role.name() + "')"
        );
    }

    // ===== update() - update data admin =====

    public void update()
            throws DatabaseException {

        runQuery(
            "UPDATE admin SET " +
            "nama = '"     + nama     + "', " +
            "username = '" + username + "', " +
            (password != null && !password.isEmpty()
                ? "password = '" + password + "', "
                : "") +
            "role = '"     + role.name() + "' " +
            "WHERE id_admin = '" + idAdmin + "'"
        );
    }

    // ===== delete() - hapus admin berdasarkan id =====

    public void delete(String idAdmin)
            throws DatabaseException {

        runQuery(
            "DELETE FROM admin " +
            "WHERE id_admin = '" + idAdmin + "'"
        );
    }

    // ===== GETTER & SETTER =====

    public String getIdAdmin() { return idAdmin; }
    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() { return role; }
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Admin{id='" + idAdmin +
               "', nama='" + nama +
               "', role=" + role + "}";
    }
}