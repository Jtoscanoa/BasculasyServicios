package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MensajesSmsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes_sms);
        Button btnEquipo = findViewById(R.id.btnEquipoTecnico);
        Button btnTecnico = findViewById(R.id.btnTecnico);
        Button btnCliente = findViewById(R.id.btnCliente);

        btnEquipo.setOnClickListener(v -> openManagement("equipo"));
        btnTecnico.setOnClickListener(v -> openManagement("tecnico"));
        btnCliente.setOnClickListener(v -> openManagement("cliente"));
    }

    private void openManagement(String type) {
        Intent intent = new Intent(this, SmsManagementActivity.class);
        intent.putExtra("recipientType", type);
        startActivity(intent);
    }
}
