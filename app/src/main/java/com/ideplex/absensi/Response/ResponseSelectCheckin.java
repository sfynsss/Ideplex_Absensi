package com.ideplex.absensi.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ideplex.absensi.Table.Shift;

import java.util.List;

public class ResponseSelectCheckin<T> {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<T> data;
    @SerializedName("durasi")
    @Expose
    private String durasi;
    @SerializedName("shift")
    @Expose
    private Shift shift;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

}
