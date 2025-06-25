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

public class RegisterTeamActivity extends AppCompatActivity {

    Spinner spinnerRequests, spinnerTechnicianRole;
    EditText etTechnicianName, etTechnicianPhone;
    Button btnSaveTeam;

    DatabaseHelper db;
    List<Request> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);  // Asegúrate de tener este layout

        // Inicializamos los Spinners y EditTexts
        spinnerRequests = findViewById(R.id.spinnerRequests);
        spinnerTechnicianRole = findViewById(R.id.spinnerTechnicianRole);  // Usar Spinner para los roles
        etTechnicianName = findViewById(R.id.etTechnicianName);
        etTechnicianPhone = findViewById(R.id.etTechnicianPhone);
        btnSaveTeam = findViewById(R.id.btnSaveTeam);

        db = new DatabaseHelper(this);

        // Cargar las solicitudes en el spinner
        loadRequestsIntoSpinner();

        // Configurar roles de técnicos/pintores en el spinner
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.technician_roles, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTechnicianRole.setAdapter(roleAdapter);

        // Guardar el equipo técnico
        btnSaveTeam.setOnClickListener(v -> saveTeam());
    }

    // Método para cargar las solicitudes en el Spinner
    private void loadRequestsIntoSpinner() {
        // Obtener todas las solicitudes
        requestsList = db.getAllRequests();
        List<String> requestNames = new ArrayList<>();
        for (Request request : requestsList) {
            // Formato: "Nombre del cliente - Fecha"
            requestNames.add(request.getServiceType() + " - " + request.getServiceDate());
        }
        // Crear el adaptador para el Spinner
        ArrayAdapter<String> requestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, requestNames);
        requestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRequests.setAdapter(requestAdapter);
    }

    private void saveTeam() {
        // Validar campos
        String technicianName = etTechnicianName.getText().toString().trim();
        String technicianRole = spinnerTechnicianRole.getSelectedItem().toString();  // Usar el Spinner para obtener el rol
        String technicianPhone = etTechnicianPhone.getText().toString().trim();
        int selectedRequestPosition = spinnerRequests.getSelectedItemPosition();

        if (technicianName.isEmpty() || technicianRole.isEmpty() || technicianPhone.isEmpty() || selectedRequestPosition == -1) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la solicitud seleccionada
        Request selectedRequest = requestsList.get(selectedRequestPosition);
        String clientCedula = db.getClientCedulaForRequest(selectedRequest.getId());

        // Insertar el miembro del equipo
        long id = db.insertTeamMember(technicianName, technicianRole, technicianPhone, clientCedula);

        if (id != -1) {
            Toast.makeText(this, "Miembro del equipo registrado correctamente", Toast.LENGTH_LONG).show();
            clearFields();  // Limpiar los campos después de registrar
        } else {
            Toast.makeText(this, "Error al registrar el miembro del equipo", Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        // Limpiar los campos de entrada
        etTechnicianName.setText("");
        etTechnicianPhone.setText("");
    }
}
