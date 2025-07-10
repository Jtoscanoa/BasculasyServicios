package com.puropoo.proyectobys;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterSecondVisitActivity extends AppCompatActivity {

    private Spinner spinnerServices;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnSave, btnUpdate, btnDelete;
    
    private DatabaseHelper dbHelper;
    private List<Request> maintenanceRequests;
    private ArrayAdapter<String> spinnerAdapter;
    private Request selectedRequest = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second_visit);

        initializeViews();
        setupDatabase();
        loadMaintenanceRequests();
        setupDateTimeLimits();
        setupListeners();
    }

    private void initializeViews() {
        spinnerServices = findViewById(R.id.spinnerServices);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void loadMaintenanceRequests() {
        maintenanceRequests = dbHelper.getMaintenanceRequests();
        
        if (maintenanceRequests.isEmpty()) {
            Toast.makeText(this, "No hay servicios de mantenimiento registrados", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Crear lista de strings para el spinner
        List<String> requestStrings = new ArrayList<>();
        requestStrings.add("Seleccionar servicio...");
        
        for (Request request : maintenanceRequests) {
            String displayText = String.format("ID: %d - %s - %s %s", 
                request.getId(), 
                request.getServiceType(), 
                request.getServiceDate(), 
                request.getServiceTime());
            requestStrings.add(displayText);
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requestStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(spinnerAdapter);
    }

    private void setupDateTimeLimits() {
        // Configurar fecha mínima como hoy
        Calendar today = Calendar.getInstance();
        datePicker.setMinDate(today.getTimeInMillis());

        // Configurar TimePicker para formato 24 horas
        timePicker.setIs24HourView(true);
        
        // Establecer hora por defecto a 08:00
        timePicker.setHour(8);
        timePicker.setMinute(0);
    }

    private void setupListeners() {
        spinnerServices.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) { // Ignorar el primer elemento "Seleccionar servicio..."
                    selectedRequest = maintenanceRequests.get(position - 1);
                    checkExistingSecondVisit();
                } else {
                    selectedRequest = null;
                    resetToCreateMode();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedRequest = null;
                resetToCreateMode();
            }
        });

        btnSave.setOnClickListener(v -> saveSecondVisit());
        btnUpdate.setOnClickListener(v -> updateSecondVisit());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void checkExistingSecondVisit() {
        if (selectedRequest != null && dbHelper.hasSecondVisit(selectedRequest.getId())) {
            // Cargar datos existentes
            String[] visitData = dbHelper.getSecondVisitData(selectedRequest.getId());
            if (visitData != null && visitData.length == 2) {
                loadExistingVisitData(visitData[0], visitData[1]);
                switchToEditMode();
            }
        } else {
            resetToCreateMode();
        }
    }

    private void loadExistingVisitData(String visitDate, String visitTime) {
        try {
            // Cargar fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = dateFormat.parse(visitDate);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                );
            }

            // Cargar hora
            String[] timeParts = visitTime.split(":");
            if (timeParts.length == 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos existentes", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchToEditMode() {
        isEditMode = true;
        btnSave.setVisibility(android.view.View.GONE);
        btnUpdate.setVisibility(android.view.View.VISIBLE);
        btnDelete.setVisibility(android.view.View.VISIBLE);
    }

    private void resetToCreateMode() {
        isEditMode = false;
        btnSave.setVisibility(android.view.View.VISIBLE);
        btnUpdate.setVisibility(android.view.View.GONE);
        btnDelete.setVisibility(android.view.View.GONE);
        
        // Resetear fecha y hora a valores por defecto
        setupDateTimeLimits();
    }

    private boolean validateInput() {
        if (selectedRequest == null) {
            Toast.makeText(this, "Por favor seleccione un servicio de mantenimiento", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar restricción de horario (6:00 - 19:00)
        int hour = timePicker.getHour();
        if (hour < 6 || hour > 19) {
            Toast.makeText(this, "La hora debe estar entre 06:00 y 19:00", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar que la fecha no sea anterior a hoy
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if (selectedDate.before(today)) {
            Toast.makeText(this, "La fecha no puede ser anterior a hoy", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getFormattedDate() {
        return String.format(Locale.getDefault(), "%02d/%02d/%04d", 
            datePicker.getDayOfMonth(), 
            datePicker.getMonth() + 1, 
            datePicker.getYear());
    }

    private String getFormattedTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", 
            timePicker.getHour(), 
            timePicker.getMinute());
    }

    private void saveSecondVisit() {
        if (!validateInput()) {
            return;
        }

        String visitDate = getFormattedDate();
        String visitTime = getFormattedTime();

        long result = dbHelper.insertSecondVisit(selectedRequest.getId(), visitDate, visitTime);
        
        if (result != -1) {
            Toast.makeText(this, "Segunda visita registrada correctamente", Toast.LENGTH_LONG).show();
            checkExistingSecondVisit(); // Cambiar a modo edición
        } else {
            Toast.makeText(this, "Error al registrar la segunda visita", Toast.LENGTH_LONG).show();
        }
    }

    private void updateSecondVisit() {
        if (!validateInput()) {
            return;
        }

        String visitDate = getFormattedDate();
        String visitTime = getFormattedTime();

        int result = dbHelper.updateSecondVisit(selectedRequest.getId(), visitDate, visitTime);
        
        if (result > 0) {
            Toast.makeText(this, "Segunda visita actualizada correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error al actualizar la segunda visita", Toast.LENGTH_LONG).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Está seguro de que desea eliminar este registro de segunda visita?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteSecondVisit())
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteSecondVisit() {
        if (selectedRequest == null) {
            return;
        }

        int result = dbHelper.deleteSecondVisit(selectedRequest.getId());
        
        if (result > 0) {
            Toast.makeText(this, "Segunda visita eliminada correctamente", Toast.LENGTH_LONG).show();
            resetToCreateMode();
        } else {
            Toast.makeText(this, "Error al eliminar la segunda visita", Toast.LENGTH_LONG).show();
        }
    }
}
