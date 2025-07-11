package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.puropoo.proyectobys.SmsUtils;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button btnRegisterClient, btnViewClients, btnRegisterTeam;
    Button btnRegisterRequirements, btnRegisterEquipment, btnRegisterSecondVisit;
    Button btnRegisterRequests;  // Nuevo botón para "Registrar Solicitudes"
    Button btnGuardarEquipoAInstalar;  // Nuevo botón para "Guardar Equipo a Instalar"
    Button btnRemoteSupport;  // Nuevo botón para "Soporte Técnico Remoto"
    Button btnMensajesSms;  // Botón para "Mensajes SMS"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SmsUtils.checkAndSendPendingSms(this);

        // Inicialización de botones
        btnRegisterClient = findViewById(R.id.btnRegisterClient);
        btnViewClients = findViewById(R.id.btnViewClients);
        btnRegisterTeam = findViewById(R.id.btnRegisterTeam);
        btnRegisterRequirements = findViewById(R.id.btnRegisterRequirements);
        btnRegisterEquipment = findViewById(R.id.btnRegisterEquipment);
        btnRegisterSecondVisit = findViewById(R.id.btnRegisterSecondVisit);
        btnRegisterRequests = findViewById(R.id.btnRegisterRequests);  // Botón para "Registrar Solicitudes"
        btnGuardarEquipoAInstalar = findViewById(R.id.btnGuardarEquipoAInstalar);  // Botón para "Guardar Equipo a Instalar"
        btnRemoteSupport = findViewById(R.id.btnRemoteSupport);  // Botón para "Soporte Técnico Remoto"
        btnMensajesSms = findViewById(R.id.btnMensajesSms);  // Botón para "Mensajes SMS"

        // Configura el botón de "Registrar Solicitudes"
        btnRegisterRequests.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterRequestActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Registrar Requerimientos de Mantenimiento"
        btnRegisterRequirements.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterMaintenanceRequirementsActivity.class);  // Asegúrate de que el nombre de la actividad es correcto
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

        btnRegisterEquipment.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterEquipmentActivity.class);
            startActivity(intent);
        });

        btnRegisterSecondVisit.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RegisterSecondVisitActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Guardar Equipo a Instalar"
        btnGuardarEquipoAInstalar.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, GuardarEquipoAInstalarActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Soporte Técnico Remoto"
        btnRemoteSupport.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RemoteSupportActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Mensajes SMS"
        btnMensajesSms.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MensajesSmsActivity.class);
            startActivity(intent);
        });
    }
}
