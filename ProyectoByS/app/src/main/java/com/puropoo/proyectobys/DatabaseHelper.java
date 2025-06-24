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

    // Obtener todas las solicitudes
    public List<Request> getAllRequests() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();  // Usa el helper para obtener la base de datos en modo lectura
        Cursor c = db.rawQuery("SELECT * FROM requests", null);  // Realiza una consulta para obtener todas las solicitudes
        while (c.moveToNext()) {
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime"))
            );
            list.add(request);
        }
        c.close();
        db.close();
        return list;  // Devuelve la lista de todas las solicitudes
    }

    // Método para eliminar una solicitud
    public int deleteRequest(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();  // Obtener base de datos en modo escritura
        int rows = db.delete("requests", "id = ?", new String[]{String.valueOf(id)});  // Eliminar la solicitud
        db.close();  // Cerrar la base de datos
        return rows;  // Retornar el número de filas eliminadas
    }

    // Obtener todos los clientes
    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();  // Usar helper para obtener la base de datos en modo lectura
        Cursor c = db.rawQuery("SELECT * FROM clients", null);  // Consultar todos los registros de la tabla 'clients'

        while (c.moveToNext()) {
            Client client = new Client(
                    c.getInt(c.getColumnIndex("id")),  // ID del cliente
                    c.getString(c.getColumnIndex("name")),  // Nombre del cliente
                    c.getString(c.getColumnIndex("cedula")),  // Cédula
                    c.getString(c.getColumnIndex("phone")),  // Teléfono
                    c.getString(c.getColumnIndex("address")),  // Dirección
                    c.getString(c.getColumnIndex("serviceType"))  // Tipo de servicio
            );
            list.add(client);  // Añadir el cliente a la lista
        }
        c.close();  // Cerrar el cursor
        db.close();  // Cerrar la base de datos
        return list;  // Devolver la lista de clientes
    }

    public int updateRequest(int requestId, String newServiceType, String newServiceDate, String newServiceTime) {
        SQLiteDatabase db = helper.getWritableDatabase();  // Obtener la base de datos en modo escritura
        ContentValues values = new ContentValues();
        values.put("serviceType", newServiceType);  // Actualizar el tipo de servicio
        values.put("serviceDate", newServiceDate);  // Actualizar la fecha
        values.put("serviceTime", newServiceTime);  // Actualizar la hora

        // Actualizar la solicitud con el ID proporcionado
        int rowsUpdated = db.update("requests", values, "id = ?", new String[]{String.valueOf(requestId)});
        db.close();
        return rowsUpdated;  // Devuelve el número de filas actualizadas
    }

    public long insertRequest(String serviceType, String serviceDate, String serviceTime, String clientCedula) {
        SQLiteDatabase db = helper.getWritableDatabase();  // Obtener la base de datos en modo escritura

        // Verificar si ya existe una solicitud con la misma fecha, hora y cliente
        String query = "SELECT 1 FROM requests WHERE serviceDate = ? AND serviceTime = ? AND clientCedula = ?";
        Cursor cursor = db.rawQuery(query, new String[]{serviceDate, serviceTime, clientCedula});

        if (cursor.moveToFirst()) {
            // Si ya existe una solicitud con la misma fecha, hora y cliente
            cursor.close();
            db.close();
            return -1; // Indicar que la inserción no fue exitosa
        }

        // Insertar nueva solicitud si no existe duplicado
        ContentValues cv = new ContentValues();
        cv.put("serviceType", serviceType);
        cv.put("serviceDate", serviceDate);
        cv.put("serviceTime", serviceTime);
        cv.put("clientCedula", clientCedula);  // Asegúrate de almacenar la cédula del cliente también

        long id = db.insert("requests", null, cv);
        cursor.close();
        db.close();
        return id;  // Devuelve el ID de la solicitud insertada
    }


}
