package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.puropoo.proyectobys.SmsUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;

public class MenuActivity extends AppCompatActivity {

    Button btnRegisterClient, btnViewClients, btnRegisterTeam;
    Button btnRegisterRequirements, btnRegisterEquipment, btnRegisterSecondVisit;
    Button btnRegisterRequests;  // Nuevo botón para "Registrar Solicitudes"
    Button btnGuardarEquipoAInstalar;  // Nuevo botón para "Guardar Equipo a Instalar"
    Button btnRemoteSupport;  // Nuevo botón para "Soporte Técnico Remoto"
    Button btnMensajesSms;  // Botón para "Mensajes SMS"

    private Button[] menuButtons;
    private int defaultHeight;
    private int selectedHeight;
    private ViewGroup menuContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        SmsUtils.checkAndSendPendingSms(this);

        menuContainer = findViewById(R.id.menuContainer);

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

        menuButtons = new Button[] {
                btnRegisterClient,
                btnViewClients,
                btnRegisterTeam,
                btnRegisterRequirements,
                btnRegisterEquipment,
                btnRegisterSecondVisit,
                btnRegisterRequests,
                btnGuardarEquipoAInstalar,
                btnRemoteSupport,
                btnMensajesSms
        };

        defaultHeight = getResources().getDimensionPixelSize(R.dimen.button_default_height);
        selectedHeight = getResources().getDimensionPixelSize(R.dimen.button_selected_height);

        for (Button b : menuButtons) {
            ViewGroup.LayoutParams lp = b.getLayoutParams();
            if (lp != null) {
                lp.height = defaultHeight;
                b.setLayoutParams(lp);
            }
        }

        // Configura el botón de "Registrar Solicitudes"
        btnRegisterRequests.setOnClickListener(v -> {
            selectButton(btnRegisterRequests);
            Intent intent = new Intent(MenuActivity.this, RegisterRequestActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Registrar Requerimientos de Mantenimiento"
        btnRegisterRequirements.setOnClickListener(v -> {
            selectButton(btnRegisterRequirements);
            Intent intent = new Intent(MenuActivity.this, RegisterMaintenanceRequirementsActivity.class);  // Asegúrate de que el nombre de la actividad es correcto
            startActivity(intent);
        });

        // Otros botones (sin cambios)
        btnRegisterClient.setOnClickListener(v -> {
            selectButton(btnRegisterClient);
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnViewClients.setOnClickListener(v -> {
            selectButton(btnViewClients);
            Intent intent = new Intent(MenuActivity.this, ClientsListActivity.class);
            startActivity(intent);
        });

        // Otros botones (registrar equipo, etc.)
        btnRegisterTeam.setOnClickListener(v -> {
            selectButton(btnRegisterTeam);
            Intent intent = new Intent(MenuActivity.this, RegisterTeamActivity.class);
            startActivity(intent);
        });

        btnRegisterEquipment.setOnClickListener(v -> {
            selectButton(btnRegisterEquipment);
            Intent intent = new Intent(MenuActivity.this, RegisterEquipmentActivity.class);
            startActivity(intent);
        });

        btnRegisterSecondVisit.setOnClickListener(v -> {
            selectButton(btnRegisterSecondVisit);
            Intent intent = new Intent(MenuActivity.this, RegisterSecondVisitActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Guardar Equipo a Instalar"
        btnGuardarEquipoAInstalar.setOnClickListener(v -> {
            selectButton(btnGuardarEquipoAInstalar);
            Intent intent = new Intent(MenuActivity.this, GuardarEquipoAInstalarActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Soporte Técnico Remoto"
        btnRemoteSupport.setOnClickListener(v -> {
            selectButton(btnRemoteSupport);
            Intent intent = new Intent(MenuActivity.this, RemoteSupportActivity.class);
            startActivity(intent);
        });

        // Configura el botón de "Mensajes SMS"
        btnMensajesSms.setOnClickListener(v -> {
            selectButton(btnMensajesSms);
            Intent intent = new Intent(MenuActivity.this, MensajesSmsActivity.class);
            startActivity(intent);
        });
    }

    private void selectButton(Button selected) {
        TransitionManager.beginDelayedTransition(menuContainer);
        for (Button b : menuButtons) {
            ViewGroup.LayoutParams lp = b.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, defaultHeight);
            }
            if (b == selected) {
                b.setBackgroundResource(R.drawable.button_border_red_selected);
                b.setTextColor(Color.BLACK);
                lp.height = selectedHeight;
            } else {
                b.setBackgroundResource(R.drawable.button_border_red);
                b.setTextColor(Color.BLACK);
                lp.height = defaultHeight;
            }
            b.setLayoutParams(lp);
        }
    }
}
