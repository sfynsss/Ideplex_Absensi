package com.ideplex.absensi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ApiError;
import com.ideplex.absensi.Helpers.ErrorUtils;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Session.Session;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfilActivity extends AppCompatActivity {

    EditText password_pengguna;
    Button btn_browse, btn_simpan;
    ImageView btn_back, foto_pengguna;

    Session session;
    Api api;

    String kataSandi;

    Call<BaseResponse> ubahPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        btn_browse = findViewById(R.id.btn_browse);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_back = findViewById(R.id.btn_back);
//        foto_pengguna = findViewById(R.id.foto_pengguna);
        password_pengguna = findViewById(R.id.password_pengguna);

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

//        btn_browse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(EditProfilActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
//                        Intent intent = new Intent();
//                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(intent, 102);
//                    } else {
//                        ActivityCompat.requestPermissions(EditProfilActivity.this,new String[]{Manifest.permission.CAMERA}, 100);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kataSandi = password_pengguna.getText().toString();
                ubahPassword = api.ubahPassword(kataSandi);
                ubahPassword.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProfilActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();
                        } else {
                            ApiError apiError = ErrorUtils.parseError(response);
                            Toast.makeText(EditProfilActivity.this, apiError.getMessage()+" Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(EditProfilActivity.this, t.getMessage()+" Error 1", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        } else {
            Toast.makeText(EditProfilActivity.this, "Permisson Dennied.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                foto_pengguna.setImageBitmap(photo);
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