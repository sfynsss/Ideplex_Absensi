package com.ideplex.absensi.Api;

import android.text.TextUtils;

import com.facebook.stetho.BuildConfig;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static String BASE_URL = "https://beta.idemedika.com/demo/kerjapintar_fr/public/index.php/api/";
//    private static String BASE_URL = "http://api.pdesoebandi.id/api/";
    private final static OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    private static RetrofitClient mInstance;
    public static Retrofit retrofit = buildRetrofit(client);

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

    private static OkHttpClient buildClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder1 = request.newBuilder()
                                .addHeader("Accept","application/json")
                                .addHeader("Connection", "close");

                        request = builder1.build();

                        return chain.proceed(request);
                    }
                });

        if (BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();
    }

    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static <T> T createServiceWithAuth(Class<T> service, final String api_token) {

        OkHttpClient newClient = client.newBuilder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                builder.addHeader("Accept", "application/json");
                builder.header("Connection", "close");
                if (!TextUtils.isEmpty(api_token)) {
                    builder.addHeader("Authorization", "Bearer " +api_token);
                    builder.header("Connection", "close");
                }

                request = builder.build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);
    }

    public static <T> T createServiceWithAuthLogin(Class<T> service, final String token) {

        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if (token != null) {
                    builder.addHeader("Authorization", "Bearer " +token);
                }
                builder.header("Connection", "close");
                request = builder.build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);
    }
}
