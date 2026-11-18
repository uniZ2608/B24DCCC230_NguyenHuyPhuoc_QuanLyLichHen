package com.example.quanlylichhen;

public class Appointment {
    private int id;
    private String title;
    private String location;
    private String date; // Định dạng lưu DB: yyyy-MM-dd
    private String time; // Định dạng lưu DB: HH:mm
    private boolean reminder;

    public Appointment() {
    }

    public Appointment(int id, String title, String location, String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Appointment(String title, String location, String date, String time, boolean reminder) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isReminder() { return reminder; }
    public void setReminder(boolean reminder) { this.reminder = reminder; }

    // Chuyển yyyy-MM-dd thành dd/MM/yyyy để hiển thị lên màn hình
    public String getDisplayDate() {
        if (date != null && date.contains("-")) {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        }
        return date;
    }
}