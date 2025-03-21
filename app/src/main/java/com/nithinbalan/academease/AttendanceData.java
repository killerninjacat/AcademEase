package com.nithinbalan.academease;

public class AttendanceData {
    private String name,date,attended;
    private int id;

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    private int cnt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttended() {
        return attended;
    }

    public void setAttended(String attended) {
        this.attended = attended;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AttendanceData(String name, String date, String attended, int id, int cnt) {
        this.name = name;
        this.date = date;
        this.attended = attended;
        this.cnt=cnt;
        this.id = id;
    }
}
