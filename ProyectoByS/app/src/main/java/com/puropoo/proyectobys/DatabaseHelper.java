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
        helper = new SQLiteHelper(ctx);  // Asegúrate de que este es el objeto que se utiliza para la conexión
    }

    public boolean clientExists(String cedula) {
        SQLiteDatabase db = helper.getReadableDatabase();  // Utilizando helper para obtener la base de datos
        Cursor c = db.rawQuery(
                "SELECT 1 FROM clients WHERE cedula = ?",
                new String[]{ cedula }
        );
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    public long insertClient(String name, String cedula, String phone, String address) {
        SQLiteDatabase db = helper.getWritableDatabase();  // Utilizando helper para obtener la base de datos
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("cedula", cedula);
        cv.put("phone", phone);
        cv.put("address", address);
        long id = db.insert("clients", null, cv);
        db.close();
        return id;
    }
    public Request getRequestById(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase();  // Cambiar a helper.getReadableDatabase()
        Cursor cursor = db.query(
                "requests",    // Nombre de la tabla
                new String[] {"id", "serviceType", "serviceDate", "serviceTime"}, // Columnas a seleccionar
                "id = ?",      // Condición WHERE
                new String[]{String.valueOf(requestId)},  // Argumento para la condición WHERE
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            // Crear y devolver el objeto Request
            Request request = new Request(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("serviceType")),
                    cursor.getString(cursor.getColumnIndex("serviceDate")),
                    cursor.getString(cursor.getColumnIndex("serviceTime"))
            );

            cursor.close();
            db.close();
            return request;
        } else {
            db.close();
            return null;  // Si no se encuentra la solicitud
        }
    }

    public int deleteClient(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete("clients", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }


    public void updateClient(int id, String nn, String cedula, String pp, String address, String serviceType) {
    }
}
