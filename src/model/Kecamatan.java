/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author adityaseptemberiano
 */
public class Kecamatan {
    private int idKecamatan;
    private String namaKecamatan;
    private double latitude;
    private double longitude;
    private String sektorUtama;
    private String subSektor;

    // CONSTRUCTOR: Harus menerima 6 parameter dengan urutan ini
    public Kecamatan(int idKecamatan, String namaKecamatan, double latitude, double longitude, String sektorUtama, String subSektor) {
        this.idKecamatan = idKecamatan;
        this.namaKecamatan = namaKecamatan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sektorUtama = sektorUtama;
        this.subSektor = subSektor;
    }

    // GETTER: Nama method harus sama persis dengan yang dipanggil di Repository
    public int getId() {
        return idKecamatan;
    }

    public String getNamaKecamatan() {
        return namaKecamatan;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getSektorUtama() {
        return sektorUtama;
    }

    public String getSubSektor() {
        return subSektor;
    }
}