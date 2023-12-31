package com.ideplex.absensi.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse2;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.Presensi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DinasDalamFragment extends Fragment {

    LinearLayout btn_absen_masuk, btn_absen_pulang;
    TextView tgl_hadir, absen_masuk, absen_pulang, jarak, test;
    Button btn_refresh_jarak;

    DateFormat dateFormat;
    Date date;

    Session session;
    Api api;
    Call<BaseResponse2<Presensi>> getKehadiran;
    Call<BaseResponse2<Presensi>> checkin;
    Call<BaseResponse2<Presensi>> checkout;
    Boolean sts_masuk = false;
    Boolean sts_pulang = false;
    Boolean sts_dinas_luar = false;
    Boolean sts_jadwal = false;

    String jam_mulai_masuk, jam_sampai_masuk;
    String jam_mulai_pulang, jam_sampai_pulang;

    private GpsTracker gpsTracker;
    double hasil = 0;
    double titik_absen = 10000000;

    //Koordinat Soebandi
    double finalLat = -8.1516843;
    double finalLong = 113.6710509;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dinas_dalam, container, false);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date();

        btn_absen_masuk = (LinearLayout) view.findViewById(R.id.btn_absen_masuk);
        btn_absen_pulang = (LinearLayout) view.findViewById(R.id.btn_absen_pulang);
        tgl_hadir = view.findViewById(R.id.tgl_hadir);
        absen_masuk = view.findViewById(R.id.absen_masuk);
        absen_pulang = view.findViewById(R.id.absen_pulang);
        tgl_hadir.setText(dateFormat.format(date));
        jarak = view.findViewById(R.id.jarak);

        btn_refresh_jarak = view.findViewById(R.id.btn_refresh_jarak);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        jadwalHariIni();

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            } else {
                getLocation();
            }
        } catch (Exception e){
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
                System.out.println(titik_absen+" | "+hasil);
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
                            Intent it = new Intent(getContext(), PilihAbsensiActivity.class);
                            it.putExtra("tipe", "pulang");
                            it.putExtra("jarak", hasil);
                            startActivity(it);
                        }
                    } else {
                        Toast.makeText(getContext(), "Silahkan checkin terlebih dahulu !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    public void jadwalHariIni() {
        getKehadiran = api.getKehadiran();
        getKehadiran.enqueue(new Callback<BaseResponse2<Presensi>>() {
            @Override
            public void onResponse(Call<BaseResponse2<Presensi>> call, Response<BaseResponse2<Presensi>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckin())) {
                            sts_masuk = true;
                            absen_masuk.setText(response.body().getData().get(0).getCheckin().substring(11));
                        }

                        if (!TextUtils.isEmpty(response.body().getData().get(0).getCheckout())) {
                            sts_pulang = true;
                            absen_pulang.setText(response.body().getData().get(0).getCheckout().substring(11));
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Data tidak ditemukan !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse2<Presensi>> call, Throwable t) {
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime,
                                               String currentTime) throws ParseException {

        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) &&
                currentTime.matches(reg))
        {
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

            if (finalTime.compareTo(initialTime) < 0)
            {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime()))
            {
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

    public void getLocation(){
        gpsTracker = new GpsTracker(getContext());
        if(gpsTracker.canGetLocation()){
            double initialLat = gpsTracker.getLatitude();
            double initialLong = gpsTracker.getLongitude();
            hasil = Math.round(CalculationByDistance(initialLat, initialLong, finalLat, finalLong) * 1000);
            jarak.setText(String.format("%.0f", hasil));
        }else{
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
}