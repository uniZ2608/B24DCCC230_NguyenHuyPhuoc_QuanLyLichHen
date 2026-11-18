package com.example.quanlylichhen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvLocation, tvDateTime, tvReminderStatus;
    private SwitchCompat switchReminder;
    private Button btnBack, btnDelete;
    private ImageButton btnBackTop;
    private DatabaseHelper dbHelper;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        dbHelper = new DatabaseHelper(this);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvDateTime = findViewById(R.id.tvDetailDateTime);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        switchReminder = findViewById(R.id.switchReminderDetail);
        btnBack = findViewById(R.id.btnBackDetail);
        btnDelete = findViewById(R.id.btnDeleteAppointment);
        btnBackTop = findViewById(R.id.btnBackTopDetail);

        // Nhận id từ Intent và truy vấn lại SQLite
        appointmentId = getIntent().getIntExtra("appointment_id", -1);
        Appointment appointment = dbHelper.getAppointmentById(appointmentId);

        if (appointment == null) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị dữ liệu lên giao diện
        tvTitle.setText(appointment.getTitle());
        tvLocation.setText(appointment.getLocation());
        tvDateTime.setText(appointment.getDisplayDate() + "  •  " + appointment.getTime());

        switchReminder.setChecked(appointment.isReminder());
        tvReminderStatus.setText(appointment.isReminder() ? "Đang bật" : "Đang tắt");

        // Gắn sự kiện bật/tắt nhắc nhở và cập nhật vào SQLite
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.updateReminder(appointmentId, isChecked);
            tvReminderStatus.setText(isChecked ? "Đang bật" : "Đang tắt");
        });

        btnBack.setOnClickListener(v -> finish());
        btnBackTop.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteAppointment(appointmentId);
            Toast.makeText(this, "Đã xóa lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}