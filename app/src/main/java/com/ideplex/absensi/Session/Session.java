package com.ideplex.absensi.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Context context;

    public Session(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("Absensi", context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public void setUserStatus(Boolean loggedIn, String id_user, String nama, String token){
        editor.putBoolean("loggedIn", loggedIn);
        editor.putString("id_user", id_user);
        editor.putString("nama", nama);
        editor.putString("token", token);
        editor.commit();
    }

    public String getLat() {
        return preferences.getString("lat", "-8.151507878490508");
    }

    public String getLng() {
        return preferences.getString("lng", "113.7152087383908");
    }

    public String getBaseUrl() {
        return preferences.getString("baseUrl", "api.pdesoebandi.id");
    }

    public boolean getUserLoggedIn() {
        return preferences.getBoolean("loggedIn", false);
    }

    public String getToken() {
        return preferences.getString("token", "");
    }

    public String getNip() {
        return preferences.getString("nip", "");
    }

    public String getNama() {
        return preferences.getString("nama", "");
    }

    public String getNamaBagian() {
        return preferences.getString("nama_bagian", "");
    }

    public String getBagianId() {
        return preferences.getString("bagian_id",  "");
    }

    public String getShift() {
        return preferences.getString("shift",  "");
    }

    public String getJenkel() {
        return preferences.getString("jenkel",  "");
    }
}

