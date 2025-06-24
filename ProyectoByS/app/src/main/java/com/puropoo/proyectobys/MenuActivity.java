package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button btnRegisterClient, btnViewClients, btnRegisterTeam;
    Button btnRegisterRequirements, btnRegisterEquipment, btnRegisterSecondVisit;
    Button btnRegisterRequests;  // Nuevo botón para "Registrar Solicitudes"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicialización de botones
        btnRegisterClient = findViewById(R.id.btnRegisterClient);
        btnViewClients = findViewById(R.id.btnViewClients);
        btnRegisterTeam = findViewById(R.id.btnRegisterTeam);
        btnRegisterRequirements = findViewById(R.id.btnRegisterRequirements);
        btnRegisterEquipment = findViewById(R.id.btnRegisterEquipment);
        btnRegisterSecondVisit = findViewById(R.id.btnRegisterSecondVisit);
        btnRegisterRequests = findViewById(R.id.btnRegisterRequests);  // Botón para "Registrar Solicitudes"

        // Configura el botón de "Registrar Solicitudes"
        btnRegisterRequests.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterRequestActivity.class);
            startActivity(intent);
        });

        // Otros botones (sin cambios)
        btnRegisterClient.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnViewClients.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ClientsListActivity.class);
            startActivity(intent);
        });

        // Otros botones (registrar equipo, etc.)
        btnRegisterTeam.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterTeamActivity.class);
            startActivity(intent);
        });

        btnRegisterRequirements.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterRequirementsActivity.class);
            startActivity(intent);
        });

        btnRegisterEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterEquipmentActivity.class);
            startActivity(intent);
        });

        btnRegisterSecondVisit.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterSecondVisitActivity.class);
            startActivity(intent);
        });
    }
}
