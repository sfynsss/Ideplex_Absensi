package com.ideplex.absensi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ApiError;
import com.ideplex.absensi.Helpers.ErrorUtils;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.Kehadiran;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatKehadiranActivity extends AppCompatActivity {

    ImageView btn_back;
    LinearLayout select_tgl_awal, select_tgl_akhir;
    TextView tgl_awal, tgl_akhir;
    Button lihat_riwayat;
    ListView list_riwayat_kehadiran;

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    ArrayList<String> tanggal = new ArrayList<>();
    ArrayList<String> checkin = new ArrayList<>();
    ArrayList<String> checkout = new ArrayList<>();

    Session session;
    Api api;
    Call<BaseResponse<Kehadiran>> getRiwayatKehadiran;
    AdapterRiwayatKehadiran adapterRiwayatKehadiran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_kehadiran);

        btn_back = findViewById(R.id.btn_back);
        select_tgl_awal = findViewById(R.id.select_tgl_awal);
        select_tgl_akhir = findViewById(R.id.select_tgl_akhir);
//        tgl_awal = findViewById(R.id.tgl_awal);
//        tgl_akhir = findViewById(R.id.tgl_akhir);
        lihat_riwayat = findViewById(R.id.lihat_riwayat);
        list_riwayat_kehadiran = findViewById(R.id.list_riwayat_kehadiran);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        lihat_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRiwayatKehadiran = api.getRiwayatKehadiran();
                getRiwayatKehadiran.enqueue(new Callback<BaseResponse<Kehadiran>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Kehadiran>> call, Response<BaseResponse<Kehadiran>> response) {
                        if (response.isSuccessful()) {
                            tanggal.clear();
                            checkin.clear();
                            checkout.clear();

                            for (int i = 0; i < response.body().getData().size(); i++) {
                                tanggal.add(response.body().getData().get(i).getTanggal());
                                checkin.add(response.body().getData().get(i).getCheckin());
                                checkout.add(response.body().getData().get(i).getCheckout());
                            }

                            adapterRiwayatKehadiran = new AdapterRiwayatKehadiran(RiwayatKehadiranActivity.this, tanggal, checkin, checkout);
                            list_riwayat_kehadiran.setAdapter(adapterRiwayatKehadiran);
                            adapterRiwayatKehadiran.notifyDataSetChanged();
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(RiwayatKehadiranActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Kehadiran>> call, Throwable t) {
                        Toast.makeText(RiwayatKehadiranActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        select_tgl_awal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String tglAwal = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
//                        tgl_awal.setText(tglAwal);
//                    }
//                }, yy, mm, dd);
//                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
//                datePickerDialog.show();
//            }
//        });
//
//        select_tgl_akhir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(RiwayatKehadiranActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String tglAkhir = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
//                        tgl_akhir.setText(tglAkhir);
//                    }
//                }, yy, mm, dd);
//                datePickerDialog.show();
//            }
//        });

    }
}