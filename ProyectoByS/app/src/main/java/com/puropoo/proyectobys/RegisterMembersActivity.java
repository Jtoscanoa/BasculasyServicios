package com.puropoo.proyectobys;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterMembersActivity extends AppCompatActivity {

    LinearLayout layoutTeamMembers;
    int teamMembersCount;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_members);

        layoutTeamMembers = findViewById(R.id.membersContainer);
        teamMembersCount = getIntent().getIntExtra("teamMembersCount", 0);
        db = new DatabaseHelper(this);

        if (teamMembersCount > 0) {
            for (int i = 0; i < teamMembersCount; i++) {
                // Crear contenedor para cada miembro
                LinearLayout memberContainer = new LinearLayout(this);
                memberContainer.setOrientation(LinearLayout.VERTICAL);
                memberContainer.setPadding(0, 20, 0, 20);
                memberContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                // Desplegable para seleccionar el rol
                Spinner spinnerRole = new Spinner(this);
                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Técnico", "Pintor", "Soldador"});
                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRole.setAdapter(roleAdapter);
                memberContainer.addView(spinnerRole);

                // Campo para ingresar el nombre
                EditText etName = new EditText(this);
                etName.setHint("Nombre del técnico " + (i + 1));
                memberContainer.addView(etName);

                // Campo para ingresar la edad
                EditText etAge = new EditText(this);
                etAge.setHint("Edad del técnico " + (i + 1));
                etAge.setInputType(InputType.TYPE_CLASS_NUMBER);
                memberContainer.addView(etAge);

                // Campo para ingresar el número de teléfono
                EditText etPhone = new EditText(this);
                etPhone.setHint("Teléfono del técnico " + (i + 1));
                etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
                memberContainer.addView(etPhone);

                // Campo para ingresar el pago
                EditText etPayment = new EditText(this);
                etPayment.setHint("Pago del técnico " + (i + 1));
                etPayment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                memberContainer.addView(etPayment);

                // Agregar el contenedor de este miembro al contenedor principal
                layoutTeamMembers.addView(memberContainer);
            }
        }

        // Configurar el botón para guardar los miembros
        Button btnSaveMembers = findViewById(R.id.btnSaveMembers);
        btnSaveMembers.setOnClickListener(v -> saveMembers());
    }

    private void saveMembers() {
        boolean allFieldsValid = true;
        boolean success = true;
        for (int i = 0; i < layoutTeamMembers.getChildCount(); i++) {
            LinearLayout memberContainer = (LinearLayout) layoutTeamMembers.getChildAt(i);

            String technicianRole = "", technicianName = "", technicianPhone = "", technicianAge = "", technicianPayment = "";

            // Obtener los campos de cada miembro
            for (int j = 0; j < memberContainer.getChildCount(); j++) {
                View childView = memberContainer.getChildAt(j);
                if (childView instanceof EditText) {
                    EditText etField = (EditText) childView;
                    String fieldValue = etField.getText().toString().trim();

                    if (etField.getHint().toString().contains("Nombre")) {
                        technicianName = fieldValue;
                    } else if (etField.getHint().toString().contains("Edad")) {
                        technicianAge = fieldValue;
                    } else if (etField.getHint().toString().contains("Teléfono")) {
                        technicianPhone = fieldValue;
                    } else if (etField.getHint().toString().contains("Pago")) {
                        technicianPayment = fieldValue;
                    }
                }
            }

            // Validar campos: nombre, teléfono y pago
            if (technicianName.isEmpty() || technicianPhone.isEmpty() || technicianPayment.isEmpty()) {
                allFieldsValid = false;
                break;  // Si algún campo está vacío, no continuar con el guardado
            }

            // Comprobar si el teléfono ya existe en la base de datos
            if (db.isPhoneNumberRegistered(technicianPhone)) {
                // Si el teléfono ya está registrado, mostrar un mensaje pero permitir registrar de nuevo
                Toast.makeText(this, "El teléfono " + technicianPhone + " ya está registrado, pero puede ser registrado nuevamente.", Toast.LENGTH_SHORT).show();
            } else {
                // Convertir el valor de pago a long, en lugar de int
                long payment = 0;
                try {
                    payment = Long.parseLong(technicianPayment);  // Usa Long.parseLong() en lugar de Integer.parseInt()
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Error al ingresar el pago. Asegúrese de que sea un número válido.", Toast.LENGTH_SHORT).show();
                    success = false;
                    break;
                }

                // Guardar técnico en la base de datos
                long id = db.insertTeamMember(technicianName, technicianRole, technicianPhone, Integer.parseInt(technicianAge));
                if (id == -1) {
                    success = false;  // Si el guardado falla, no continuar
                    break;
                }
            }
        }

        // Si todos los campos son válidos, mostrar mensaje de éxito y regresar
        if (allFieldsValid && success) {
            Toast.makeText(this, "Técnicos registrados correctamente.", Toast.LENGTH_LONG).show();
            finish();  // Regresar a la actividad anterior
        } else if (!allFieldsValid) {
            Toast.makeText(this, "Por favor complete todos los campos antes de guardar.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar los técnicos. Intente nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }


}
