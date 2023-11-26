package com.ideplex.absensi.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideplex.absensi.R;

import java.util.ArrayList;

/**
 * Created by Sfyn on 04/02/2018.
 */

public class AdapterRiwayatKehadiran extends ArrayAdapter<String> {

    private Activity context;
    private Context mContext;
    private ArrayList<String> scan_date = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> dinas_luar = new ArrayList<>();
    private ArrayList<String> keterangan = new ArrayList<>();

    public AdapterRiwayatKehadiran(Activity context, ArrayList<String> scan_date, ArrayList<String> status, ArrayList<String> dinas_luar, ArrayList<String> keterangan) {
        super(context, R.layout.adapter_riwayat_kehadiran, scan_date);

        this.context = context;
        this.mContext = context;
        this.scan_date = scan_date;
        this.status = status;
        this.dinas_luar = dinas_luar;
        this.keterangan = keterangan;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder = null;
        if (v == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            v = layoutInflater.inflate(R.layout.adapter_riwayat_kehadiran, null, true);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tgl_kehadiran.setText(scan_date.get(position).substring(0,10));
        viewHolder.jam_absen.setText(scan_date.get(position).substring(11));
        viewHolder.keterangan.setText(keterangan.get(position));

        if (status.get(position).equals("1")) {
            viewHolder.jam_absen.setTextColor(Color.parseColor("#00B04E"));
            viewHolder.gbr_keluar.setVisibility(View.GONE);
            viewHolder.gbr_masuk.setVisibility(View.VISIBLE);
            viewHolder.absen_pulang.setVisibility(View.GONE);
            if (dinas_luar.get(position).equals("0")) {
                viewHolder.absen_masuk.setVisibility(View.VISIBLE);
                viewHolder.dinas_luar.setVisibility(View.GONE);
            } else if (dinas_luar.get(position).equals("1")) {
                viewHolder.absen_masuk.setVisibility(View.GONE);
                viewHolder.dinas_luar.setVisibility(View.VISIBLE);
            }
        } else if (status.get(position).equals("2")) {
            viewHolder.jam_absen.setTextColor(Color.parseColor("#E95557"));
            viewHolder.gbr_keluar.setVisibility(View.VISIBLE);
            viewHolder.gbr_masuk.setVisibility(View.GONE);
            viewHolder.absen_pulang.setVisibility(View.VISIBLE);
            viewHolder.absen_masuk.setVisibility(View.GONE);
        }

        return v;
    }

    class ViewHolder{
        ImageView gbr_keluar, gbr_masuk;
        TextView tgl_kehadiran;
        TextView jam_absen;
        TextView keterangan;
        TextView absen_masuk, absen_pulang, dinas_luar;
        ViewHolder(View view){
            gbr_masuk = view.findViewById(R.id.gbr_masuk);
            gbr_keluar = view.findViewById(R.id.gbr_keluar);
            tgl_kehadiran = view.findViewById(R.id.tgl_kehadiran);
            jam_absen = view.findViewById(R.id.jam_absen);
            keterangan = view.findViewById(R.id.keterangan);
            absen_masuk = view.findViewById(R.id.absen_masuk);
            absen_pulang = view.findViewById(R.id.absen_pulang);
            dinas_luar = view.findViewById(R.id.dinas_luar);
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
