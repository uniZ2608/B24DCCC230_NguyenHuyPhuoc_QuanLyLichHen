package com.example.quanlylichhen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText edtTitle, edtLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat switchReminder;
    private Button btnSave;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        dbHelper = new DatabaseHelper(this);
        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminderAdd);
        btnSave = findViewById(R.id.btnSaveAppointment);
        btnBack = findViewById(R.id.btnBackAdd);

        timePicker.setIs24HourView(true);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        // 1. Kiểm tra rỗng
        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tên lịch hẹn");
            edtTitle.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            edtLocation.setError("Vui lòng nhập địa điểm");
            edtLocation.requestFocus();
            return;
        }

        // 2. Lấy ngày giờ từ DatePicker và TimePicker
        int year = datePicker.getYear();
        int month = datePicker.getMonth(); // 0 - 11
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 3. Kiểm tra thời điểm hẹn phải sau thời điểm hiện tại
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, day, hour, minute, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Calendar nowCal = Calendar.getInstance();
        if (!selectedCal.after(nowCal)) {
            Toast.makeText(this, "Thời điểm hẹn phải sau thời điểm hiện tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Định dạng chuẩn yyyy-MM-dd và HH:mm để lưu SQLite
        String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String timeStr = String.format(Locale.US, "%02d:%02d", hour, minute);
        boolean reminder = switchReminder.isChecked();

        Appointment newApp = new Appointment(title, location, dateStr, timeStr, reminder);
        long result = dbHelper.insertAppointment(newApp);

        if (result != -1) {
            Toast.makeText(this, "Đã lưu lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về MainActivity, onResume() sẽ tự tải lại danh sách
        } else {
            Toast.makeText(this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }
}