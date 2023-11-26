package com.ideplex.absensi.Helpers;

public class ApiError {

    private int statusCode;
    private String error;
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
