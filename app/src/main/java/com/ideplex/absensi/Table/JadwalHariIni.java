package com.ideplex.absensi.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JadwalHariIni {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("id_shift")
    @Expose
    private Integer idShift;
    @SerializedName("id_karyawan")
    @Expose
    private Integer idKaryawan;
    @SerializedName("jadwal")
    @Expose
    private String jadwal;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("shift")
    @Expose
    private String shift;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("early_checkin")
    @Expose
    private String earlyCheckin;
    @SerializedName("checkin")
    @Expose
    private String checkin;
    @SerializedName("late_checkin")
    @Expose
    private String lateCheckin;
    @SerializedName("start_break")
    @Expose
    private String startBreak;
    @SerializedName("end_break")
    @Expose
    private String endBreak;
    @SerializedName("early_checkout")
    @Expose
    private String earlyCheckout;
    @SerializedName("checkout")
    @Expose
    private String checkout;
    @SerializedName("ontime_checkout")
    @Expose
    private String ontimeCheckout;
    @SerializedName("updated_by")
    @Expose
    private Integer updatedBy;
    @SerializedName("checkout_nextday")
    @Expose
    private Integer checkoutNextday;
    @SerializedName("id_bagian")
    @Expose
    private Integer idBagian;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdShift() {
        return idShift;
    }

    public void setIdShift(Integer idShift) {
        this.idShift = idShift;
    }

    public Integer getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(Integer idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getJadwal() {
        return jadwal;
    }

    public void setJadwal(String jadwal) {
        this.jadwal = jadwal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEarlyCheckin() {
        return earlyCheckin;
    }

    public void setEarlyCheckin(String earlyCheckin) {
        this.earlyCheckin = earlyCheckin;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getLateCheckin() {
        return lateCheckin;
    }

    public void setLateCheckin(String lateCheckin) {
        this.lateCheckin = lateCheckin;
    }

    public String getStartBreak() {
        return startBreak;
    }

    public void setStartBreak(String startBreak) {
        this.startBreak = startBreak;
    }

    public String getEndBreak() {
        return endBreak;
    }

    public void setEndBreak(String endBreak) {
        this.endBreak = endBreak;
    }

    public String getEarlyCheckout() {
        return earlyCheckout;
    }

    public void setEarlyCheckout(String earlyCheckout) {
        this.earlyCheckout = earlyCheckout;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public String getOntimeCheckout() {
        return ontimeCheckout;
    }

    public void setOntimeCheckout(String ontimeCheckout) {
        this.ontimeCheckout = ontimeCheckout;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getCheckoutNextday() {
        return checkoutNextday;
    }

    public void setCheckoutNextday(Integer checkoutNextday) {
        this.checkoutNextday = checkoutNextday;
    }

    public Integer getIdBagian() {
        return idBagian;
    }

    public void setIdBagian(Integer idBagian) {
        this.idBagian = idBagian;
    }

}
