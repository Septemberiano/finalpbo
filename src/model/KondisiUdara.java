/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author adityaseptemberiano
 */


public abstract class KondisiUdara {
    // Seluruh variabel disembunyikan secara rapat (Strict Encapsulation)
    private Kecamatan kecamatan;
    private double suhu;
    private int kelembapan;
    private double kecepatanAngin;
    private double curahHujan;
    private String deskripsiCuaca;

    public KondisiUdara(Kecamatan kecamatan, double suhu, int kelembapan, double kecepatanAngin, double curahHujan, String deskripsiCuaca) {
        this.kecamatan = kecamatan;
        this.suhu = suhu;
        this.kelembapan = kelembapan;
        this.kecepatanAngin = kecepatanAngin;
        this.curahHujan = curahHujan;
        this.deskripsiCuaca = deskripsiCuaca;
    }

    // Pintu akses resmi (Getter) untuk mengambil objek Kecamatan
    public Kecamatan getKecamatan() { 
        return kecamatan; 
    }

    public double getSuhu() { return suhu; }
    public int getKelembapan() { return kelembapan; }
    public double getKecepatanAngin() { return kecepatanAngin; }
    public double getCurahHujan() { return curahHujan; }
    public String getDeskripsiCuaca() { return deskripsiCuaca; }
    
    public abstract String getPeringatanSektoral();
}
