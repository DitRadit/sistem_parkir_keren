/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LENOVO
 */
package model;

public class QRPayment extends Pembayaran {

    private QRCode qrCode;

    public QRPayment(double jumlahBayar) {
        super(jumlahBayar);
    }

    public void generateQR() {
        this.qrCode = new QRCode(
            "QR-" + jumlahBayar
        );
    }

    public QRCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QRCode qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public void prosesPembayaran() {
        this.statusBayar = "LUNAS";
    }
}
