package com.puropoo.proyectobys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, "mydatabase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS clients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, cedula TEXT, phone TEXT, address TEXT, serviceType TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS team_members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, role TEXT, phone TEXT, age INTEGER, payment REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si necesitas upgrade de BD
    }

    public boolean insertTeamMember(String name, String role, String phone, int age, double payment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("role", role);
        values.put("phone", phone);
        values.put("age", age);
        values.put("payment", payment);

        long result = db.insert("team_members", null, values);
        return result != -1;
    }

}
