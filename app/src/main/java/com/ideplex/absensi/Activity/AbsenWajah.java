package com.ideplex.absensi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ideplex.absensi.R;

import java.io.ByteArrayOutputStream;

public class AbsenWajah extends AppCompatActivity {

    Button btn_ambil_gambar;
    ImageView gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_wajah);

        gambar = findViewById(R.id.gambar);
        btn_ambil_gambar = findViewById(R.id.btn_ambil_gambar);

        btn_ambil_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(AbsenWajah.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 102);
                    } else {
                        ActivityCompat.requestPermissions(AbsenWajah.this,new String[]{Manifest.permission.CAMERA}, 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            Toast.makeText(AbsenWajah.this, "Permisson Dennied.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102){
            if(resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                gambar.setImageBitmap(photo);
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