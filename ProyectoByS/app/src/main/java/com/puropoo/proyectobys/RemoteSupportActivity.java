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
    private TextView tvClientCedula;
    private TextView tvServiceDateTime;
    private EditText etMedio;
    private EditText etLink;
    private Button btnVerServiciosRemotos;

    private DatabaseHelper db;
    private List<Request> technicalServices;

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
        tvClientCedula = findViewById(R.id.tvClientCedula);
        tvServiceDateTime = findViewById(R.id.tvServiceDateTime);
        etMedio = findViewById(R.id.etMedio);
        etLink = findViewById(R.id.etLink);
        btnVerServiciosRemotos = findViewById(R.id.btnVerServiciosRemotos);
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
            
            String item = request.getServiceType() + " - " + formattedDate + " - " + request.getServiceTime();
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
                    Request selectedRequest = technicalServices.get(position - 1);
                    updateServiceInfo(selectedRequest);
                } else {
                    clearServiceInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearServiceInfo();
            }
        });

        // Listener para el botón
        btnVerServiciosRemotos.setOnClickListener(v -> validateAndProceed());
    }

    private void updateServiceInfo(Request request) {
        tvClientCedula.setText(request.getClientCedula());
        
        // Formatear fecha y hora
        String formattedDate = request.getServiceDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            Date date = inputFormat.parse(request.getServiceDate());
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            // Si no se puede parsear, usar la fecha original
            formattedDate = request.getServiceDate();
        }
        
        String dateTime = formattedDate + " a las " + request.getServiceTime();
        tvServiceDateTime.setText(dateTime);
    }

    private void clearServiceInfo() {
        tvClientCedula.setText("No seleccionado");
        tvServiceDateTime.setText("No seleccionado");
    }

    private void validateAndProceed() {
        // Validar que se haya seleccionado un servicio
        if (spinnerTechnicalServices.getSelectedItemPosition() <= 0) {
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

        // Si pasa todas las validaciones, navegar a la lista de servicios remotos
        // Por ahora, mostrar un mensaje de éxito y simular la navegación
        String link = etLink.getText().toString().trim();
        String successMessage = "Soporte remoto registrado exitosamente.\nMedio: " + medio;
        if (!link.isEmpty()) {
            successMessage += "\nLink: " + link;
        }
        
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
        
        // TODO: Implementar navegación a la lista de servicios remotos
        // Intent intent = new Intent(RemoteSupportActivity.this, RemoteServicesListActivity.class);
        // startActivity(intent);
        
        // Por ahora, limpiar el formulario para permitir otro registro
        clearForm();
    }

    private void clearForm() {
        spinnerTechnicalServices.setSelection(0);
        etMedio.setText("");
        etLink.setText("");
        clearServiceInfo();
    }
}