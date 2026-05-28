/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author LENOVO
 */
public class QRCode {

    private String dataQR;

    public QRCode(String dataQR) {
        this.dataQR = dataQR;
    }

    public String getDataQR() {
        return dataQR;
    }

    public void setDataQR(String dataQR) {
        this.dataQR = dataQR;
    }

    public void tampilkanQR() {
        System.out.println("QR Code : " + dataQR);
    }
}
