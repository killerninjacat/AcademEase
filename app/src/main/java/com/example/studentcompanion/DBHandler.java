package com.example.studentcompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "attendancedb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "attendance";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "Course";
    private static final String DATE_COL = "Date";
    private static final String ATTENDED = "Attended";
    public void deleteAttendance(String courseName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = NAME_COL + "=? AND " + DATE_COL + "=?";
        String[] whereArgs = {courseName, date};
        db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
    }
    public void deleteSubject(String subjectName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NAME_COL+"=?", new String[]{subjectName});
        db.close();
    }
    public void updateName(String newName,String oldName)
    {
        Log.d("UpdateName", "Old Name: " + oldName);
        Log.d("UpdateName", "New Name: " + newName);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL,newName);
        String whereClause = NAME_COL + "=?";
        String[] whereArgs = {oldName};
        db.update(TABLE_NAME,values,whereClause,whereArgs);
        int rowsUpdated = db.update(TABLE_NAME, values, NAME_COL + "=?", new String[]{oldName});
        if (rowsUpdated == 0) {
            Log.e("UpdateName", "Update failed. No rows were updated.");
        }
        else {
            Log.e("UpdateName", "Update successful. Rows updated: " + rowsUpdated);
        }
        db.close();
    }
    public void updateAttended(String subjectName, String date, String newAttended)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ATTENDED,newAttended);
        String whereClause = NAME_COL + "=? AND " + DATE_COL + "=?";
        String[] whereArgs = {subjectName, date};
        db.update(TABLE_NAME,values,whereClause,whereArgs);
        db.close();
    }
    public List<AttendanceData> readData()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        List<AttendanceData> attendanceDataList=new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                attendanceDataList.add(new AttendanceData(cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getInt(0) ));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return attendanceDataList;
    }
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + DATE_COL + " TEXT,"
                + ATTENDED + " TEXT)";
        db.execSQL(query);
    }

    public void addNewCourse(String courseName, String date, String attended) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, courseName);
        values.put(DATE_COL, date);
        values.put(ATTENDED, attended);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
