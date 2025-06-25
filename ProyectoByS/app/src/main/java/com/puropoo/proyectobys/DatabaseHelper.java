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
                new String[]{cedula}
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
                new String[]{"id", "serviceType", "serviceDate", "serviceTime"}, // Columnas a seleccionar
                "id = ?",      // Condición WHERE
                new String[]{String.valueOf(requestId)},  // Argumento para la condición WHERE
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Request request = new Request(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("serviceType")),
                    cursor.getString(cursor.getColumnIndex("serviceDate")),
                    cursor.getString(cursor.getColumnIndex("serviceTime")),
                    cursor.getString(cursor.getColumnIndex("serviceAddress"))  // Asegúrate de agregar esta línea
            );
            cursor.close();
            db.close();
            return request;
        } else {
            db.close();
            return null;
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
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, serviceType, serviceDate, serviceTime, clientCedula, serviceAddress FROM requests", null);

        while (c.moveToNext()) {
            // Puedes agregar un campo `serviceAddress` en el constructor de la clase `Request` si es necesario
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress"))  // Obtener el valor de serviceAddress
            );
            list.add(request);
        }

        c.close();
        db.close();
        return list;
    }


    // Método para eliminar solicitud
    public int deleteRequest(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete("requests", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
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

    public int updateRequest(int requestId, String newServiceType, String newServiceDate, String newServiceTime, String newServiceAddress) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serviceType", newServiceType);
        values.put("serviceDate", newServiceDate);
        values.put("serviceTime", newServiceTime);
        values.put("serviceAddress", newServiceAddress);  // Actualizamos solo la dirección de la solicitud

        // Actualizar la solicitud con el ID proporcionado
        int rowsUpdated = db.update("requests", values, "id = ?", new String[]{String.valueOf(requestId)});
        db.close();
        return rowsUpdated;
    }


    public long insertRequest(String serviceType, String serviceDate, String serviceTime, String clientCedula, String serviceAddress) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Verificar si ya existe una solicitud con el mismo cliente, en la misma fecha y hora
        String query = "SELECT 1 FROM requests WHERE serviceDate = ? AND serviceTime = ? AND clientCedula = ?";
        Cursor cursor = db.rawQuery(query, new String[]{serviceDate, serviceTime, clientCedula});

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return -1; // Indicar que la inserción no fue exitosa
        }

        // Insertar nueva solicitud si no existe duplicado
        ContentValues cv = new ContentValues();
        cv.put("serviceType", serviceType);
        cv.put("serviceDate", serviceDate);
        cv.put("serviceTime", serviceTime);
        cv.put("clientCedula", clientCedula);  // Guardar la cédula del cliente
        cv.put("serviceAddress", serviceAddress);  // Guardar la dirección de la solicitud

        long id = db.insert("requests", null, cv);
        cursor.close();
        db.close();
        return id;
    }


    public Client getClientByCedula(String cedula) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("clients", new String[]{"id", "name", "cedula", "phone", "address", "serviceType"},
                "cedula = ?", new String[]{cedula}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Client client = new Client(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("cedula")),
                    cursor.getString(cursor.getColumnIndex("phone")),
                    cursor.getString(cursor.getColumnIndex("address")),
                    cursor.getString(cursor.getColumnIndex("serviceType"))
            );
            cursor.close();
            db.close();
            return client;
        }

        cursor.close();
        db.close();
        return null;  // Si no se encuentra el cliente
    }

    // Método para obtener la cédula del cliente asociado a una solicitud
    public String getClientCedulaForRequest(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT clientCedula FROM requests WHERE id = ?", new String[]{String.valueOf(requestId)});

        String clientCedula = null;
        if (cursor != null && cursor.moveToFirst()) {
            clientCedula = cursor.getString(cursor.getColumnIndex("clientCedula"));
        }

        cursor.close();
        db.close();
        return clientCedula;
    }


    // Método para insertar un miembro del equipo técnico
    public long insertTeamMember(String technicianName, String technicianRole, String technicianPhone, int teamMembersCount) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("technician_name", technicianName);  // Nombre del técnico
        values.put("technician_role", technicianRole);  // Rol del técnico
        values.put("technician_phone", technicianPhone);  // Teléfono del técnico
        values.put("team_members_count", teamMembersCount);  // Número de miembros del equipo

        long id = db.insert("team_members", null, values);  // Insertamos en la tabla 'team_members'
        db.close();  // Cerramos la base de datos
        return id;  // Retornamos el id generado del nuevo miembro del equipo
    }



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Agregar la columna "technician_phone" si no existe
            db.execSQL("ALTER TABLE team_members ADD COLUMN technician_phone TEXT");
        }
    }


    //Método para verificar si el número de teléfono ya está registrado
    public boolean isPhoneNumberRegistered(String phoneNumber) {
        SQLiteDatabase db = helper.getReadableDatabase();  // Usamos el helper para obtener la base de datos
        Cursor cursor = db.rawQuery("SELECT 1 FROM team_members WHERE technician_phone = ?", new String[]{phoneNumber});
        boolean isRegistered = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isRegistered;
    }


    // Método para verificar si ya se han asignado técnicos a una solicitud
    public boolean isTeamAssignedToRequest(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase(); // Usar el helper para obtener la base de datos
        Cursor cursor = db.rawQuery("SELECT * FROM team WHERE request_id = ?", new String[]{String.valueOf(requestId)});
        boolean hasTeamAssigned = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return hasTeamAssigned;
    }
    public boolean insertMaintenanceRequirements(String serviceName, String requirements) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("serviceName", serviceName);
        values.put("requirements", requirements);

        long id = db.insert("maintenance_requirements", null, values);
        db.close();
        return id != -1;  // Retorna true si la inserción fue exitosa
    }

}
