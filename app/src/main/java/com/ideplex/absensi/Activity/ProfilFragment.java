package com.ideplex.absensi.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Session.Session;
import com.ideplex.absensi.Table.JadwalHariIni;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;

public class ProfilFragment extends Fragment {

    RelativeLayout btn_logout, btn_edit, btn_qr;

    Session session;
    Api api;
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        session = new Session(getContext());
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_qr = view.findViewById(R.id.btn_qr);

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputBox(session.getNip());
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfilActivity.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setUserStatus(false, "","", "");
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    public void showInputBox(String tmp_nip) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("QR Code NIP");
        View v = getLayoutInflater().inflate(R.layout.adapter_qr, null);
        dialog.setContentView(v);
//        System.out.println(oldItem);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView close, nip;
        ImageView qr;

        close = v.findViewById(R.id.close);
        nip = v.findViewById(R.id.nip);
        qr = v.findViewById(R.id.qr);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        QRGEncoder qrgEncoder = new QRGEncoder(tmp_nip, null, QRGContents.Type.TEXT, 200);
        Bitmap qrBits = qrgEncoder.getBitmap();
        qr.setImageBitmap(qrBits);

        nip.setText(tmp_nip);

        dialog.show();
    }

}