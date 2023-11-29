package com.ideplex.absensi.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ideplex.absensi.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sfyn on 04/02/2018.
 */

public class AdapterDurasiKerja extends ArrayAdapter<String> {

    private Activity context;
    private Context mContext;
    private ArrayList<String> tanggal = new ArrayList<>();
    private ArrayList<String> durasi = new ArrayList<>();

    public AdapterDurasiKerja(Activity context, ArrayList<String> tanggal, ArrayList<String> durasi) {
        super(context, R.layout.adapter_durasi_kerja, tanggal);

        this.context = context;
        this.mContext = context;
        this.tanggal = tanggal;
        this.durasi = durasi;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder = null;
        if (v == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            v = layoutInflater.inflate(R.layout.adapter_durasi_kerja, null, true);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tanggal.setText(tanggal.get(position));
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(tanggal.get(position));
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] days = new String[] { "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu" };
        String day = days[calendar.get(Calendar.DAY_OF_WEEK)-1];

        viewHolder.nama_hari.setText(day);
        viewHolder.durasi.setText(durasi.get(position));

        return v;
    }

    class ViewHolder{
        TextView tanggal;
        TextView nama_hari;
        TextView durasi;
        ViewHolder(View view){
            nama_hari = view.findViewById(R.id.nama_hari);
            tanggal = view.findViewById(R.id.tanggal);
            durasi = view.findViewById(R.id.durasi);
        }
    }

    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }

    public interface OnEditLocationListener {
        void onClickAdapter(int position);
    }

}
