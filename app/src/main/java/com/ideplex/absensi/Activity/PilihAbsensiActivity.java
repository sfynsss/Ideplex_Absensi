package com.ideplex.absensi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class PilihAbsensiActivity extends AppCompatActivity {

    LinearLayout btn_absen_wajah, btn_qr_code;
    ImageView btn_back;

    DateFormat dateFormat, dateFormat1;
    Date date;

    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
    Call<BaseResponse> absenWajah;
    Call<BaseResponse2<Presensi>> checkin;
    Call<BaseResponse2<Presensi>> checkout;
    Call<BaseResponse> absenScanQr;
    Call<BaseResponse> cekQr;
    Call<Jarak> getJarak;
    Call<BaseResponse> getSettingJarak;

    ArrayList<String> shift = new ArrayList<>();
    ArrayList<String> jam_mulai = new ArrayList<>();
    ArrayList<String> jam_selesai = new ArrayList<>();

    String jadwal_id, jam_kerja_id, tipe, jarak_tmp, laporan, upload, id_presensi;

    String latitude = "0";
    String longitude = "0";
    String dest = "";
    int tmp_jarak = 0;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) PilihAbsensiActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                latitude = String.valueOf(location1.getLatitude());
                                longitude = String.valueOf(location1.getLongitude());
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    dest = latitude + "," + longitude;
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_absensi);

        btn_back = findViewById(R.id.btn_back);
        btn_absen_wajah = findViewById(R.id.btn_absen_wajah);
        btn_qr_code = findViewById(R.id.btn_qr_code);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        dateFormat1 = new SimpleDateFormat("H:m:s");
        date = new Date();

        session = new Session(PilihAbsensiActivity.this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PilihAbsensiActivity.this);

        Log.d("TAG", "onCreate token: "+session.getToken());

        tipe = getIntent().getStringExtra("tipe");
        jarak_tmp = getIntent().getStringExtra("jarak");
        laporan = getIntent().getStringExtra("laporan");
        upload = getIntent().getStringExtra("upload");
        id_presensi = getIntent().getStringExtra("id_presensi");
        setJarak();

        if (ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(PilihAbsensiActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 105);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_absen_wajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(tmp_jarak);
                String origin = session.getLat() + "," + session.getLng();
                try {
                    if (ActivityCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 102);
                    } else {
                        ActivityCompat.requestPermissions(PilihAbsensiActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PilihAbsensiActivity.this, "Sedang Maintance !!!", Toast.LENGTH_SHORT);
//                System.out.println(tmp_jarak);
//                String origin = session.getLat() + "," + session.getLng();
//                if (ContextCompat.checkSelfPermission(PilihAbsensiActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(PilihAbsensiActivity.this, new String[]{Manifest.permission.CAMERA}, 50);
//                }
//                //ask for authorisation
//                else {
//                    Intent i = new Intent(PilihAbsensiActivity.this, QrScanner.class);
//                    startActivityForResult(i, 1);
//                }
            }
        });
    }

    public void setJarak() {
        getSettingJarak = api.getSettingJarak();
        getSettingJarak.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    tmp_jarak = Integer.parseInt(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

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
            Toast.makeText(PilihAbsensiActivity.this, "Butuh akses kamera.", Toast.LENGTH_SHORT);
        }

        if (requestCode == 105 && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText(PilihAbsensiActivity.this, "Butuh akses lokasi.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                int sts = 0;
                if (tipe.equals("masuk")) {
                    sts = 1;
                    checkin = api.checkin();
                    checkin.enqueue(new Callback<BaseResponse2<Presensi>>() {
                        @Override
                        public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                                intent.putExtra("tipe", tipe);
                                startActivity(intent);
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                            Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    sts = 2;
                    checkout = api.checkout(laporan, upload, id_presensi);
                    checkout.enqueue(new Callback<BaseResponse2<Presensi>>() {
                        @Override
                        public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                                intent.putExtra("tipe", tipe);
                                startActivity(intent);
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                            Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else if (requestCode == 1) {
            if (resultCode == 101) {
//                Toast.makeText(PilihAbsensiActivity.this, data.getExtras().get("content").toString(), Toast.LENGTH_SHORT).show();
                SpotsDialog dialog = (SpotsDialog) new SpotsDialog.Builder().setContext(PilihAbsensiActivity.this).build();
                dialog.show();
                int sts = 0;
                if (tipe.equals("masuk")) {
                    sts = 1;
                    checkin = api.checkin();
                    checkin.enqueue(new Callback<BaseResponse2<Presensi>>() {
                        @Override
                        public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                                intent.putExtra("tipe", tipe);
                                startActivity(intent);
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                            Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    sts = 2;
                    checkout = api.checkout(laporan, upload, id_presensi);
                    checkout.enqueue(new Callback<BaseResponse2<Presensi>>() {
                        @Override
                        public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
                                intent.putExtra("tipe", tipe);
                                startActivity(intent);
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                            Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
//                cekQr = api.cekQr(data.getExtras().get("content").toString());
//                cekQr.enqueue(new Callback<BaseResponse>() {
//                    @Override
//                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                        if (response.isSuccessful()) {
//                            int sts = 0;
//                            if (tipe.equals("masuk")) {
//                                sts = 1;
//                            } else {
//                                sts = 2;
//                            }
//                            absenScanQr = api.absenScanQr(sts + "", jadwal_id + "", jam_kerja_id + "", session.getBagianId() + "", latitude, longitude, "", jarak_tmp);
//                            absenScanQr.enqueue(new Callback<BaseResponse>() {
//                                @Override
//                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                                    if (response.isSuccessful()) {
//                                        Intent intent = new Intent(PilihAbsensiActivity.this, AbsensiBerhasilActivity.class);
//                                        intent.putExtra("tipe", tipe);
//                                        startActivity(intent);
//                                        finish();
//                                        dialog.dismiss();
//                                    } else {
//                                        ApiError apiError = ErrorUtils.parseError(response);
//                                        Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
//                                        dialog.dismiss();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<BaseResponse> call, Throwable t) {
//                                    Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                                    dialog.dismiss();
//                                }
//                            });
//                        } else {
//                            ApiError apiError = ErrorUtils.parseError(response);
//                            Toast.makeText(PilihAbsensiActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<BaseResponse> call, Throwable t) {
//                        Toast.makeText(PilihAbsensiActivity.this, "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
//                });
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