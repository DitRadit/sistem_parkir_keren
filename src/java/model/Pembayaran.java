/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LENOVO
 */
package model;

public abstract class Pembayaran {

    protected double jumlahBayar;
    protected String statusBayar;

    public Pembayaran(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
        this.statusBayar = "BELUM BAYAR";
    }

    public double getJumlahBayar() {
        return jumlahBayar;
    }

    public void setJumlahBayar(double jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    public abstract void prosesPembayaran();
}