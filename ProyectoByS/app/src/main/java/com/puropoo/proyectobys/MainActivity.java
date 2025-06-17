package com.puropoo.proyectobys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etCedula, etPhone, etAddress;
    private Spinner spServiceType;
    private Button btnRegister;
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
        spServiceType = findViewById(R.id.spServiceType);
        btnRegister   = findViewById(R.id.btnRegister);

        // 2) Crea tu helper de base de datos
        dbHelper = new DatabaseHelper(this);

        // 3) Al hacer click en “Registrar Cliente”
        btnRegister.setOnClickListener(v -> {
            String name        = etName.getText().toString().trim();
            String cedula      = etCedula.getText().toString().trim();
            String phone       = etPhone.getText().toString().trim();
            String address     = etAddress.getText().toString().trim();
            String serviceType = spServiceType.getSelectedItem().toString();

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
            long id = dbHelper.insertClient(name, cedula, phone, address, serviceType);
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
                spServiceType.setSelection(0);
            } else {
                Toast.makeText(this,
                        "Error al registrar cliente",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
