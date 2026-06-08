/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

/**
 *
 * @author NabilRapa
 */

public class PembayaranException extends Exception {

    public static final int PEMBAYARAN_GAGAL = 1;
    public static final int PEMBAYARAN_TIMEOUT = 2;

    private final int code;

    public PembayaranException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
} 

