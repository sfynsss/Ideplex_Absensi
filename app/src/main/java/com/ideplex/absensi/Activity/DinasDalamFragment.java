package com.ideplex.absensi.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ImageFilePath;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse2;
import com.ideplex.absensi.Response.ResponseSelectCheckin;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.Presensi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DinasDalamFragment extends Fragment {

    LinearLayout btn_absen_masuk, btn_absen_pulang, btn_break, btn_lanjut, btn_message_checkin;
    TextView tgl_hadir, absen_masuk, absen_pulang, jarak, text_message_checkin, durasi;
    Button btn_refresh_jarak;

    DateFormat dateFormat;
    Date date;

    Session session;
    Api api;
    Call<ResponseSelectCheckin<Presensi>> getKehadiran;
    Call<BaseResponse2<Presensi>> istirahat;
    Call<BaseResponse2<Presensi>> lanjut;
    Call<BaseResponse2<Presensi>> cek_checkout;
    Boolean sts_masuk = false;
    Boolean sts_pulang = false;
    Boolean result_cek_checkout = false;
    Integer id_presensi = 0;

    private GpsTracker gpsTracker;
    double hasil = 0;
    double titik_absen = 10000000;

    //Koordinat Soebandi
    double finalLat = -8.1516843;
    double finalLong = 113.6710509;

    EditText laporan;
    Button btn_browse, btn_simpan;
    TextView nama_file;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinas_dalam, container, false);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date();

        btn_absen_masuk = (LinearLayout) view.findViewById(R.id.btn_absen_masuk);
        btn_break = (LinearLayout) view.findViewById(R.id.btn_break);
        btn_lanjut = (LinearLayout) view.findViewById(R.id.btn_lanjut);
        btn_absen_pulang = (LinearLayout) view.findViewById(R.id.btn_absen_pulang);
        btn_message_checkin = (LinearLayout) view.findViewById(R.id.message_checkin);
        text_message_checkin = view.findViewById(R.id.text_message_checkin);
        tgl_hadir = view.findViewById(R.id.tgl_hadir);
        absen_masuk = view.findViewById(R.id.absen_masuk);
        absen_pulang = view.findViewById(R.id.absen_pulang);
        durasi = view.findViewById(R.id.durasi);
        tgl_hadir.setText(dateFormat.format(date));
        jarak = view.findViewById(R.id.jarak);

        btn_refresh_jarak = view.findViewById(R.id.btn_refresh_jarak);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        jadwalHariIni();

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            } else {
                getLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_refresh_jarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                Toast.makeText(getContext(), "Jarak lokasi diperbaharui", Toast.LENGTH_SHORT).show();
                jadwalHariIni();
            }
        });

        btn_absen_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                System.out.println(titik_absen + " | " + hasil);
                if (hasil > titik_absen) {
                    Toast.makeText(getContext(), "Anda diluar radius presensi", Toast.LENGTH_SHORT).show();
                } else {
                    if (sts_masuk) {
                        Toast.makeText(getContext(), "Anda sudah checkin", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
                        it.putExtra("tipe", "masuk");
                        it.putExtra("jarak", hasil);
                        startActivity(it);
                    }
                }
            }
        });

        btn_absen_pulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                if (hasil > titik_absen) {
                    Toast.makeText(getContext(), "Anda diluar radius presensi", Toast.LENGTH_SHORT).show();
                } else {
                    if (sts_masuk) {
                        if (sts_pulang) {
                            Toast.makeText(getContext(), "Anda sudah checkout", Toast.LENGTH_SHORT).show();
                        } else {
                            action_cek_chekout(id_presensi);
//                                Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
//                                it.putExtra("tipe", "pulang");
//                                it.putExtra("jarak", hasil);
//                                startActivity(it);
                        }
                    } else {
                        Toast.makeText(getContext(), "Silahkan checkin terlebih dahulu !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                if (hasil > titik_absen) {
                    Toast.makeText(getContext(), "Anda diluar radius presensi", Toast.LENGTH_SHORT).show();
                } else {
                    doing_break(id_presensi);
                }
            }
        });

        btn_lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                if (hasil > titik_absen) {
                    Toast.makeText(getContext(), "Anda diluar radius presensi", Toast.LENGTH_SHORT).show();
                } else {
                    doing_lanjut(id_presensi);
                }
            }
        });

        return view;
    }

    public void jadwalHariIni() {
        getKehadiran = api.getKehadiran();
        getKehadiran.enqueue(new Callback<ResponseSelectCheckin<Presensi>>() {
            @Override
            public void onResponse(Call<ResponseSelectCheckin<Presensi>> call, Response<ResponseSelectCheckin<Presensi>> response) {
                if (response.isSuccessful()) {
                    String waktuServer = response.body().getShift().getWaktuServer();
                    String earlyCheckin = response.body().getShift().getEarlyCheckin();
                    String lateCheckin = response.body().getShift().getLateCheckin();

                    if (response.body().getShift().getStatus() != 0) {
                        if (response.body().getShift().getStatus() == 0 &&
                                !response.body().getShift().getBagian().equalsIgnoreCase("dokter")) {
                            text_message_checkin.setText("Checkin belum dibuka, silahkan cek jadwal Anda");
                            btn_message_checkin.setVisibility(View.VISIBLE);
                        } else {
                            if (earlyCheckin.compareTo(waktuServer) > 0 &&
                                    response.body().getShift().getStatus() == 1 &&
                                    !response.body().getShift().getBagian().equalsIgnoreCase("dokter")) {
                                text_message_checkin.setText("Checkin akan dibuka pada pukul " + response.body().getShift().getEarlyCheckin());
                                btn_message_checkin.setVisibility(View.VISIBLE);
                            } else if (lateCheckin.compareTo(waktuServer) < 0 &&
                                    response.body().getShift().getStatus() == 1 &&
                                    !response.body().getShift().getBagian().equalsIgnoreCase("dokter")) {
                                text_message_checkin.setText("Checkin telah ditutup pada pukul " + response.body().getShift().getLateCheckin());
                                btn_message_checkin.setVisibility(View.VISIBLE);
                            } else {
                                if (response.body().getData() == null) {
                                    btn_absen_masuk.setVisibility(View.VISIBLE);
                                } else {
                                    id_presensi = response.body().getData().get(0).getId();
                                    if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckin())) {
                                        sts_masuk = true;
                                        absen_masuk.setText(response.body().getData().get(0).getCheckin().substring(11));
                                    }

                                    if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckout())) {
                                        sts_pulang = true;
                                        absen_pulang.setText(response.body().getData().get(0).getCheckout().substring(11));
                                    }

                                    if (!TextUtils.isEmpty(response.body().getDurasi())) {
                                        durasi.setText(response.body().getDurasi());
                                    }

                                    switch (response.body().getData().get(0).getStatus()) {
                                        case "aktif":
                                            btn_break.setVisibility(View.VISIBLE);
                                            btn_absen_pulang.setVisibility(View.VISIBLE);
                                            break;
                                        case "break":
                                            btn_lanjut.setVisibility(View.VISIBLE);
                                            btn_absen_pulang.setVisibility(View.VISIBLE);
                                            break;
                                        default:
                                            if (response.body().getShift().getBagian().equalsIgnoreCase("dokter")) {
                                                btn_lanjut.setVisibility(View.VISIBLE);
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (response.body().getData() != null) {
                            id_presensi = response.body().getData().get(0).getId();
                            if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckin())) {
                                sts_masuk = true;
                                absen_masuk.setText(response.body().getData().get(0).getCheckin().substring(11));
                            }

                            if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckout())) {
                                sts_pulang = true;
                                absen_pulang.setText(response.body().getData().get(0).getCheckout().substring(11));
                            }

                            if (!TextUtils.isEmpty(response.body().getDurasi())) {
                                durasi.setText(response.body().getDurasi());
                            }

                            switch (response.body().getData().get(0).getStatus()) {
                                case "aktif":
                                    btn_break.setVisibility(View.VISIBLE);
                                    btn_absen_pulang.setVisibility(View.VISIBLE);
                                    break;
                                case "break":
                                    btn_lanjut.setVisibility(View.VISIBLE);
                                    btn_absen_pulang.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    if (response.body().getShift().getBagian().equalsIgnoreCase("dokter")) {
                                        btn_lanjut.setVisibility(View.VISIBLE);
                                    }
                                    break;
                            }
                        } else {
                            btn_absen_masuk.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Data tidak ditemukan !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSelectCheckin<Presensi>> call, Throwable t) {
                Toast.makeText(getContext(), "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime,
                                               String currentTime) throws ParseException {

        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) &&
                currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime())) {
                valid = true;
                return valid;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean cekBefore(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean cekAfter(String time, String endtime) {

        String pattern = "H:m:s";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(getContext());
        if (gpsTracker.canGetLocation()) {
            double initialLat = gpsTracker.getLatitude();
            double initialLong = gpsTracker.getLongitude();
            hasil = Math.round(CalculationByDistance(initialLat, initialLong, finalLat, finalLong) * 1000);
            jarak.setText(String.format("%.0f", hasil));
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong) {
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat - initialLat);
        double dLon = toRadians(finalLong - initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI / 180);
    }

    public void doing_break(Integer id_presensi) {
        istirahat = api.istirahat(id_presensi + "");
        istirahat.enqueue(new Callback<BaseResponse2<Presensi>>() {
            @Override
            public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == false) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        btn_lanjut.setVisibility(View.VISIBLE);
                        btn_break.setVisibility(View.GONE);
                        btn_absen_pulang.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        jadwalHariIni();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal melakukan break !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                Toast.makeText(getContext(), "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void doing_lanjut(Integer id_presensi) {
        lanjut = api.lanjut(id_presensi + "");
        lanjut.enqueue(new Callback<BaseResponse2<Presensi>>() {
            @Override
            public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == false) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        btn_lanjut.setVisibility(View.GONE);
                        btn_break.setVisibility(View.VISIBLE);
                        btn_absen_pulang.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal melakukan lanjut !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                Toast.makeText(getContext(), "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void action_cek_chekout(Integer id_presensi) {
        cek_checkout = api.cek_checkout(id_presensi + "");
        cek_checkout.enqueue(new Callback<BaseResponse2<Presensi>>() {
            @Override
            public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == false) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        showInputBox();
                    }
                } else {
                    Toast.makeText(getContext(), "Pengecekan Checkout Gagal !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                Toast.makeText(getContext(), "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInputBox() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Modal Laporan");
        View v = getLayoutInflater().inflate(R.layout.adapter_laporan, null);
        dialog.setContentView(v);
//        System.out.println(oldItem);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        laporan = v.findViewById(R.id.laporan);
        btn_browse = v.findViewById(R.id.btn_browse);
        nama_file = v.findViewById(R.id.nama_file);
        btn_simpan = v.findViewById(R.id.btn_simpan);

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
                it.putExtra("tipe", "pulang");
                it.putExtra("jarak", hasil);
                it.putExtra("laporan", laporan.getText().toString());
                it.putExtra("upload", imageToString(bitmap));
                it.putExtra("id_presensi", id_presensi+"");
                startActivity(it);
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1 && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String realPath = ImageFilePath.getPath(getContext(), data.getData());
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                nama_file.setText(realPath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                } catch (IOException e) {
                    e.printStackTrace();
                }
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