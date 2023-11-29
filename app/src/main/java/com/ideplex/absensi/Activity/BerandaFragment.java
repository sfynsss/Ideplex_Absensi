package com.ideplex.absensi.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ideplex.absensi.Table.JadwalHariIni;
import com.ideplex.absensi.Table.Kehadiran;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout btn_jadwal_saya, btn_riwayat_kehadiran, btn_pengajuan_izin;
    Handler handler = new Handler();

    TextView nama_pengguna, nip_pengguna, jabatan_pengguna, shift_pengguna;
    ListView list_riwayat_kehadiran;
    ImageView img_profil;
    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;
    Call<BaseResponse<Kehadiran>> getRiwayatKehadiran;
    AdapterDurasiKerja adapterDurasiKerja;

    ArrayList<String> tanggal = new ArrayList<>();
    ArrayList<String> durasi = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beranda2, container, false);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        btn_jadwal_saya = (LinearLayout) view.findViewById(R.id.btn_jadwal_saya);
        btn_riwayat_kehadiran = (LinearLayout) view.findViewById(R.id.btn_riwayat_kehadiran);
        btn_pengajuan_izin = (LinearLayout) view.findViewById(R.id.btn_pengajuan_izin);
        img_profil = (ImageView) view.findViewById(R.id.img_profil);

        nama_pengguna = view.findViewById(R.id.nama_pengguna);
        list_riwayat_kehadiran = view.findViewById(R.id.list_riwayat_kehadiran);
//        nip_pengguna = view.findViewById(R.id.nip_pengguna);
//        jabatan_pengguna = view.findViewById(R.id.jabatan_pengguna);
//        shift_pengguna = view.findViewById(R.id.shift_pengguna);
//        shift_pengguna.setText(session.getShift());

        nama_pengguna.setText(session.getNama());
//        nip_pengguna.setText(session.getNip());
//        jabatan_pengguna.setText(session.getNamaBagian());

        jadwalHariIni(session.getNip());
        getDataRiwayatKehadiran();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        btn_jadwal_saya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), JadwalSayaActivity.class));
            }
        });

        btn_riwayat_kehadiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RiwayatKehadiranActivity.class));
            }
        });

        btn_pengajuan_izin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PengajuanIzinActivity.class));
            }
        });

        if (session.getJenkel().equalsIgnoreCase("laki-laki")) {
            img_profil.setBackgroundResource(R.drawable.profile_photo_l);
        } else if(session.getJenkel().equalsIgnoreCase("perempuan")) {
            img_profil.setBackgroundResource(R.drawable.profile_photo_p);
        } else {
            img_profil.setBackgroundResource(R.drawable.profile_photo_l);
        }

        return view;
    }

    public void jadwalHariIni(String nip) {
        getJadwalHariIni = api.getJadwalHariIni(session.getNip());
        getJadwalHariIni.enqueue(new Callback<BaseResponse<JadwalHariIni>>() {
            @Override
            public void onResponse(Call<BaseResponse<JadwalHariIni>> call, Response<BaseResponse<JadwalHariIni>> response) {
                if (response.isSuccessful()) {
                    ArrayList<String> jadwalku = new ArrayList<>();
                    jadwalku.clear();

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        jadwalku.add(response.body().getData().get(i).getShift());
                    }

                    Collections.reverse(jadwalku);
//                    shift_pengguna.setText(TextUtils.join(", ", jadwalku));
                } else {
//                    shift_pengguna.setText("-");
//                    ApiError apiError = ErrorUtils.parseError(response);
//                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JadwalHariIni>> call, Throwable t) {
//                shift_pengguna.setText("-");
                Toast.makeText(getContext(), "Error "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataRiwayatKehadiran() {
        getRiwayatKehadiran = api.getRiwayatKehadiran();
        getRiwayatKehadiran.enqueue(new Callback<BaseResponse<Kehadiran>>() {
            @Override
            public void onResponse(Call<BaseResponse<Kehadiran>> call, Response<BaseResponse<Kehadiran>> response) {
                if (response.isSuccessful()) {
                    tanggal.clear();
                    durasi.clear();

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        tanggal.add(response.body().getData().get(i).getTanggal());
                        durasi.add(response.body().getData().get(i).getDurasi());
                    }

                    adapterDurasiKerja = new AdapterDurasiKerja(getActivity(), tanggal, durasi);
                    list_riwayat_kehadiran.setAdapter(adapterDurasiKerja);
                    adapterDurasiKerja.notifyDataSetChanged();
                } else {
                    ApiError apiError = ErrorUtils.parseError(response);
                    Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Kehadiran>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}