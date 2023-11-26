package com.ideplex.absensi.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Absen {

    @SerializedName("att_id")
    @Expose
    private Integer attId;
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("scan_date")
    @Expose
    private String scanDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("jam_kerja_id")
    @Expose
    private String jamKerjaId;
    @SerializedName("bagian_id")
    @Expose
    private String bagianId;
    @SerializedName("id_finger")
    @Expose
    private String idFinger;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("foto_wajah")
    @Expose
    private String fotoWajah;
    @SerializedName("dinas_luar")
    @Expose
    private String dinasLuar;

    public Integer getAttId() {
        return attId;
    }

    public void setAttId(Integer attId) {
        this.attId = attId;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJamKerjaId() {
        return jamKerjaId;
    }

    public void setJamKerjaId(String jamKerjaId) {
        this.jamKerjaId = jamKerjaId;
    }

    public String getBagianId() {
        return bagianId;
    }

    public void setBagianId(String bagianId) {
        this.bagianId = bagianId;
    }

    public String getIdFinger() {
        return idFinger;
    }

    public void setIdFinger(String idFinger) {
        this.idFinger = idFinger;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getFotoWajah() {
        return fotoWajah;
    }

    public void setFotoWajah(String fotoWajah) {
        this.fotoWajah = fotoWajah;
    }

    public String getDinasLuar() {
        return dinasLuar;
    }

    public void setDinasLuar(String dinasLuar) {
        this.dinasLuar = dinasLuar;
    }

}
