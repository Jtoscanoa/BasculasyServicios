package com.puropoo.proyectobys;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class RegisterSecondVisitActivity extends AppCompatActivity {

    private Spinner spinnerMaintenanceServices;
    private DatePicker datePickerSecondVisit;
    private TimePicker timePickerSecondVisit;
    private Button btnSaveSecondVisit;
    private Button btnDeleteSecondVisit;

    private DatabaseHelper databaseHelper;
    private List<Request> maintenanceServices;
    private Request selectedService;
    private SecondVisit existingSecondVisit;
    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_visit);

        // Inicializar componentes
        initializeComponents();

        // Configurar base de datos
        databaseHelper = new DatabaseHelper(this);

        // Cargar servicios técnicos
        loadMaintenanceServices();

        // Configurar listeners
        setupListeners();

        // Configurar restricciones de fecha y hora
        setupDateTimeConstraints();
    }

    private void initializeComponents() {
        spinnerMaintenanceServices = findViewById(R.id.spinnerMaintenanceServices);
        datePickerSecondVisit = findViewById(R.id.datePickerSecondVisit);
        timePickerSecondVisit = findViewById(R.id.timePickerSecondVisit);
        btnSaveSecondVisit = findViewById(R.id.btnSaveSecondVisit);
        btnDeleteSecondVisit = findViewById(R.id.btnDeleteSecondVisit);
    }

    private void loadMaintenanceServices() {
        // Obtener únicamente servicios técnicos próximos
        maintenanceServices = databaseHelper.getFutureTechnicalServices();

        if (maintenanceServices == null || maintenanceServices.isEmpty()) {
            Toast.makeText(this, "No hay servicios técnicos registrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Crear lista de strings para el spinner
        List<String> serviceDescriptions = new ArrayList<>();
        serviceDescriptions.add("Seleccionar servicio..."); // Opción por defecto

        for (Request service : maintenanceServices) {
            String description = service.getServiceType() + " - " + service.getServiceDate() + " " + service.getServiceTime();
            serviceDescriptions.add(description);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceDescriptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaintenanceServices.setAdapter(adapter);
    }

    private void setupListeners() {
        spinnerMaintenanceServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Evitar la opción "Seleccionar servicio..."
                    selectedService = maintenanceServices.get(position - 1);
                    checkExistingSecondVisit();
                } else {
                    selectedService = null;
                    resetForm();
                }
                validateForm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedService = null;
                resetForm();
                validateForm();
            }
        });

        datePickerSecondVisit.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> validateForm());

        timePickerSecondVisit.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            validateTimeRange(hourOfDay);
            validateForm();
        });

        btnSaveSecondVisit.setOnClickListener(v -> saveSecondVisit());
        btnDeleteSecondVisit.setOnClickListener(v -> deleteSecondVisit());
    }

    private void setupDateTimeConstraints() {
        // Configurar fecha mínima (hoy)
        Calendar today = Calendar.getInstance();
        datePickerSecondVisit.setMinDate(today.getTimeInMillis());

        // Configurar hora inicial dentro del rango permitido
        timePickerSecondVisit.setHour(8);
        timePickerSecondVisit.setMinute(0);
    }

    private void checkExistingSecondVisit() {
        if (selectedService != null) {
            existingSecondVisit = databaseHelper.getSecondVisitByServiceRequestId(selectedService.getId());
            
            if (existingSecondVisit != null) {
                // Modo edición
                isUpdateMode = true;
                loadExistingSecondVisit();
                btnSaveSecondVisit.setText("ACTUALIZAR");
                btnDeleteSecondVisit.setVisibility(View.VISIBLE);
            } else {
                // Modo crear
                isUpdateMode = false;
                btnSaveSecondVisit.setText("GUARDAR");
                btnDeleteSecondVisit.setVisibility(View.GONE);
            }
        }
    }

    private void loadExistingSecondVisit() {
        if (existingSecondVisit != null) {
            // Cargar fecha
            String[] dateParts = existingSecondVisit.getVisitDate().split("-");
            if (dateParts.length == 3) {
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1; // Mes es 0-indexado
                int day = Integer.parseInt(dateParts[2]);
                datePickerSecondVisit.updateDate(year, month, day);
            }

            // Cargar hora
            String[] timeParts = existingSecondVisit.getVisitTime().split(":");
            if (timeParts.length >= 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                timePickerSecondVisit.setHour(hour);
                timePickerSecondVisit.setMinute(minute);
            }
        }
    }

    private void resetForm() {
        isUpdateMode = false;
        existingSecondVisit = null;
        btnSaveSecondVisit.setText("GUARDAR");
        btnDeleteSecondVisit.setVisibility(View.GONE);
        
        // Resetear fecha y hora a valores por defecto
        Calendar today = Calendar.getInstance();
        datePickerSecondVisit.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        timePickerSecondVisit.setHour(8);
        timePickerSecondVisit.setMinute(0);
    }

    private void validateTimeRange(int hourOfDay) {
        if (hourOfDay < 6 || hourOfDay > 19) {
            Toast.makeText(this, "La hora debe estar entre las 06:00 y 19:00", Toast.LENGTH_SHORT).show();
            // Ajustar a hora válida más cercana
            if (hourOfDay < 6) {
                timePickerSecondVisit.setHour(6);
            } else {
                timePickerSecondVisit.setHour(19);
            }
        }
    }

    private void validateForm() {
        boolean isValid = true;
        StringBuilder missingFields = new StringBuilder();

        // Validar servicio seleccionado
        if (selectedService == null) {
            isValid = false;
            missingFields.append("Servicio de mantenimiento, ");
        }

        // Validar hora dentro del rango permitido
        int currentHour = timePickerSecondVisit.getHour();
        if (currentHour < 6 || currentHour > 19) {
            isValid = false;
            missingFields.append("Hora válida (06:00-19:00), ");
        }

        // Validar que la fecha no sea pasada
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(datePickerSecondVisit.getYear(), datePickerSecondVisit.getMonth(), datePickerSecondVisit.getDayOfMonth());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        if (selectedDate.before(today)) {
            isValid = false;
            missingFields.append("Fecha válida (hoy o futuro), ");
        }

        btnSaveSecondVisit.setEnabled(isValid);
        
        // Mostrar campos faltantes si hay alguno
        if (!isValid && missingFields.length() > 0) {
            String missingFieldsStr = missingFields.toString();
            if (missingFieldsStr.endsWith(", ")) {
                missingFieldsStr = missingFieldsStr.substring(0, missingFieldsStr.length() - 2);
            }
            // Solo mostrar el mensaje si es la primera vez que se valida o si el usuario intenta guardar
            // Para evitar spam de mensajes mientras el usuario está completando el formulario
        }
    }

    private void saveSecondVisit() {
        // Validar campos antes de guardar
        if (!validateFormForSave()) {
            return;
        }

        // Formatear fecha
        String date = String.format("%04d-%02d-%02d", 
                datePickerSecondVisit.getYear(),
                datePickerSecondVisit.getMonth() + 1,
                datePickerSecondVisit.getDayOfMonth());

        // Formatear hora
        String time = String.format("%02d:%02d", 
                timePickerSecondVisit.getHour(),
                timePickerSecondVisit.getMinute());

        try {
            long result;
            if (isUpdateMode) {
                result = databaseHelper.updateSecondVisit(selectedService.getId(), date, time);
                if (result > 0) {
                    Toast.makeText(this, "Segunda visita actualizada exitosamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al actualizar la segunda visita", Toast.LENGTH_SHORT).show();
                }
            } else {
                result = databaseHelper.insertSecondVisit(
                        selectedService.getId(),
                        selectedService.getServiceType(),
                        date,
                        time,
                        selectedService.getClientCedula()
                );
                if (result > 0) {
                    Toast.makeText(this, "Segunda visita registrada exitosamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al registrar la segunda visita", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar la segunda visita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFormForSave() {
        StringBuilder missingFields = new StringBuilder();

        // Validar servicio seleccionado
        if (selectedService == null) {
            missingFields.append("Servicio de mantenimiento, ");
        }

        // Validar hora dentro del rango permitido
        int currentHour = timePickerSecondVisit.getHour();
        if (currentHour < 6 || currentHour > 19) {
            missingFields.append("Hora válida (06:00-19:00), ");
        }

        // Validar que la fecha no sea pasada
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(datePickerSecondVisit.getYear(), datePickerSecondVisit.getMonth(), datePickerSecondVisit.getDayOfMonth());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        if (selectedDate.before(today)) {
            missingFields.append("Fecha válida (hoy o futuro), ");
        }

        if (missingFields.length() > 0) {
            String missingFieldsStr = missingFields.toString();
            if (missingFieldsStr.endsWith(", ")) {
                missingFieldsStr = missingFieldsStr.substring(0, missingFieldsStr.length() - 2);
            }
            Toast.makeText(this, "Faltan campos obligatorios: " + missingFieldsStr, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void deleteSecondVisit() {
        if (selectedService == null || !isUpdateMode) {
            Toast.makeText(this, "No hay segunda visita para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int result = databaseHelper.deleteSecondVisit(selectedService.getId());
            if (result > 0) {
                Toast.makeText(this, "Segunda visita eliminada exitosamente", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar la segunda visita", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al eliminar la segunda visita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
