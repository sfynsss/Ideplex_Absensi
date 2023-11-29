package com.ideplex.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ApiError;
import com.ideplex.absensi.Helpers.ErrorUtils;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Response.BaseResponse2;
import com.ideplex.absensi.Response.ResponseSelectCheckin;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.Absen;
import com.ideplex.absensi.Table.Presensi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsensiBerhasilActivity extends AppCompatActivity {

    Button btn_home;
    TextView tipe, jam_absen, wib;

    Session session;
    Api api;
    Call<BaseResponse<Absen>> absen;
    Call<ResponseSelectCheckin<Presensi>> getKehadiran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi_berhasil);

        tipe = findViewById(R.id.tipe);
        jam_absen = findViewById(R.id.jam_absen);
        btn_home = findViewById(R.id.btn_home);
        wib = findViewById(R.id.wib);

        if (getIntent().getStringExtra("tipe").equals("masuk")) {
            tipe.setText("DATANG");
            wib.setVisibility(View.VISIBLE);
            getAbsen("1");
        } else if (getIntent().getStringExtra("tipe").equals("pulang")) {
            tipe.setText("PULANG");
            wib.setVisibility(View.VISIBLE);
            getAbsen("2");
        } else {
            tipe.setText("Dinas Luar");
            wib.setVisibility(View.VISIBLE);
            getAbsen("1");
        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AbsensiBerhasilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getAbsen(String sts){
        session = new Session(AbsensiBerhasilActivity.this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

//        absen = api.getAbsen(sts);
//        absen.enqueue(new Callback<BaseResponse<Absen>>() {
//            @Override
//            public void onResponse(Call<BaseResponse<Absen>> call, Response<BaseResponse<Absen>> response) {
//                if (response.isSuccessful()) {
//                    jam_absen.setText(response.body().getData().get(0).getScanDate().substring(11));
//                } else {
//                    ApiError apiError = ErrorUtils.parseError(response);
//                    Toast.makeText(AbsensiBerhasilActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse<Absen>> call, Throwable t) {
//                Toast.makeText(AbsensiBerhasilActivity.this, "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
        getKehadiran = api.getKehadiran();
        getKehadiran.enqueue(new Callback<ResponseSelectCheckin<Presensi>>() {
            @Override
            public void onResponse(Call<ResponseSelectCheckin<Presensi>> call, Response<ResponseSelectCheckin<Presensi>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        if (sts.equals("1")) {
                            jam_absen.setText(response.body().getData().get(0).getCheckin().substring(11));
                        } else if (sts.equals("2")) {
                            jam_absen.setText(response.body().getData().get(0).getCheckout().substring(11));
                        }
                    }
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(AbsensiBerhasilActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSelectCheckin<Presensi>> call, Throwable t) {
                Toast.makeText(AbsensiBerhasilActivity.this, "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}