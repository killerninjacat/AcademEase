package com.nithinbalan.academease;

public class DayData {
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    String day;
    int cnt;

    public DayData(String date, int cnt) {
        this.day = date;
        this.cnt = cnt;
    }
}
