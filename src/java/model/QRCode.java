package model;

public class QRCode {

    private String dataQR;
    private String localFileName;

    public QRCode(String dataQR) {
        this.dataQR = dataQR;
    }

    public String getDataQR() {
        return dataQR;
    }

    public void setDataQR(String dataQR) {
        this.dataQR = dataQR;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public void tampilkanQR() {
        System.out.println("QR Code : " + dataQR);
    }
}