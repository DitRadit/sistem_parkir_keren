package model;

import exception.DatabaseException;
import exception.TiketException;
import util.JDBC;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Model Tiket - extends JDBC
 * Berisi atribut + method DB sesuai pola
 * slide dosen PBO Tel-U.
 *
 * Implements interface Validatable (dari proposal).
 *
 * Relasi OOP:
 *   - Association ke Kendaraan
 *   - Divalidasi oleh Admin
 *
 * Kelompok PenghancurBiji - SQR Parking System
 */
public class Tiket extends JDBC
        implements Validatable {

    // ===== KONSTANTA TARIF =====
    public static final double TARIF_MOTOR = 2000;
    public static final double TARIF_MOBIL = 5000;
    public static final int KAPASITAS_MAKS = 50;
    public static final int DENDA_KARCIS_HILANG = 50000;

    // ===== ATRIBUT =====
    private String        idTiket;
    private String        platNomor;
    private String        jenis;        // "Motor" atau "Mobil"
    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private String        status;       // AKTIF, SELESAI, BATAL
    private Double        totalBiaya;
    private String        snapToken;
    private String        statusBayar;  // BELUM_BAYAR, MENUNGGU, LUNAS, GAGAL

    // ===== CONSTRUCTOR =====

    public Tiket() {
        // Default constructor (wajib ada sesuai slide dosen)
    }

    public Tiket(
            String idTiket,
            String platNomor,
            String jenis) {
        this.idTiket    = idTiket;
        this.platNomor  = platNomor;
        this.jenis      = jenis;
        this.waktuMasuk = LocalDateTime.now();
        this.status     = "AKTIF";
        this.statusBayar = "BELUM_BAYAR";
    }

    // ===== IMPLEMENTASI INTERFACE Validatable =====

    @Override
    public boolean validasiTiket() {
        return idTiket != null &&
               !idTiket.isBlank() &&
               "AKTIF".equals(status);
    }

    // ===== toModel() - mapping ResultSet ke Tiket =====
    // Sesuai slide dosen hal. 20

    public Tiket toModel(ResultSet rs) {
        try {
            Tiket t = new Tiket();
            t.setIdTiket(rs.getString("id_tiket"));
            t.setPlatNomor(rs.getString("plat_nomor"));
            t.setJenis(rs.getString("jenis"));
            t.setStatus(rs.getString("status"));
            t.setStatusBayar(rs.getString("status_bayar"));

            Timestamp tsIn = rs.getTimestamp("waktu_masuk");
            if (tsIn != null) {
                t.setWaktuMasuk(tsIn.toLocalDateTime());
            }

            Timestamp tsOut = rs.getTimestamp("waktu_keluar");
            if (tsOut != null) {
                t.setWaktuKeluar(tsOut.toLocalDateTime());
            }

            double biaya = rs.getDouble("total_biaya");
            if (!rs.wasNull()) {
                t.setTotalBiaya(biaya);
            }

            t.setSnapToken(rs.getString("snap_token"));

            return t;

        } catch (Exception e) {
            setMessage(e.getMessage());
            return null;
        }
    }

    // ===== get() - ambil semua tiket aktif =====
    // Sesuai slide dosen hal. 21

    public ArrayList<Tiket> get()
            throws DatabaseException {

        ArrayList<Tiket> res = new ArrayList<>();

        ResultSet rs = getData(
            "SELECT * FROM tiket " +
            "WHERE status = 'AKTIF' " +
            "ORDER BY waktu_masuk DESC"
        );

        try {
            while (rs.next()) {
                res.add(toModel(rs));
            }
            rs.close();
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mengambil daftar tiket.",
                e
            );
        }

        return res;
    }

    // ===== find() - cari tiket berdasarkan id =====
    // Sesuai slide dosen hal. 30

    public Tiket find(String idTiket)
            throws DatabaseException,
                   TiketException {

        ResultSet rs = getData(
            "SELECT * FROM tiket " +
            "WHERE id_tiket = '" + idTiket + "'"
        );

        try {
            if (rs.next()) {
                return toModel(rs);
            }
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mencari tiket: " + idTiket,
                e
            );
        }

        throw new TiketException(
            "Tiket '" + idTiket + "' tidak ditemukan.",
            TiketException.TIKET_NOT_FOUND
        );
    }
    
    // ===== findByPlat() - cari tiket aktif berdasarkan plat nomor =====
    // Untuk kasus karcis hilang (manual checkout)

    public Tiket findByPlat(String platNomor)
            throws DatabaseException,
                   TiketException {

        ResultSet rs = getData(
            "SELECT * FROM tiket " +
            "WHERE plat_nomor = '" + platNomor + "' " +
            "AND status = 'AKTIF' " +
            "ORDER BY waktu_masuk DESC " +
            "LIMIT 1"
        );

        try {
            if (rs.next()) {
                return toModel(rs);
            }
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal mencari tiket by plat: " + platNomor,
                e
            );
        }

        throw new TiketException(
            "Tidak ada kendaraan aktif dengan plat '" + platNomor + "'.",
            TiketException.TIKET_NOT_FOUND
        );
    }

    // ===== insert() - simpan tiket baru (kendaraan masuk) =====
    // Sesuai slide dosen hal. 30

    public void insert()
            throws DatabaseException,
                   TiketException {

        // Cek kapasitas parkir terlebih dahulu
        int jumlahAktif = hitungAktif();

        if (jumlahAktif >= KAPASITAS_MAKS) {
            throw new TiketException(
                "Parkir penuh! Kapasitas maksimum " +
                KAPASITAS_MAKS + " kendaraan.",
                TiketException.PARKIR_PENUH
            );
        }

        runQuery(
            "INSERT INTO tiket " +
            "(id_tiket, plat_nomor, jenis, " +
            "waktu_masuk, status, status_bayar) " +
            "VALUES " +
            "('" + idTiket   + "', " +
            "'"  + platNomor + "', " +
            "'"  + jenis     + "', " +
            "NOW(), 'AKTIF', 'BELUM_BAYAR')"
        );
    }

    // ===== update() - update snap token & total biaya =====
    // Sesuai slide dosen hal. 30

    public void update()
            throws DatabaseException {

        runQuery(
            "UPDATE tiket " +
            "SET snap_token   = '" + snapToken   + "', " +
            "    total_biaya  = "  + totalBiaya  + ", " +
            "    status_bayar = 'MENUNGGU' " +
            "WHERE id_tiket   = '" + idTiket + "'"
        );
    }

    // ===== updateStatusBayar() - update setelah callback Midtrans =====

    public void updateStatusBayar(String newStatus)
            throws DatabaseException {

        runQuery(
            "UPDATE tiket " +
            "SET status_bayar = '" + newStatus + "', " +
            "    status       = 'SELESAI', " +
            "    waktu_keluar = NOW() " +
            "WHERE id_tiket   = '" + idTiket + "' " +
            "AND status = 'AKTIF'"
        );
    }

    // ===== checkout() - buka palang, set kendaraan keluar =====

    public void checkout()
            throws DatabaseException,
                   TiketException {

        if (!"LUNAS".equals(statusBayar)) {
            throw new TiketException(
                "Pembayaran belum lunas. " +
                "Palang tidak dapat dibuka.",
                TiketException.TIKET_NOT_ACTIVE
            );
        }

        runQuery(
            "UPDATE tiket " +
            "SET status       = 'SELESAI', " +
            "    waktu_keluar = NOW() " +
            "WHERE id_tiket   = '" + idTiket + "' " +
            "AND status = 'AKTIF'"
        );
    }

    // ===== hitungAktif() - hitung kendaraan aktif =====

    public int hitungAktif()
            throws DatabaseException {

        ArrayList<ArrayList<Object>> result =
            query(
                "SELECT COUNT(*) FROM tiket " +
                "WHERE status = 'AKTIF'"
            );

        try {
            if (!result.isEmpty()) {
                return Integer.parseInt(
                    result.get(0).get(0).toString()
                );
            }
        } catch (Exception e) {
            throw new DatabaseException(
                "Gagal menghitung tiket aktif.",
                e
            );
        }

        return 0;
    }

    // ===== query() - plain object untuk aggregasi =====
    // Sesuai slide dosen hal. 27

    public ArrayList<ArrayList<Object>> query(
            String sql)
            throws DatabaseException {

        ArrayList<ArrayList<Object>> res =
            new ArrayList<>();

        ResultSet rs = getData(sql);

        try {
            while (rs.next()) {
                res.add(toRow(rs));
            }
            rs.close();
        } catch (Exception e) {
            setMessage(e.getMessage());
            throw new DatabaseException(
                "Gagal menjalankan query.",
                e
            );
        }

        return res;
    }

    // ===== toRow() - mapping ResultSet ke ArrayList<Object> =====
    // Sesuai slide dosen hal. 26

    public ArrayList<Object> toRow(ResultSet rs) {

        ArrayList<Object> res = new ArrayList<>();
        int i = 1;

        try {
            while (true) {
                res.add(rs.getObject(i));
                i++;
            }
        } catch (Exception e) {
            // Selesai iterasi kolom
        }

        return res;
    }

    // ===== KALKULASI BIAYA =====

    public long hitungDurasiMenit() {
        LocalDateTime akhir = (waktuKeluar != null)
            ? waktuKeluar
            : LocalDateTime.now();

        return ChronoUnit.MINUTES.between(
            waktuMasuk, akhir
        );
    }

    public long hitungDurasiJam() {
        long menit = hitungDurasiMenit();
        long jam = (long) Math.ceil(menit / 60.0);
        return jam < 1 ? 1 : jam;
    }

    public double kalkulasiBiaya() {
        double tarif = "Motor".equalsIgnoreCase(jenis)
            ? TARIF_MOTOR
            : TARIF_MOBIL;

        return hitungDurasiJam() * tarif;
    }

    // ===== GETTER & SETTER =====

    public String getIdTiket() { return idTiket; }
    public void setIdTiket(String idTiket) {
        this.idTiket = idTiket;
    }

    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public void setWaktuMasuk(LocalDateTime waktuMasuk) {
        this.waktuMasuk = waktuMasuk;
    }

    public LocalDateTime getWaktuKeluar() { return waktuKeluar; }
    public void setWaktuKeluar(LocalDateTime waktuKeluar) {
        this.waktuKeluar = waktuKeluar;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(Double totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    public String getSnapToken() { return snapToken; }
    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public String getStatusBayar() { return statusBayar; }
    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    @Override
    public String toString() {
        return "Tiket{id='" + idTiket +
               "', plat='" + platNomor +
               "', status=" + status + "}";
    }
}