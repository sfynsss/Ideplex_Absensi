package com.ideplex.absensi.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ApiError;
import com.ideplex.absensi.Helpers.ErrorUtils;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Response.BaseResponse2;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.JadwalHariIni;
import com.ideplex.absensi.Table.Jarak;
import com.ideplex.absensi.Table.Presensi;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterMukaActivity extends AppCompatActivity {

    LinearLayout btn_absen_wajah, container_image;
    Button btn_simpan;
    EditText username;
    ImageView btn_back;
    ImageView img_preview;

    Session session;
    Api api;
    Call<BaseResponse> registerMuka;
    String foto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_muka);

        btn_back = findViewById(R.id.btn_back);
        btn_absen_wajah = findViewById(R.id.btn_absen_wajah);
        btn_simpan = findViewById(R.id.btn_simpan);
        username = findViewById(R.id.username);
        container_image = findViewById(R.id.container_image);
        img_preview = findViewById(R.id.img_preview);

        session = new Session(RegisterMukaActivity.this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_absen_wajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(RegisterMukaActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 102);
                    } else {
                        ActivityCompat.requestPermissions(RegisterMukaActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmp_username = username.getText().toString();

                if (TextUtils.isEmpty(tmp_username) || tmp_username == "") {
                    Toast.makeText(RegisterMukaActivity.this, "Masukkan Username Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(foto) || foto == "") {
                    Toast.makeText(RegisterMukaActivity.this, "Masukkan Foto Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    registerMuka = api.registerMuka(tmp_username+"", foto+"");
                    registerMuka.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterMukaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(RegisterMukaActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(RegisterMukaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 102);
        } else {
            Toast.makeText(RegisterMukaActivity.this, "Butuh akses kamera.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                foto = imageToString(photo);
                img_preview.setImageBitmap(photo);
            }
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}