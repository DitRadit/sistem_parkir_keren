package model;

public class Mobil extends Kendaraan {

    private String tipeMobil;

    public Mobil(String platNomor, String tipeMobil) {
        super(platNomor, "Mobil");
        this.tipeMobil = tipeMobil;
    }

    public String getTipeMobil() {
        return tipeMobil;
    }

    public void setTipeMobil(String tipeMobil) {
        this.tipeMobil = tipeMobil;
    }

    @Override
    public double getTarifPerJam() {
        return Tiket.TARIF_MOBIL;
    }
}