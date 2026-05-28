/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LENOVO
 */
package model;

import java.time.LocalDateTime;

public class Tiket implements Validatable {

    private String idTiket;
    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private String status;

    public Tiket(String idTiket) {
        this.idTiket = idTiket;
        this.waktuMasuk = LocalDateTime.now();
        this.status = "AKTIF";
    }

    public String getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(String idTiket) {
        this.idTiket = idTiket;
    }

    public LocalDateTime getWaktuMasuk() {
        return waktuMasuk;
    }

    public void setWaktuMasuk(LocalDateTime waktuMasuk) {
        this.waktuMasuk = waktuMasuk;
    }

    public LocalDateTime getWaktuKeluar() {
        return waktuKeluar;
    }

    public void setWaktuKeluar(LocalDateTime waktuKeluar) {
        this.waktuKeluar = waktuKeluar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean validasiTiket() {
        return idTiket != null && status.equals("AKTIF");
    }
}
