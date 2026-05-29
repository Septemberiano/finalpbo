/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author adityaseptemberiano
 */
public class CuacaEkstrem extends KondisiUdara {
    
    public CuacaEkstrem(Kecamatan kec, double suh, int lembap, double angin, double hujan, String desk) {
        super(kec, suh, lembap, angin, hujan, desk);
    }

    @Override
    public String getPeringatanSektoral() {
        // Menggunakan getKecamatan() untuk mengakses objek Kecamatan yang private di parent
        // Serta menyesuaikan getNamaKecamatan() sesuai blueprint Kecamatan kita
        String infoHeader = "Kecamatan " + getKecamatan().getNamaKecamatan() + " (" + getKecamatan().getSubSektor() + ")\n";
        String alert = "⚠️ STATUS SIAGA: " + getDeskripsiCuaca().toUpperCase() + "!\n";

        // Mengambil sektor utama melalui gerbang getKecamatan()
        switch (getKecamatan().getSektorUtama()) {
            case "Agrikultur":
                alert += "Rekomendasi Agro: Lindungi komoditas pertanian. Waspada angin perusak/genangan air yang merusak akar.";
                break;
            case "Wisata":
                alert += "Rekomendasi Wisata: Tunda aktivitas luar ruangan wisatawan. Amankan properti/tenda dari badai.";
                break;
            case "Kawasan Rawan":
                alert += "🚨 ALERT RAWAN BENCANA: Evakuasi area bantaran sungai dari potensi banjir lahar!";
                break;
            default:
                alert += "Waspada cuaca buruk di wilayah Anda.";
        }
        return infoHeader + alert;
    }
}
