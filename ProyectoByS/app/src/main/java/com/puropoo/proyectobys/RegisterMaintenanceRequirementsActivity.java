package com.puropoo.proyectobys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegisterMaintenanceRequirementsActivity extends AppCompatActivity {

    private Spinner spinnerServices;
    private EditText etRequirements;
    private Button btnSaveRequirements;

    private DatabaseHelper db;
    private List<Request> maintenanceRequests;

    private boolean isEditing = false;  // Estado para saber si estamos editando
    private boolean hasExistingRequirements = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maintenance_requirements);

        spinnerServices = findViewById(R.id.spinnerServices);
        etRequirements = findViewById(R.id.etRequirements);
        btnSaveRequirements = findViewById(R.id.btnSaveRequirements);

        db = new DatabaseHelper(this);

        // Cargar los servicios de mantenimiento en el spinner
        loadMaintenanceServices();

        btnSaveRequirements.setOnClickListener(v -> handleButtonClick());
    }

    // Método para cargar los servicios de mantenimiento en el Spinner
    private void loadMaintenanceServices() {
        maintenanceRequests = db.getAllRequests();  // Obtener todos los servicios de mantenimiento
        List<String> serviceNames = new ArrayList<>();
        for (Request request : maintenanceRequests) {
            // Filtrar solo los servicios de mantenimiento
            if (request.getServiceType() != null && request.getServiceType().toLowerCase().contains("mantenimiento")) {
                serviceNames.add(request.getServiceType() + " - " + request.getServiceDate());
            }
        }
        // Crear el adaptador para el Spinner
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceNames);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(serviceAdapter);

        // Cargar los requerimientos existentes para el servicio seleccionado (si existen)
        if (spinnerServices.getSelectedItem() != null) {
            String selectedService = spinnerServices.getSelectedItem().toString();
            loadExistingRequirements(selectedService);
        }

        spinnerServices.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String service = parent.getItemAtPosition(position).toString();
                loadExistingRequirements(service);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    // Método para cargar los requerimientos existentes en el campo de texto
    private void loadExistingRequirements(String selectedService) {
        // Buscar los requerimientos para el servicio seleccionado
        String requirements = db.getRequirementsForService(selectedService);

        // Agregar un Log para depurar si estamos obteniendo correctamente los requerimientos
        Log.d("LoadExistingRequirements", "Servicio seleccionado: " + selectedService);
        Log.d("LoadExistingRequirements", "Requerimientos encontrados: " + requirements);

        if (requirements != null && !requirements.isEmpty()) {
            hasExistingRequirements = true;
            isEditing = false;
            etRequirements.setText(requirements);  // Establecer el texto en el campo de texto
            etRequirements.setEnabled(false);
            btnSaveRequirements.setText("Editar Requerimientos");
        } else {
            Log.d("LoadExistingRequirements", "No se encontraron requerimientos para el servicio seleccionado");
            hasExistingRequirements = false;
            isEditing = true;
            etRequirements.setText("");
            etRequirements.setEnabled(true);
            btnSaveRequirements.setText("Guardar Requerimientos");
        }
    }


    // Método para guardar o actualizar los requerimientos
    private void saveRequirements() {
        // Obtener el texto de los requerimientos
        String requirements = etRequirements.getText().toString().trim();

        // Verificar que no esté vacío
        if (requirements.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese los requerimientos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el servicio seleccionado
        String selectedService = spinnerServices.getSelectedItem().toString();

        // Verificar si los requerimientos ya existen para este servicio
        String existingRequirements = db.getRequirementsForService(selectedService);

        boolean isSaved;

        if (existingRequirements != null) {
            // Si ya existen, actualizar los requerimientos
            isSaved = db.updateMaintenanceRequirements(selectedService, requirements);
        } else {
            // Si no existen, insertar los nuevos requerimientos
            isSaved = db.insertMaintenanceRequirements(selectedService, requirements);
        }

        if (isSaved) {
            Toast.makeText(this, "Requerimientos guardados correctamente", Toast.LENGTH_SHORT).show();
            hasExistingRequirements = true;
        } else {
            Toast.makeText(this, "Error al guardar los requerimientos", Toast.LENGTH_SHORT).show();
        }
    }

    // Manejador del botón único para editar o guardar
    private void handleButtonClick() {
        if (hasExistingRequirements && !isEditing) {
            // Permitir edición
            etRequirements.setEnabled(true);
            btnSaveRequirements.setText("Guardar Cambios");
            isEditing = true;
        } else {
            saveRequirements();
            etRequirements.setEnabled(false);
            btnSaveRequirements.setText("Editar Requerimientos");
            isEditing = false;
        }
    }



    // Método onResume() para recargar los requerimientos cuando la actividad se vuelve a mostrar
    @Override
    protected void onResume() {
        super.onResume();

        // Obtener el servicio seleccionado
        String selectedService = spinnerServices.getSelectedItem().toString();
        loadExistingRequirements(selectedService);
    }


}
