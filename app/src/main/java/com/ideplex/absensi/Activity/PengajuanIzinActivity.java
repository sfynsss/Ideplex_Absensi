package com.ideplex.absensi.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ideplex.absensi.Api.Api;
import com.ideplex.absensi.Api.RetrofitClient;
import com.ideplex.absensi.Helpers.ApiError;
import com.ideplex.absensi.Helpers.ErrorUtils;
import com.ideplex.absensi.Helpers.ImageFilePath;
import com.ideplex.absensi.R;
import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Session.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengajuanIzinActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    ImageView btn_back;
    Spinner cuti_spinner;
    LinearLayout select_tgl_awal,select_tgl_akhir;
    TextView tgl_awal, tgl_akhir, nama_file;
    Button btn_simpan, btn_browse;
    EditText keterangan;

    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    private static int REQUEST_CODE = 1;
    Bitmap bitmap;

    Api api;
    Session session;
    Call<BaseResponse> izinCuti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_izin);

        session = new Session(this);
        api = RetrofitClient.createServiceWithAuth(Api.class, session.getToken());

        btn_back = findViewById(R.id.btn_back);
        select_tgl_awal = findViewById(R.id.select_tgl_awal);
        select_tgl_akhir = findViewById(R.id.select_tgl_akhir);
        tgl_awal = findViewById(R.id.tgl_awal);
        tgl_akhir = findViewById(R.id.tgl_akhir);
        nama_file = findViewById(R.id.nama_file);
        btn_browse = findViewById(R.id.btn_browse);
        btn_simpan = findViewById(R.id.btn_simpan);
        cuti_spinner = findViewById(R.id.cuti_spinner);
        keterangan = findViewById(R.id.keterangan);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<CharSequence> bulan_spinner = ArrayAdapter.createFromResource(this, R.array.izin_array, android.R.layout.simple_spinner_item);
        bulan_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuti_spinner.setAdapter(bulan_spinner);

        select_tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tglAwal = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                        tgl_awal.setText(tglAwal);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        select_tgl_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PengajuanIzinActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tglAkhir = String.valueOf(year) +"-"+String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
                        tgl_akhir.setText(tglAkhir);
                    }
                }, yy, mm, dd);
                datePickerDialog.show();
            }
        });

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, REQUEST_CODE);
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(tgl_awal.getText())) {
                    tgl_awal.setError("Pilih tanggal terlebih dahulu !!!");
                } else if (TextUtils.isEmpty(tgl_akhir.getText())) {
                    tgl_akhir.setError("Pilih tanggal terlebih dahulu !!!");
                } else if (TextUtils.isEmpty(nama_file.getText())) {
                    Toast.makeText(PengajuanIzinActivity.this, "Pilih Gambar Surat Izin !!!", Toast.LENGTH_SHORT).show();
                } else {
//                    System.out.println(cuti_spinner.getSelectedItem().toString().toUpperCase(Locale.ROOT));
//                    System.out.println(tgl_awal.getText().toString());
//                    System.out.println(tgl_akhir.getText().toString());
//                    System.out.println(imageToString(bitmap));
//                    System.out.println(keterangan.getText().toString());
                    izinCuti = api.izinCuti(cuti_spinner.getSelectedItem().toString().toUpperCase(Locale.ROOT), tgl_awal.getText().toString(),
                            tgl_akhir.getText().toString(), imageToString(bitmap), keterangan.getText().toString());
                    izinCuti.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PengajuanIzinActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            } else {
                                ApiError apiError = ErrorUtils.parseError(response);
                                Toast.makeText(PengajuanIzinActivity.this, apiError.getMessage()+" Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(PengajuanIzinActivity.this, t.getMessage()+" Error 1", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String realPath = ImageFilePath.getPath(PengajuanIzinActivity.this, data.getData());
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                nama_file.setText(realPath);
                Log.i(TAG, "onActivityResult: file path : " + realPath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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