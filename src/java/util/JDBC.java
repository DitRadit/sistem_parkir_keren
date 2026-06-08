package util;

import exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * JDBC - Base class koneksi database.
 * Semua Model class extends class ini,
 * sesuai pola dari slide dosen PBO Tel-U.
 *
 * Menyediakan:
 *   - getConnection()  → koneksi ke MySQL
 *   - getData(sql)     → eksekusi SELECT
 *   - runQuery(sql)    → eksekusi INSERT/UPDATE/DELETE
 *   - message          → pesan error/info
 *
 * Kelompok PenghancurBiji - SQR Parking System
 */
public class JDBC {

    // ===== KONFIGURASI =====
    private static final String DRIVER =
        "com.mysql.jdbc.Driver";

    private static final String URL =
        "jdbc:mysql://localhost:3306/sistem_parkir_qren";

    private static final String USER = "root";
    private static final String PASS = "";

    // ===== MESSAGE (untuk error feedback) =====
    private String message = "";

    // ===== KONEKSI =====

    /**
     * Membuat koneksi baru ke database MySQL.
     * Dipanggil di setiap method yang butuh DB.
     */
    public static Connection getConnection()
            throws DatabaseException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(
                URL, USER, PASS
            );
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(
                "Driver MySQL tidak ditemukan. " +
                "Pastikan mysql-connector.jar ada di Libraries.",
                e
            );
        } catch (Exception e) {
            throw new DatabaseException(
                "Gagal terhubung ke database: " +
                e.getMessage(),
                e
            );
        }
    }

    // ===== getData: untuk query SELECT =====

    /**
     * Eksekusi query SELECT dan kembalikan ResultSet.
     * Sesuai pola slide dosen: getData(queryString)
     *
     * @param sql Query SELECT yang akan dieksekusi
     * @return ResultSet hasil query
     */
    public ResultSet getData(String sql)
            throws DatabaseException {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            return stmt.executeQuery(sql);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mengambil data: " +
                e.getMessage(),
                e
            );
        }
    }

    // ===== runQuery: untuk INSERT, UPDATE, DELETE =====

    /**
     * Eksekusi query DML (INSERT/UPDATE/DELETE).
     * Sesuai pola slide dosen: runQuery(updateString)
     *
     * @param sql Query DML yang akan dieksekusi
     */
    public void runQuery(String sql)
            throws DatabaseException {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal menjalankan query: " +
                e.getMessage(),
                e
            );
        }
    }

    // ===== MESSAGE =====

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}