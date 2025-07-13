package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.puropoo.proyectobys.SmsUtils;

public class RegisterRequestActivity extends AppCompatActivity {

    Spinner spinnerClients, spinnerServiceType;
    DatePicker datePickerServiceDate;
    TimePicker timePickerServiceTime;
    EditText etServiceAddress;
    Button btnSaveRequest, btnViewRequests;
    DatabaseHelper db;
    List<Client> clientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_request);

        spinnerClients = findViewById(R.id.spinnerClients);
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        datePickerServiceDate = findViewById(R.id.datePickerServiceDate);
        timePickerServiceTime = findViewById(R.id.timePickerServiceTime);
        timePickerServiceTime.setIs24HourView(true);
        btnSaveRequest = findViewById(R.id.btnSaveRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        etServiceAddress = findViewById(R.id.etServiceAddress); // Vincula el EditText para la dirección


        db = new DatabaseHelper(this);

        // Cargar clientes al spinner
        clientsList = db.getAllClients();
        List<String> clientNames = new ArrayList<>();
        clientNames.add(getString(R.string.select_client));
        for (Client client : clientsList) {
            clientNames.add(client.getName() + " - " + client.getCedula());
        }
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clientNames);
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClients.setAdapter(clientAdapter);

        // Cargar tipo de servicio
        ArrayAdapter<CharSequence> serviceAdapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(serviceAdapter);

        // Guardar solicitud
        btnSaveRequest.setOnClickListener(v -> saveRequest());

        // Ver solicitudes guardadas
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterRequestActivity.this, ViewRequestsActivity.class);
            startActivity(intent);
        });
    }

    private void saveRequest() {
        String serviceDate = String.format("%02d/%02d/%04d",
                datePickerServiceDate.getDayOfMonth(),
                datePickerServiceDate.getMonth() + 1,
                datePickerServiceDate.getYear());

        String serviceTime = String.format("%02d:%02d",
                timePickerServiceTime.getHour(),
                timePickerServiceTime.getMinute());

        int servicePos = spinnerServiceType.getSelectedItemPosition();
        int clientPos = spinnerClients.getSelectedItemPosition();

        if (clientPos <= 0) {
            Toast.makeText(this, getString(R.string.select_client), Toast.LENGTH_SHORT).show();
            return;
        }

        if (servicePos <= 0) {
            Toast.makeText(this, getString(R.string.select_service), Toast.LENGTH_SHORT).show();
            return;
        }

        String serviceType = spinnerServiceType.getSelectedItem().toString();
        String clientCedula = clientsList.get(clientPos - 1).getCedula();
        String newServiceAddress = etServiceAddress.getText().toString();

        // Validación de fecha
        if (!isDateValid(serviceDate)) {
            Toast.makeText(this, "La fecha debe ser mayor a 3 días desde hoy.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de hora
        if (!isTimeValid(serviceTime)) {
            Toast.makeText(this, "La hora debe estar entre 6am y 7pm.", Toast.LENGTH_SHORT).show();
            return;
        }



        // Guardar solicitud
        // Obtener la cédula del cliente seleccionado
// Llamar al método para guardar la solicitud con la dirección también
        long id = db.insertRequest(serviceType, serviceDate, serviceTime, clientCedula, newServiceAddress);
        if (id != -1) {
            Toast.makeText(this, "Solicitud guardada correctamente", Toast.LENGTH_LONG).show();
            Request request = new Request((int) id, serviceType, serviceDate, serviceTime, newServiceAddress, clientCedula);
            SmsUtils.scheduleSmsForRequest(this, request);
        } else {
            Toast.makeText(this, "Ya existe una solicitud para este cliente en esta fecha y hora", Toast.LENGTH_LONG).show();
        }

    }




    private boolean isDateValid(String serviceDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(serviceDate);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 3);  // Aumenta 3 días
            Date threeDaysFromNow = calendar.getTime();

            return date.after(threeDaysFromNow); // Verifica si la fecha ingresada es posterior a 3 días desde hoy
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTimeValid(String serviceTime) {
        try {
            // Verificación de formato (HH:mm)
            if (!serviceTime.matches("^(0[6-9]|1[0-7]):[0-5][0-9]$")) {
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date time = sdf.parse(serviceTime);

            // Hora mínima (6 AM) y hora máxima (7 PM)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            // Verificar si la hora está entre 6 AM (6:00) y 7 PM (19:00)
            return hour >= 6 && hour <= 19;
        } catch (Exception e) {
            return false;
        }
    }



}
