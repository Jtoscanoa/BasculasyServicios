package com.puropoo.proyectobys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterTeamActivity extends AppCompatActivity {

    EditText editName, editPhone, editAge, editPayment;
    Spinner spinnerRole;
    Button btnSave;
    SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);

        editName = findViewById(R.id.editName);
        spinnerRole = findViewById(R.id.spinnerRole);
        editPhone = findViewById(R.id.editPhone);
        editAge = findViewById(R.id.editAge);
        editPayment = findViewById(R.id.editPayment);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new SQLiteHelper(this);

        // Configurar Spinner con opciones
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Seleccione cargo", "Pintor", "Técnico"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String role = spinnerRole.getSelectedItem().toString();

                // Validación de campos obligatorios
                if (role.equals("Seleccione cargo") ||
                        TextUtils.isEmpty(editName.getText()) ||
                        TextUtils.isEmpty(editPhone.getText()) ||
                        TextUtils.isEmpty(editAge.getText()) ||
                        TextUtils.isEmpty(editPayment.getText())) {

                    Toast.makeText(RegisterTeamActivity.this, "Debe completar todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = editName.getText().toString();
                String phone = editPhone.getText().toString();
                int age = Integer.parseInt(editAge.getText().toString());
                double payment = Double.parseDouble(editPayment.getText().toString());

                // Guardar en BD
                boolean inserted = dbHelper.insertTeamMember(name, role, phone, age, payment);

                if (inserted) {
                    Toast.makeText(RegisterTeamActivity.this, "Equipo técnico registrado correctamente.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterTeamActivity.this, "Error al registrar el equipo técnico.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
