package model;

import com.midtrans.httpclient.CoreApi;
import org.json.JSONArray;
import org.json.JSONObject;
import util.MidtransUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class QRPayment extends Pembayaran {

    private String orderIdPrefix;
    private String idTiket;
    private String folderQR;
    private String fileName;

    private String orderId;
    private String transactionId;
    private QRCode qrCode;

    public QRPayment(double jumlahBayar) {
        super(jumlahBayar);
    }

    // Dipanggil sebelum prosesPembayaran(), isi konfigurasi transaksi
    public void setDetailTransaksi(String orderIdPrefix, String idTiket, String folderQR, String fileName) {
        this.orderIdPrefix = orderIdPrefix;
        this.idTiket = idTiket;
        this.folderQR = folderQR;
        this.fileName = fileName;
    }

    // ===== IMPLEMENTASI prosesPembayaran() dari abstract class Pembayaran =====
    @Override
    public void prosesPembayaran() throws Exception {

        MidtransUtil.init();

        this.orderId = orderIdPrefix + "-" + idTiket + "-" + System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> txDetail = new HashMap<>();
        txDetail.put("order_id", orderId);
        txDetail.put("gross_amount", (long) jumlahBayar);

        params.put("payment_type", "qris");
        params.put("transaction_details", txDetail);

        Map<String, Object> qrisDetail = new HashMap<>();
        qrisDetail.put("acquirer", "gopay");
        params.put("qris", qrisDetail);

        JSONObject result = CoreApi.chargeTransaction(params);

        String qrUrl = null;
        JSONArray actions = result.getJSONArray("actions");
        for (int i = 0; i < actions.length(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if ("generate-qr-code".equals(action.getString("name"))) {
                qrUrl = action.getString("url");
                break;
            }
        }

        if (qrUrl == null) {
            throw new Exception("Tidak ada QR code di response Midtrans.");
        }

        File dir = new File(folderQR);
        if (!dir.exists()) dir.mkdirs();

        File fullFile = new File(dir, fileName);
        try (InputStream in = new URL(qrUrl).openStream();
             FileOutputStream out = new FileOutputStream(fullFile)) {
            in.transferTo(out);
        }

        this.transactionId = result.getString("transaction_id");
        this.statusBayar = "MENUNGGU";

        this.qrCode = new QRCode(qrUrl);
        this.qrCode.setLocalFileName(fileName);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public QRCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QRCode qrCode) {
        this.qrCode = qrCode;
    }
}