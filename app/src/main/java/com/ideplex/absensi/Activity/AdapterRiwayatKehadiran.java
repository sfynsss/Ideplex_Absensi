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
    private ArrayList<String> tanggal = new ArrayList<>();
    private ArrayList<String> checkin = new ArrayList<>();
    private ArrayList<String> checkout = new ArrayList<>();

    public AdapterRiwayatKehadiran(Activity context, ArrayList<String> tanggal, ArrayList<String> checkin, ArrayList<String> checkout) {
        super(context, R.layout.adapter_riwayat_kehadiran, tanggal);

        this.context = context;
        this.mContext = context;
        this.tanggal = tanggal;
        this.checkin = checkin;
        this.checkout = checkout;
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

        viewHolder.tgl_kehadiran.setText(tanggal.get(position));
        viewHolder.checkin.setText(checkin.get(position) != null ? checkin.get(position).substring(11) : "");
        viewHolder.checkout.setText(checkout.get(position) != null ? checkout.get(position).substring(11) : "");

        return v;
    }

    class ViewHolder{
        TextView tgl_kehadiran;
        TextView checkin;
        TextView checkout;
        ViewHolder(View view){
            tgl_kehadiran = view.findViewById(R.id.tgl_kehadiran);
            checkin = view.findViewById(R.id.checkin);
            checkout = view.findViewById(R.id.checkout);
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
