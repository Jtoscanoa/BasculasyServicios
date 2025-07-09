package com.puropoo.proyectobys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, "mydatabase.db", null, 1);
    }

    // Definir el nombre y la versión de la base de datos
    private static final String DATABASE_NAME = "service_db";
    private static final int DATABASE_VERSION = 1;

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

        // Crear tabla 'team' (falta esta tabla)
        db.execSQL("CREATE TABLE IF NOT EXISTS team (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "request_id INTEGER, " + // Relación con la solicitud
                "technician_name TEXT, " +
                "technician_role TEXT, " +
                "technician_phone TEXT);");

        // Crear la tabla 'maintenance_requirements'
        String CREATE_MAINTENANCE_REQUIREMENTS_TABLE = "CREATE TABLE IF NOT EXISTS maintenance_requirements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "serviceName TEXT, " +
                "requirements TEXT)";
        db.execSQL(CREATE_MAINTENANCE_REQUIREMENTS_TABLE);

        // Crear la tabla 'equipo_instalar'
        String CREATE_EQUIPO_INSTALAR_TABLE = "CREATE TABLE IF NOT EXISTS equipo_instalar (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "request_id INTEGER, " +
                "equipo_nombre TEXT, " +
                "clientCedula TEXT)";
        db.execSQL(CREATE_EQUIPO_INSTALAR_TABLE);

    }

    // Método para actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí se manejan los cambios de versión de la base de datos
        db.execSQL("DROP TABLE IF EXISTS clients");
        db.execSQL("DROP TABLE IF EXISTS requests");
        db.execSQL("DROP TABLE IF EXISTS team");
        onCreate(db);
    }

}
