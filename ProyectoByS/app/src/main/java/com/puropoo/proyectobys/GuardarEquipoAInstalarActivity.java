package com.puropoo.proyectobys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GuardarEquipoAInstalarActivity extends AppCompatActivity {

    private Spinner spinnerServices;
    private EditText etEquipoNombre;
    private Button btnGuardar;
    private DatabaseHelper db;
    private List<Request> installationRequests;
    private int selectedRequestId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_equipo_a_instalar);

        initializeViews();
        setupDatabase();
        loadInstallationServices();
        setupListeners();
    }

    private void initializeViews() {
        spinnerServices = findViewById(R.id.spinnerServices);
        etEquipoNombre = findViewById(R.id.etEquipoNombre);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void setupDatabase() {
        db = new DatabaseHelper(this);
    }

    private void loadInstallationServices() {
        installationRequests = db.getInstallationRequestsFromToday();
        
        if (installationRequests.isEmpty()) {
            Toast.makeText(this, "No hay servicios de instalación programados desde hoy", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear lista de strings para el spinner
        List<String> spinnerItems = new ArrayList<>();
        for (Request request : installationRequests) {
            String item = "Cédula: " + request.getClientCedula() + 
                         " | Fecha: " + request.getServiceDate() + 
                         " | Hora: " + request.getServiceTime();
            spinnerItems.add(item);
        }

        // Configurar adaptador del spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServices.setAdapter(adapter);
    }

    private void setupListeners() {
        spinnerServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < installationRequests.size()) {
                    Request selectedRequest = installationRequests.get(position);
                    selectedRequestId = selectedRequest.getId();
                    
                    // Verificar si ya existe equipo para esta solicitud
                    String existingEquipo = db.getEquipoByRequestId(selectedRequestId);
                    if (existingEquipo != null && !existingEquipo.isEmpty()) {
                        // Modo edición
                        etEquipoNombre.setText(existingEquipo);
                        btnGuardar.setText("Editar");
                        isEditMode = true;
                    } else {
                        // Modo nuevo
                        etEquipoNombre.setText("");
                        btnGuardar.setText("Guardar");
                        isEditMode = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRequestId = -1;
                isEditMode = false;
                btnGuardar.setText("Guardar");
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateEquipo();
            }
        });
    }

    private void saveOrUpdateEquipo() {
        String equipoNombre = etEquipoNombre.getText().toString().trim();
        
        // Validación del campo
        if (equipoNombre.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el nombre del equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de selección de servicio
        if (selectedRequestId == -1) {
            Toast.makeText(this, "Por favor seleccione un servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // Actualizar equipo existente
            int rowsUpdated = db.updateEquipoInstalar(selectedRequestId, equipoNombre);
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Equipo actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar el equipo", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insertar nuevo equipo
            Request selectedRequest = getSelectedRequest();
            if (selectedRequest != null) {
                long id = db.insertEquipoInstalar(selectedRequestId, equipoNombre, selectedRequest.getClientCedula());
                if (id != -1) {
                    Toast.makeText(this, "Equipo guardado correctamente", Toast.LENGTH_SHORT).show();
                    // Cambiar a modo edición
                    btnGuardar.setText("Editar");
                    isEditMode = true;
                } else {
                    Toast.makeText(this, "Error al guardar el equipo", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Request getSelectedRequest() {
        int selectedPosition = spinnerServices.getSelectedItemPosition();
        if (selectedPosition >= 0 && selectedPosition < installationRequests.size()) {
            return installationRequests.get(selectedPosition);
        }
        return null;
    }
}