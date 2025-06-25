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
        // Crear tabla 'clients'
        db.execSQL("CREATE TABLE IF NOT EXISTS clients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, cedula TEXT, phone TEXT, address TEXT, serviceType TEXT)");

        // Crear la tabla 'team_members' con la columna 'clientCedula'
        String CREATE_TEAM_MEMBERS_TABLE = "CREATE TABLE IF NOT EXISTS team_members (" +
                "id INTEGER PRIMARY KEY," +
                "technician_role TEXT," +
                "technician_name TEXT," +
                "technician_phone TEXT," +
                "team_members_count INTEGER)";
        db.execSQL(CREATE_TEAM_MEMBERS_TABLE);

        // Crear tabla 'requests' con la nueva columna 'serviceAddress'
        db.execSQL("CREATE TABLE IF NOT EXISTS requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "serviceType TEXT, " +
                "serviceDate TEXT, " +
                "serviceTime TEXT, " +
                "clientCedula TEXT, " + // Asegurarnos de tener la cédula del cliente
                "serviceAddress TEXT);"); // Columna para la dirección de la solicitud
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {  // Asegúrate de actualizar la versión de la base de datos
            // Agregar la columna 'clientCedula'
            db.execSQL("ALTER TABLE team_members ADD COLUMN clientCedula TEXT");
        }
    }

}
