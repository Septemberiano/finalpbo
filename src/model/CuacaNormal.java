/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author adityaseptemberiano
 */


public class CuacaNormal extends KondisiUdara {
    
    public CuacaNormal(Kecamatan kecamatan, double suhu, int kelembapan, double kecepatanAngin, double curahHujan, String deskripsiCuaca) {
        super(kecamatan, suhu, kelembapan, kecepatanAngin, curahHujan, deskripsiCuaca);
    }

    @Override
    public String getPeringatanSektoral() {
        // Menggunakan getKecamatan() untuk mengambil data objek Kecamatan
        return "📡 [STATUS CORE]: SEKTOR " + getKecamatan().getSektorUtama().toUpperCase() + 
               " (" + getKecamatan().getSubSektor() + ") AMAN.\n" +
               "Logika Sistem: Parameter telemetri di bawah ambang batas bahaya.\n" +
               "Rekomendasi: Aktivitas agro/event dapat berjalan normal.";
    }
}
