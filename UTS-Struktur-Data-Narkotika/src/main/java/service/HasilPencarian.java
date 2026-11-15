package service;

import model.Putusan;


public class HasilPencarian {
    private final Putusan putusan;
    private final int indeks;
    private final int jumlahLangkah;

    public HasilPencarian(Putusan putusan, int indeks, int jumlahLangkah) {
        this.putusan = putusan;
        this.indeks = indeks;
        this.jumlahLangkah = jumlahLangkah;
    }

    public boolean isDitemukan() {
        return indeks >= 0;
    }

    public Putusan getPutusan() {
        return putusan;
    }

    public int getIndeks() {
        return indeks;
    }

    public int getJumlahLangkah() {
        return jumlahLangkah;
    }
}
