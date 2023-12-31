package com.ideplex.absensi.Api;

import com.ideplex.absensi.Response.BaseResponse;
import com.ideplex.absensi.Response.BaseResponse2;
import com.ideplex.absensi.Response.UserResponse;
import com.ideplex.absensi.Table.Absen;
import com.ideplex.absensi.Table.JadwalHariIni;
import com.ideplex.absensi.Table.Jarak;
import com.ideplex.absensi.Table.Kehadiran;
import com.ideplex.absensi.Table.Presensi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Call<UserResponse> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("imei") String imei
    );

    @FormUrlEncoded
    @POST("getJadwalHariIni")
    Call<BaseResponse<JadwalHariIni>> getJadwalHariIni(
            @Field("nip") String nip
    );

    @GET("getJadwalKerja")
    Call<BaseResponse<JadwalHariIni>> getJadwalSaya();

    @GET("select-checkin")
    Call<BaseResponse2<Presensi>> getKehadiran();

    @GET("checkin")
    Call<BaseResponse2<Presensi>> checkin();

    @GET("checkout")
    Call<BaseResponse2<Presensi>> checkout();

    @FormUrlEncoded
    @POST("getAbsensi")
    Call<BaseResponse<Absen>> getAbsen(
            @Field("sts") String sts
    );

    @FormUrlEncoded
    @POST("absenWajah")
    Call<BaseResponse> absenWajah(
            @Field("status") String status,
            @Field("jadwal_id") String jadwal_id,
            @Field("jam_kerja_id") String jam_kerja_id,
            @Field("bagian_id") String bagian_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("gambar") String gambar,
            @Field("dinas_luar") String dinas_luar,
            @Field("jarak") String jarak
    );

    @FormUrlEncoded
    @POST("izinCuti")
    Call<BaseResponse> izinCuti(
            @Field("jenis_izin") String jenis_izin,
            @Field("tgl_mulai") String tgl_mulai,
            @Field("tgl_selesai") String tgl_selesai,
            @Field("surat_izin") String surat_izin,
            @Field("keterangan") String keterangan
    );

    @FormUrlEncoded
    @POST("absenScanQr")
    Call<BaseResponse> absenScanQr(
            @Field("status") String status,
            @Field("jadwal_id") String jadwal_id,
            @Field("jam_kerja_id") String jam_kerja_id,
            @Field("bagian_id") String bagian_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("dinas_luar") String dinas_luar,
            @Field("jarak") String jarak
    );

    @FormUrlEncoded
    @POST("cekQr")
    Call<BaseResponse> cekQr(
            @Field("qr") String qr
    );

    @GET("getRiwayatKehadiran")
    Call<BaseResponse<Kehadiran>> getRiwayatKehadiran();

    @GET("https://maps.googleapis.com/maps/api/distancematrix/json")
    Call<Jarak> getJarak (
            @Query(value = "origins", encoded = true) String origin,
            @Query(value = "destinations", encoded = true) String destination,
            @Query("key") String api_key
    );

    @FormUrlEncoded
    @POST("ubahPassword")
    Call<BaseResponse> ubahPassword(
            @Field("nip") String nip,
            @Field("password") String password
    );

    @GET("getSettingJarak")
    Call<BaseResponse> getSettingJarak();
}
