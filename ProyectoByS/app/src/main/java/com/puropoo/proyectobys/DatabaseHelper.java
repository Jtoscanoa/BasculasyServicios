package com.puropoo.proyectobys;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private final SQLiteHelper helper;

    public DatabaseHelper(Context ctx) {
        helper = new SQLiteHelper(ctx);
    }

    public boolean clientExists(String cedula) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM clients WHERE cedula = ?",
                new String[]{ cedula }
        );
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    public long insertClient(String name, String cedula, String phone, String address, String serviceType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("cedula", cedula);
        cv.put("phone", phone);
        cv.put("address", address);
        cv.put("serviceType", serviceType);
        long id = db.insert("clients", null, cv);
        db.close();
        return id;
    }

    public int updateClient(int id, String name, String cedula, String phone, String address, String serviceType) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("cedula", cedula);
        cv.put("phone", phone);
        cv.put("address", address);
        cv.put("serviceType", serviceType);
        int rows = db.update("clients", cv, "id = ?", new String[]{ String.valueOf(id) });
        db.close();
        return rows;
    }

    public int deleteClient(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete("clients", "id = ?", new String[]{ String.valueOf(id) });
        db.close();
        return rows;
    }

    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, name, cedula, phone, address, serviceType FROM clients",
                null
        );
        while (c.moveToNext()) {
            Client cl = new Client(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5)
            );
            list.add(cl);
        }
        c.close();
        db.close();
        return list;
    }

}
