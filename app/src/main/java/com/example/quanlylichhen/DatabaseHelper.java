package com.example.quanlylichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Bảng appointments
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COL_APP_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_LOCATION = "location";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // 2. Tạo bảng appointments
        String createAppointmentsTable = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                COL_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_LOCATION + " TEXT NOT NULL, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_TIME + " TEXT NOT NULL, " +
                COL_REMINDER + " INTEGER NOT NULL)";
        db.execSQL(createAppointmentsTable);

        // 3. Chèn tài khoản mẫu: admin / 123456
        insertUserInternal(db, "admin", "123456");

        // 4. Chèn 3 lịch hẹn mẫu (ngày dạng yyyy-MM-dd, giờ dạng HH:mm)
        insertAppointmentInternal(db, new Appointment("Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", true));
        insertAppointmentInternal(db, new Appointment("Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", true));
        insertAppointmentInternal(db, new Appointment("Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", false));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    private void insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
    }

    private long insertAppointmentInternal(SQLiteDatabase db, Appointment a) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, a.getTitle());
        values.put(COL_LOCATION, a.getLocation());
        values.put(COL_DATE, a.getDate());
        values.put(COL_TIME, a.getTime());
        values.put(COL_REMINDER, a.isReminder() ? 1 : 0);
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    // 1. Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 2. Thêm tài khoản mới
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // 3. Thêm lịch hẹn mới, trả về id vừa thêm
    public long insertAppointment(Appointment a) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertAppointmentInternal(db, a);
    }

    // 4. Lấy toàn bộ lịch hẹn, sắp xếp tăng dần theo ngày và giờ
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " ORDER BY " + COL_DATE + " ASC, " + COL_TIME + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
                boolean reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMINDER)) == 1;

                list.add(new Appointment(id, title, location, date, time, reminder));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 5. Lấy một lịch hẹn theo id
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS + " WHERE " + COL_APP_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Appointment appointment = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
            boolean reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMINDER)) == 1;
            appointment = new Appointment(id, title, location, date, time, reminder);
        }
        cursor.close();
        return appointment;
    }

    // 6. Cập nhật trạng thái nhắc nhở
    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPOINTMENTS, values, COL_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 7. Xóa lịch hẹn theo id
    public int deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPOINTMENTS, COL_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 8. Đếm tổng số lịch hẹn
    public int countAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 9. Đếm số lịch hẹn đang bật nhắc nhở
    public int countReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COL_REMINDER + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}