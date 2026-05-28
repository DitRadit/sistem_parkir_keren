/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRUtil {

    public static void generateQRCode(
            String text,
            String path)
            throws Exception {

        BitMatrix matrix =
                new MultiFormatWriter().encode(
                        text,
                        BarcodeFormat.QR_CODE,
                        300,
                        300
                );

        Path qrPath =
                FileSystems.getDefault()
                        .getPath(path);

        MatrixToImageWriter.writeToPath(
                matrix,
                "PNG",
                qrPath
        );
    }
}