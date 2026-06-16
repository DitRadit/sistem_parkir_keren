package model;

public abstract class Kendaraan {

    private String platNomor;
    private String jenis;

    public Kendaraan(String platNomor, String jenis) {
        this.platNomor = platNomor;
        this.jenis = jenis;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public abstract double getTarifPerJam();

    public static Kendaraan dariJenis(String platNomor, String jenis) {
        if ("Mobil".equalsIgnoreCase(jenis)) {
            return new Mobil(platNomor, "Standar");
        } else {
            return new Motor(platNomor, "Standar");
        }
    }
}