package com.ideplex.absensi.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shift {

    @SerializedName("early_checkin")
    @Expose
    private String earlyCheckin;
    @SerializedName("start_break")
    @Expose
    private String startBreak;
    @SerializedName("late_checkin")
    @Expose
    private String lateCheckin;
    @SerializedName("checkout_nextday")
    @Expose
    private String checkoutNextday;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("bagian")
    @Expose
    private String bagian;
    @SerializedName("waktu_server")
    @Expose
    private String waktuServer;

    public String getEarlyCheckin() {
        return earlyCheckin;
    }

    public void setEarlyCheckin(String earlyCheckin) {
        this.earlyCheckin = earlyCheckin;
    }

    public String getStartBreak() {
        return startBreak;
    }

    public void setStartBreak(String startBreak) {
        this.startBreak = startBreak;
    }

    public String getLateCheckin() {
        return lateCheckin;
    }

    public void setLateCheckin(String lateCheckin) {
        this.lateCheckin = lateCheckin;
    }

    public String getCheckoutNextday() {
        return checkoutNextday;
    }

    public void setCheckoutNextday(String checkoutNextday) {
        this.checkoutNextday = checkoutNextday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBagian() {
        return bagian;
    }

    public void setBagian(String bagian) {
        this.bagian = bagian;
    }

    public String getWaktuServer() {
        return waktuServer;
    }

    public void setWaktuServer(String waktuServer) {
        this.waktuServer = waktuServer;
    }

}
