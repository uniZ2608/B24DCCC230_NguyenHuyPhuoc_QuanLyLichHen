package com.example.quanlylichhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCount, tvReminderCount, tvEmptyList;
    private ListView lvAppointments;
    private Button btnAddAppointment;
    private DatabaseHelper dbHelper;
    private ArrayList<Appointment> appointmentList;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList);
        lvAppointments.setAdapter(adapter);

        // Chuyển sang màn hình thêm lịch hẹn
        btnAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });

        // Nhấn vào một lịch hẹn để mở chi tiết (truyền id qua Intent)
        lvAppointments.setOnItemClickListener((parent, view, position, id) -> {
            Appointment selected = appointmentList.get(position);
            Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
            intent.putExtra("appointment_id", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Cập nhật thống kê
        tvTotalCount.setText(String.valueOf(dbHelper.countAll()));
        tvReminderCount.setText(String.valueOf(dbHelper.countReminders()));

        // Tải lại danh sách từ SQLite
        appointmentList.clear();
        appointmentList.addAll(dbHelper.getAllAppointments());
        adapter.notifyDataSetChanged();

        // Xử lý trường hợp danh sách rỗng
        if (appointmentList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            lvAppointments.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            lvAppointments.setVisibility(View.VISIBLE);
        }
    }
}