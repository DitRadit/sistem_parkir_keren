/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import java.nio.file.FileSystems;
import java.nio.file.Path;



public class QRUtil {
    public static String decodeQRCode(
        InputStream imageStream)
        throws Exception {

    BufferedImage image =
            ImageIO.read(imageStream);

    BinaryBitmap bitmap =
            new BinaryBitmap(
                    new HybridBinarizer(
                            new BufferedImageLuminanceSource(image)
                    )
            );

    Result result =
            new MultiFormatReader().decode(bitmap);

    return result.getText();
}

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