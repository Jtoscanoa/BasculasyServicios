package com.puropoo.proyectobys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bys.db";
    private static final int DB_VERSION = 2;  // Incrementamos

    public SQLiteHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE clients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "cedula TEXT UNIQUE NOT NULL," +
                        "phone TEXT NOT NULL," +
                        "address TEXT NOT NULL," +
                        "serviceType TEXT NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Para demo: borramos y recreamos
        db.execSQL("DROP TABLE IF EXISTS clients");
        onCreate(db);
    }
}
