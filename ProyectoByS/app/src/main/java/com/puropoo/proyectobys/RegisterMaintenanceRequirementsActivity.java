package com.puropoo.proyectobys;

import android.os.Bundle;
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

        btnSaveRequirements.setOnClickListener(v -> saveRequirements());
    }

    // Método para cargar los servicios de mantenimiento en el Spinner
    private void loadMaintenanceServices() {
        maintenanceRequests = db.getAllRequests();  // Obtener todos los servicios de mantenimiento
        List<String> serviceNames = new ArrayList<>();
        for (Request request : maintenanceRequests) {
            // Filtrar solo los servicios de mantenimiento
            if (request.getServiceType().equalsIgnoreCase("Mantenimiento")) {
                serviceNames.add(request.getServiceType() + " - " + request.getServiceDate());
            }
        }
        // Crear el adaptador para el Spinner
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceNames);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(serviceAdapter);
    }

    // Método para guardar los requerimientos
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

        // Guardar los requerimientos en la base de datos
        boolean isSaved = db.insertMaintenanceRequirements(selectedService, requirements);

        if (isSaved) {
            Toast.makeText(this, "Requerimientos guardados correctamente", Toast.LENGTH_SHORT).show();
            clearFields();  // Limpiar los campos después de guardar
        } else {
            Toast.makeText(this, "Error al guardar los requerimientos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para limpiar los campos
    private void clearFields() {
        etRequirements.setText("");
        spinnerServices.setSelection(0);  // Seleccionar el primer servicio
    }
}
