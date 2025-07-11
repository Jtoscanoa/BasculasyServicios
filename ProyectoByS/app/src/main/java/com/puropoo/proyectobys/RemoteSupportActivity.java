package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteSupportActivity extends AppCompatActivity {

    private Spinner spinnerTechnicalServices;
    private EditText etMedio;
    private EditText etLink;
    private Button btnSave;
    private Button btnViewRemoteServices;

    private DatabaseHelper db;
    private List<Request> technicalServices;
    private Request selectedRequest;
    private RemoteSupport existingRemoteSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_support);

        // Inicializar vistas
        initializeViews();

        // Configurar base de datos
        db = new DatabaseHelper(this);

        // Cargar servicios técnicos futuros
        loadTechnicalServices();

        // Configurar listeners
        setupListeners();
    }

    private void initializeViews() {
        spinnerTechnicalServices = findViewById(R.id.spinnerTechnicalServices);
        etMedio = findViewById(R.id.etMedio);
        etLink = findViewById(R.id.etLink);
        btnSave = findViewById(R.id.btnSave);
        btnViewRemoteServices = findViewById(R.id.btnViewRemoteServices);
    }

    private void loadTechnicalServices() {
        technicalServices = db.getFutureTechnicalServices();
        
        if (technicalServices.isEmpty()) {
            // Si no hay servicios, agregar datos dummy para demostración
            loadDummyData();
        }

        // Crear lista de strings para el spinner
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Seleccione un servicio");
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Request request : technicalServices) {
            String formattedDate = request.getServiceDate();
            
            // Convertir fecha de yyyy-MM-dd a dd/MM/yyyy
            try {
                Date date = inputFormat.parse(request.getServiceDate());
                formattedDate = outputFormat.format(date);
            } catch (ParseException e) {
                // Si no se puede parsear, usar la fecha original
                formattedDate = request.getServiceDate();
            }
            
            // Formato: Número de servicio - Fecha - Hora
            String item = request.getId() + " - " + formattedDate + " - " + request.getServiceTime();
            spinnerItems.add(item);
        }

        // Configurar adaptador del spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTechnicalServices.setAdapter(adapter);
    }

    private void loadDummyData() {
        // Agregar algunos datos dummy para demostración
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String futureDate1 = "2024-12-20";
        String futureDate2 = "2024-12-25";
        
        Request dummyRequest1 = new Request(999, "Mantenimiento Preventivo", futureDate1, "10:00", "Dirección 123", "12345678");
        Request dummyRequest2 = new Request(998, "Reparación Técnica", futureDate2, "14:30", "Dirección 456", "87654321");
        
        technicalServices.add(dummyRequest1);
        technicalServices.add(dummyRequest2);
    }

    private void setupListeners() {
        // Listener para el spinner
        spinnerTechnicalServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignorar la opción "Seleccione un servicio"
                    selectedRequest = technicalServices.get(position - 1);
                    loadExistingRemoteSupport();
                } else {
                    selectedRequest = null;
                    clearForm();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRequest = null;
                clearForm();
            }
        });

        // Listener para el botón guardar
        btnSave.setOnClickListener(v -> saveRemoteSupport());
        
        // Listener para el botón ver servicios remotos
        btnViewRemoteServices.setOnClickListener(v -> {
            Intent intent = new Intent(RemoteSupportActivity.this, RemoteSupportListActivity.class);
            startActivity(intent);
        });
    }

    private void loadExistingRemoteSupport() {
        if (selectedRequest != null) {
            existingRemoteSupport = db.getRemoteSupportByRequestId(selectedRequest.getId());
            
            if (existingRemoteSupport != null) {
                // Cargar datos existentes
                etMedio.setText(existingRemoteSupport.getMedium());
                etLink.setText(existingRemoteSupport.getLink() != null ? existingRemoteSupport.getLink() : "");
                btnSave.setText("Editar");
            } else {
                // Limpiar campos para nuevo registro
                etMedio.setText("");
                etLink.setText("");
                btnSave.setText("Guardar");
            }
        }
    }

    private void saveRemoteSupport() {
        // Validar que se haya seleccionado un servicio
        if (selectedRequest == null) {
            Toast.makeText(this, "Por favor seleccione un servicio técnico", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que se haya ingresado un medio
        String medio = etMedio.getText().toString().trim();
        if (medio.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el medio de comunicación", Toast.LENGTH_SHORT).show();
            etMedio.requestFocus();
            return;
        }

        String link = etLink.getText().toString().trim();
        
        // Obtener fecha y hora actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String currentDate = dateFormat.format(now);
        String currentTime = timeFormat.format(now);

        long result;
        if (existingRemoteSupport != null) {
            // Actualizar registro existente
            result = db.updateRemoteSupport(selectedRequest.getId(), currentDate, currentTime, medio, link);
            if (result > 0) {
                Toast.makeText(this, "Soporte remoto actualizado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar el soporte remoto", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Insertar nuevo registro
            result = db.insertRemoteSupport(selectedRequest.getId(), currentDate, currentTime, 
                                          medio, link, selectedRequest.getClientCedula());
            if (result > 0) {
                Toast.makeText(this, "Soporte remoto guardado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar el soporte remoto", Toast.LENGTH_SHORT).show();
            }
        }

        // Limpiar formulario después de guardar
        clearForm();
    }

    private void clearForm() {
        spinnerTechnicalServices.setSelection(0);
        etMedio.setText("");
        etLink.setText("");
        btnSave.setText("Guardar");
        selectedRequest = null;
        existingRemoteSupport = null;
    }
}