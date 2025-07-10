package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etCedula, etPhone, etAddress;
    private Button btnRegister;
    private Button btnRegisterSecondVisit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) Vincula tus vistas con su ID
        etName        = findViewById(R.id.etName);
        etCedula      = findViewById(R.id.etCedula);
        etPhone       = findViewById(R.id.etPhone);
        etAddress     = findViewById(R.id.etAddress);
        btnRegister   = findViewById(R.id.btnRegister);
        btnRegisterSecondVisit = findViewById(R.id.btnRegisterSecondVisit);

        // 2) Crea tu helper de base de datos
        dbHelper = new DatabaseHelper(this);

        // 3) Al hacer click en “Registrar Cliente”
        btnRegister.setOnClickListener(v -> {
            String name        = etName.getText().toString().trim();
            String cedula      = etCedula.getText().toString().trim();
            String phone       = etPhone.getText().toString().trim();
            String address     = etAddress.getText().toString().trim();

            // 4) Validaciones
            if (name.isEmpty() || cedula.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cedula.matches("\\d+")) {
                Toast.makeText(this, "La cédula debe contener sólo números", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!phone.matches("\\d{10}")) {
                Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.clientExists(cedula)) {
                Toast.makeText(this, "Ya existe un cliente con esa cédula", Toast.LENGTH_LONG).show();
                return;
            }

            // 5) Inserta el cliente (nota la firma con 5 parámetros)
            long id = dbHelper.insertClient(name, cedula, phone, address);
            if (id != -1) {
                Toast.makeText(this,
                        "Cliente registrado correctamente",
                        Toast.LENGTH_LONG
                ).show();

                // 6) Limpia los campos
                etName.setText("");
                etCedula.setText("");
                etPhone.setText("");
                etAddress.setText("");
            } else {
                Toast.makeText(this,
                        "Error al registrar cliente",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        Button btnViewEditClients = findViewById(R.id.btnViewEditClients);

// Configurar el clic del botón
        btnViewEditClients.setOnClickListener(v -> {
            // Redirige a la actividad que muestra la lista de clientes
            Intent intent = new Intent(MainActivity.this, ClientsListActivity.class);
            startActivity(intent);
        });

        // Configurar el clic del botón "Registrar Segunda Visita"
        btnRegisterSecondVisit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterSecondVisitActivity.class);
            startActivity(intent);
        });

    }
}
