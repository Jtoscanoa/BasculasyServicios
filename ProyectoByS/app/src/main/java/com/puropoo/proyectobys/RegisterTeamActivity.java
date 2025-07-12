package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegisterTeamActivity extends AppCompatActivity {

    Spinner spinnerRequests;
    Button btnManageMembers;
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
        btnManageMembers = findViewById(R.id.btnManageMembers);

        db = new DatabaseHelper(this);

        // Cargar las solicitudes en el spinner
        loadRequestsIntoSpinner();

        btnManageMembers.setOnClickListener(v -> openMembersScreen());
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

        spinnerRequests.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < requestsList.size()) {
                    Request req = requestsList.get(position);
                    boolean hasTeam = isTeamAssignedToRequest(req.getId());
                    btnManageMembers.setText(hasTeam ? "Editar Integrantes" : "Registrar Integrantes");
                    etTeamMembersCount.setEnabled(!hasTeam);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void openMembersScreen() {
        int position = spinnerRequests.getSelectedItemPosition();
        if (position == -1) {
            Toast.makeText(this, "Seleccione una solicitud", Toast.LENGTH_SHORT).show();
            return;
        }

        Request selectedRequest = requestsList.get(position);
        boolean hasTeam = isTeamAssignedToRequest(selectedRequest.getId());

        int membersCount = 0;
        if (!hasTeam) {
            String countStr = etTeamMembersCount.getText().toString().trim();
            if (countStr.isEmpty()) {
                Toast.makeText(this, "Ingrese la cantidad de miembros", Toast.LENGTH_SHORT).show();
                return;
            }
            membersCount = Integer.parseInt(countStr);
            if (membersCount <= 0) {
                Toast.makeText(this, "Cantidad inválida de miembros", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(this, RegisterMembersActivity.class);
        intent.putExtra("requestId", selectedRequest.getId());
        intent.putExtra("isEdit", hasTeam);
        if (!hasTeam) intent.putExtra("teamMembersCount", membersCount);
        startActivity(intent);
    }

    // Verificar si ya se han asignado técnicos a la solicitud
    private boolean isTeamAssignedToRequest(int requestId) {
        return db.isTeamAssignedToRequest(requestId); // Usar el método con el tipo correcto
    }

}
