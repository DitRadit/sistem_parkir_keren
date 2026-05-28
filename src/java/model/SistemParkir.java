/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author LENOVO
 */
import java.util.ArrayList;

public class SistemParkir {

    private ArrayList<Kendaraan> daftarKendaraan;
    private ArrayList<Tiket> daftarTiket;
    private int kapasitasMaks;

    public SistemParkir(int kapasitasMaks) {
        this.kapasitasMaks = kapasitasMaks;
        daftarKendaraan = new ArrayList<>();
        daftarTiket = new ArrayList<>();
    }

    public ArrayList<Kendaraan> getDaftarKendaraan() {
        return daftarKendaraan;
    }

    public void setDaftarKendaraan(ArrayList<Kendaraan> daftarKendaraan) {
        this.daftarKendaraan = daftarKendaraan;
    }

    public ArrayList<Tiket> getDaftarTiket() {
        return daftarTiket;
    }

    public void setDaftarTiket(ArrayList<Tiket> daftarTiket) {
        this.daftarTiket = daftarTiket;
    }

    public int getKapasitasMaks() {
        return kapasitasMaks;
    }

    public void setKapasitasMaks(int kapasitasMaks) {
        this.kapasitasMaks = kapasitasMaks;
    }

    public boolean cekKapasitas() {
        return daftarKendaraan.size() < kapasitasMaks;
    }

    public void kendaraanMasuk(Kendaraan kendaraan, Tiket tiket) {
        daftarKendaraan.add(kendaraan);
        daftarTiket.add(tiket);
    }

    public int hitungKendaraanAktif() {
        return daftarKendaraan.size();
    }

    public double hitungBiaya(int jam) {
        return jam * 5000;
    }

    public QRPayment generatePembayaran(double jumlah) {
        return new QRPayment(jumlah);
    }
}
