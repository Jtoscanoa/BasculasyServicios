package com.puropoo.proyectobys;

import android.content.Intent;
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

    Spinner spinnerRequests;
    Button btnSaveTeam, btnRegisterMembers;
    EditText etTeamMembersCount;

    DatabaseHelper db;
    List<Request> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);  // Asegúrate de tener este layout

        // Inicializamos los Spinners y EditTexts
        spinnerRequests = findViewById(R.id.spinnerRequests);
        etTeamMembersCount = findViewById(R.id.etTeamMembersCount);
        btnSaveTeam = findViewById(R.id.btnSaveTeam);
        btnRegisterMembers = findViewById(R.id.btnRegisterMembers);

        db = new DatabaseHelper(this);

        // Cargar las solicitudes en el spinner
        loadRequestsIntoSpinner();

        btnRegisterMembers.setOnClickListener(v -> {
            // Obtener el número de miembros del equipo
            String teamMembersCountStr = etTeamMembersCount.getText().toString().trim();
            int teamMembersCount = 0;

            if (!teamMembersCountStr.isEmpty()) {
                teamMembersCount = Integer.parseInt(teamMembersCountStr);
            }

            // Verificar que el valor sea mayor que 0
            if (teamMembersCount > 0) {
                Intent intent = new Intent(RegisterTeamActivity.this, RegisterMembersActivity.class);
                intent.putExtra("teamMembersCount", teamMembersCount);  // Pasar la cantidad de miembros
                startActivity(intent);
            } else {
                Toast.makeText(RegisterTeamActivity.this, "Por favor ingresa un número válido de miembros", Toast.LENGTH_SHORT).show();
            }
        });

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
        String teamMembersCountStr = etTeamMembersCount.getText().toString().trim();
        int teamMembersCount = 0;

        // Verificar si el campo está vacío y asignar el valor
        if (!teamMembersCountStr.isEmpty()) {
            teamMembersCount = Integer.parseInt(teamMembersCountStr);
        }

        int selectedRequestPosition = spinnerRequests.getSelectedItemPosition();

        if (selectedRequestPosition == -1 || teamMembersCount == 0) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la solicitud seleccionada
        Request selectedRequest = requestsList.get(selectedRequestPosition);

        // Verificar si ya hay técnicos asignados a esta solicitud
        if (isTeamAssignedToRequest(selectedRequest.getId())) {
            Toast.makeText(this, "Ya se han asignado técnicos a esta solicitud.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar los técnicos si no hay ninguno asignado
        long id = db.insertTeamMember("No asignado", "Rol no asignado", "", teamMembersCount);

        if (id != -1) {
            Toast.makeText(this, "Miembros del equipo registrados correctamente", Toast.LENGTH_LONG).show();
            clearFields();  // Limpiar los campos después de registrar
        } else {
            Toast.makeText(this, "Error al registrar los miembros del equipo", Toast.LENGTH_LONG).show();
        }
    }

    // Verificar si ya se han asignado técnicos a la solicitud
    private boolean isTeamAssignedToRequest(int requestId) {
        return db.isTeamAssignedToRequest(requestId); // Usar el método con el tipo correcto
    }

    private void clearFields() {
        etTeamMembersCount.setText("");
        spinnerRequests.setSelection(0);
    }
}
