/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exception;

public class TiketException extends Exception {

    public static final int TIKET_NOT_FOUND = 1;
    public static final int PARKIR_PENUH = 2;
    public static final int TIKET_NOT_ACTIVE = 3;

    private final int code;

    public TiketException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
