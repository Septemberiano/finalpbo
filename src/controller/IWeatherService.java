/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package controller;

/**
 *
 * @author adityaseptemberiano
 */
public interface IWeatherService {
    /** Memuat daftar kecamatan dari basis data ke dalam UI JComboBox */
    void loadComboKecamatan();
    
    /** Rantai eksekusi utama: Query DB -> HTTP Fetch API -> Parsing -> Rule System -> UI Render */
    void eksekusiPindaiRadar();
    
    /** Menguraikan string JSON secara manual berbasis indeks matematika tanpa library luar */
    double ambilNilaiJson(String json, String key);
    
    /** Mengunduh citra satelit geospasial secara dinamis */
    void memuatCitraSatelitAsli(double lat, double lon);
}
