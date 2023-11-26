package com.ideplex.absensi.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponse<T> {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<T> data = null;

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
}
