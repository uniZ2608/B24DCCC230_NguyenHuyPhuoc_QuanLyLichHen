package com.example.quanlylichhen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class AppointmentAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Appointment> list;

    public AppointmentAdapter(Context context, ArrayList<Appointment> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false);
        }

        Appointment item = list.get(position);

        TextView tvTitle = convertView.findViewById(R.id.tvItemTitle);
        TextView tvLocation = convertView.findViewById(R.id.tvItemLocation);
        TextView tvDateTime = convertView.findViewById(R.id.tvItemDateTime);
        TextView tvReminder = convertView.findViewById(R.id.tvItemReminder);

        tvTitle.setText(item.getTitle());
        tvLocation.setText("Địa điểm: " + item.getLocation());
        tvDateTime.setText(item.getDisplayDate() + "  •  " + item.getTime());

        if (item.isReminder()) {
            tvReminder.setText("Có nhắc");
            tvReminder.setBackgroundResource(R.drawable.bg_badge_orange);
            tvReminder.setTextColor(ContextCompat.getColor(context, R.color.orange_text));
        } else {
            tvReminder.setText("Không nhắc");
            tvReminder.setBackgroundResource(R.drawable.bg_badge_gray);
            tvReminder.setTextColor(ContextCompat.getColor(context, R.color.text_gray));
        }

        return convertView;
    }
}