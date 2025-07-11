package com.puropoo.proyectobys;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
                new String[]{"id", "serviceType", "serviceDate", "serviceTime", "serviceAddress", "clientCedula"}, // Columnas a seleccionar
                "id = ?",      // Condición WHERE
                new String[]{String.valueOf(requestId)},  // Argumento para la condición WHERE
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Request request = new Request(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("serviceType")),
                    cursor.getString(cursor.getColumnIndex("serviceDate")),
                    cursor.getString(cursor.getColumnIndex("serviceTime")),
                    cursor.getString(cursor.getColumnIndex("serviceAddress")),
                    cursor.getString(cursor.getColumnIndex("clientCedula"))
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
            // Asegúrate de que 'serviceType' contenga los valores correctos
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress")),
                    c.getString(c.getColumnIndex("clientCedula"))
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

    public String getRequirementsForService(String serviceName) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT requirements FROM maintenance_requirements WHERE serviceName = ?", new String[]{serviceName});

        String requirements = null;
        if (cursor != null && cursor.moveToFirst()) {
            requirements = cursor.getString(cursor.getColumnIndex("requirements"));
        }
        cursor.close();
        db.close();
        return requirements;
    }

    public boolean updateMaintenanceRequirements(String serviceName, String requirements) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("requirements", requirements);

        // Log para verificar los valores
        Log.d("DatabaseHelper", "Actualizando requerimientos para el servicio: " + serviceName);
        Log.d("DatabaseHelper", "Nuevos requerimientos: " + requirements);

        int rowsUpdated = db.update("maintenance_requirements", values, "serviceName = ?", new String[]{serviceName});
        db.close();

        if (rowsUpdated > 0) {
            Log.d("DatabaseHelper", "Requerimientos actualizados correctamente");
        } else {
            Log.d("DatabaseHelper", "No se actualizó ningún requerimiento");
        }

        return rowsUpdated > 0;
    }

    // Métodos para manejo de equipo a instalar

    // Obtener solicitudes de instalación con fecha mayor o igual a hoy
    public List<Request> getInstallationRequestsFromToday() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        
        // Obtener fecha actual en formato dd/MM/yyyy
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String today = sdf.format(new java.util.Date());
        
        String query = "SELECT id, serviceType, serviceDate, serviceTime, clientCedula, serviceAddress " +
                       "FROM requests WHERE serviceType = 'Instalación' AND " +
                       "DATE(substr(serviceDate, 7, 4) || '-' || substr(serviceDate, 4, 2) || '-' || substr(serviceDate, 1, 2)) >= " +
                       "DATE(substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)) " +
                       "ORDER BY serviceDate, serviceTime";
        
        Cursor c = db.rawQuery(query, new String[]{today, today, today});

        while (c.moveToNext()) {
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress")),
                    c.getString(c.getColumnIndex("clientCedula"))
            );
            list.add(request);
        }

        c.close();
        db.close();
        return list;
    }

    // Obtener solicitudes de instalación próximas (desde hoy en adelante)
    public List<Request> getUpcomingInstallRequests() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        
        // Obtener fecha actual en formato yyyy-MM-dd
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());
        
        String query = "SELECT id, serviceType, serviceDate, serviceTime, clientCedula, serviceAddress " +
                       "FROM requests WHERE LOWER(serviceType) LIKE LOWER('%Instalac%') AND " +
                       "serviceDate >= ? " +
                       "ORDER BY serviceDate, serviceTime";
        
        Cursor c = db.rawQuery(query, new String[]{today});

        while (c.moveToNext()) {
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress")),
                    c.getString(c.getColumnIndex("clientCedula"))
            );
            list.add(request);
        }

        c.close();
        db.close();
        return list;
    }

    // Insertar equipo a instalar
    public long insertEquipoInstalar(int requestId, String equipoNombre, String clientCedula) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("request_id", requestId);
        values.put("equipo_nombre", equipoNombre);
        values.put("clientCedula", clientCedula);

        long id = db.insert("equipo_instalar", null, values);
        db.close();
        return id;
    }

    // Actualizar equipo a instalar
    public int updateEquipoInstalar(int requestId, String equipoNombre) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("equipo_nombre", equipoNombre);

        int rowsUpdated = db.update("equipo_instalar", values, "request_id = ?", 
                                  new String[]{String.valueOf(requestId)});
        db.close();
        return rowsUpdated;
    }

    // Verificar si ya existe equipo para una solicitud
    public String getEquipoByRequestId(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT equipo_nombre FROM equipo_instalar WHERE request_id = ?", 
                                   new String[]{String.valueOf(requestId)});

        String equipoNombre = null;
        if (cursor != null && cursor.moveToFirst()) {
            equipoNombre = cursor.getString(cursor.getColumnIndex("equipo_nombre"));
        }

        cursor.close();
        db.close();
        return equipoNombre;
    }

    // Obtener servicios técnicos futuros (mantenimiento y reparación)
    public List<Request> getFutureTechnicalServices() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        
        // Obtener fecha actual en formato yyyy-MM-dd
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());
        
        String query = "SELECT id, serviceType, serviceDate, serviceTime, clientCedula, serviceAddress " +
                       "FROM requests WHERE (LOWER(serviceType) LIKE LOWER('%mantenimiento%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%reparac%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%técnic%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%tecnic%')) AND " +
                       "serviceDate >= ? " +
                       "ORDER BY serviceDate, serviceTime";
        
        Cursor c = db.rawQuery(query, new String[]{today});

        while (c.moveToNext()) {
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress")),
                    c.getString(c.getColumnIndex("clientCedula"))
            );
            list.add(request);
        }

        c.close();
        db.close();
        return list;
    }

    // Métodos para manejo de segundas visitas

    // Obtener todos los servicios de mantenimiento (para el spinner)
    public List<Request> getAllMaintenanceServices() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        
        String query = "SELECT id, serviceType, serviceDate, serviceTime, clientCedula, serviceAddress " +
                       "FROM requests WHERE LOWER(serviceType) LIKE LOWER('%mantenimiento%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%reparac%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%técnic%') OR " +
                       "LOWER(serviceType) LIKE LOWER('%tecnic%') " +
                       "ORDER BY serviceDate DESC, serviceTime DESC";
        
        Cursor c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            Request request = new Request(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("serviceType")),
                    c.getString(c.getColumnIndex("serviceDate")),
                    c.getString(c.getColumnIndex("serviceTime")),
                    c.getString(c.getColumnIndex("serviceAddress")),
                    c.getString(c.getColumnIndex("clientCedula"))
            );
            list.add(request);
        }

        c.close();
        db.close();
        return list;
    }

    // Insertar segunda visita
    public long insertSecondVisit(int serviceRequestId, String serviceType, String visitDate, String visitTime, String clientCedula) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("service_request_id", serviceRequestId);
        values.put("service_type", serviceType);
        values.put("visit_date", visitDate);
        values.put("visit_time", visitTime);
        values.put("client_cedula", clientCedula);

        long id = db.insert("second_visits", null, values);
        db.close();
        return id;
    }

    // Actualizar segunda visita
    public int updateSecondVisit(int serviceRequestId, String visitDate, String visitTime) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("visit_date", visitDate);
        values.put("visit_time", visitTime);

        int rowsUpdated = db.update("second_visits", values, "service_request_id = ?", 
                                  new String[]{String.valueOf(serviceRequestId)});
        db.close();
        return rowsUpdated;
    }

    // Eliminar segunda visita
    public int deleteSecondVisit(int serviceRequestId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsDeleted = db.delete("second_visits", "service_request_id = ?", 
                                   new String[]{String.valueOf(serviceRequestId)});
        db.close();
        return rowsDeleted;
    }

    // Obtener segunda visita por ID de solicitud de servicio
    public SecondVisit getSecondVisitByServiceRequestId(int serviceRequestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM second_visits WHERE service_request_id = ?", 
                                   new String[]{String.valueOf(serviceRequestId)});

        SecondVisit secondVisit = null;
        if (cursor != null && cursor.moveToFirst()) {
            secondVisit = new SecondVisit(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("service_request_id")),
                    cursor.getString(cursor.getColumnIndex("service_type")),
                    cursor.getString(cursor.getColumnIndex("visit_date")),
                    cursor.getString(cursor.getColumnIndex("visit_time")),
                    cursor.getString(cursor.getColumnIndex("client_cedula"))
            );
        }

        cursor.close();
        db.close();
        return secondVisit;
    }

    // Verificar si existe una segunda visita para un servicio
    public boolean hasSecondVisit(int serviceRequestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM second_visits WHERE service_request_id = ?", 
                                   new String[]{String.valueOf(serviceRequestId)});

        boolean hasSecondVisit = cursor.moveToFirst();
        cursor.close();
        db.close();
        return hasSecondVisit;
    }

    // Métodos para manejo de soporte remoto

    // Insertar soporte remoto
    public long insertRemoteSupport(int requestId, String supportDate, String supportTime, 
                                   String medium, String link, String clientCedula) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("request_id", requestId);
        values.put("support_date", supportDate);
        values.put("support_time", supportTime);
        values.put("medium", medium);
        values.put("link", link);
        values.put("client_cedula", clientCedula);

        long id = db.insert("remote_support", null, values);
        db.close();
        return id;
    }

    // Actualizar soporte remoto
    public int updateRemoteSupport(int requestId, String supportDate, String supportTime, 
                                  String medium, String link) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("support_date", supportDate);
        values.put("support_time", supportTime);
        values.put("medium", medium);
        values.put("link", link);

        int rowsUpdated = db.update("remote_support", values, "request_id = ?", 
                                  new String[]{String.valueOf(requestId)});
        db.close();
        return rowsUpdated;
    }

    // Obtener soporte remoto por ID de solicitud
    public RemoteSupport getRemoteSupportByRequestId(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM remote_support WHERE request_id = ?", 
                                   new String[]{String.valueOf(requestId)});

        RemoteSupport remoteSupport = null;
        if (cursor != null && cursor.moveToFirst()) {
            remoteSupport = new RemoteSupport(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("request_id")),
                    cursor.getString(cursor.getColumnIndex("support_date")),
                    cursor.getString(cursor.getColumnIndex("support_time")),
                    cursor.getString(cursor.getColumnIndex("medium")),
                    cursor.getString(cursor.getColumnIndex("link")),
                    cursor.getString(cursor.getColumnIndex("client_cedula"))
            );
        }

        cursor.close();
        db.close();
        return remoteSupport;
    }

    // Verificar si existe soporte remoto para un servicio
    public boolean hasRemoteSupport(int requestId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM remote_support WHERE request_id = ?", 
                                   new String[]{String.valueOf(requestId)});

        boolean hasSupport = cursor.moveToFirst();
        cursor.close();
        db.close();
        return hasSupport;
    }

    // Obtener todos los servicios con soporte remoto
    public List<RemoteSupport> getAllRemoteSupports() {
        List<RemoteSupport> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        
        String query = "SELECT rs.*, r.serviceDate, r.serviceTime " +
                       "FROM remote_support rs " +
                       "JOIN requests r ON rs.request_id = r.id " +
                       "ORDER BY rs.support_date DESC, rs.support_time DESC";
        
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            RemoteSupport remoteSupport = new RemoteSupport(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("request_id")),
                    cursor.getString(cursor.getColumnIndex("support_date")),
                    cursor.getString(cursor.getColumnIndex("support_time")),
                    cursor.getString(cursor.getColumnIndex("medium")),
                    cursor.getString(cursor.getColumnIndex("link")),
                    cursor.getString(cursor.getColumnIndex("client_cedula"))
            );
            list.add(remoteSupport);
        }

        cursor.close();
        db.close();
        return list;
    }



}
