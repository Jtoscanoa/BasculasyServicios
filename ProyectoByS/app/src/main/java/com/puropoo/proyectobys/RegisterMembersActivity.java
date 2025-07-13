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
    int requestId;
    boolean isEdit;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_members);

        layoutTeamMembers = findViewById(R.id.membersContainer);
        db = new DatabaseHelper(this);

        requestId = getIntent().getIntExtra("requestId", -1);
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        if (isEdit) {
            java.util.List<TeamMember> existing = db.getTeamMembersForRequest(requestId);
            teamMembersCount = existing.size();
            for (int i = 0; i < existing.size(); i++) {
                TeamMember member = existing.get(i);
                LinearLayout memberContainer = createMemberContainer(i, member);
                layoutTeamMembers.addView(memberContainer);
            }
        } else {
            teamMembersCount = getIntent().getIntExtra("teamMembersCount", 0);
            for (int i = 0; i < teamMembersCount; i++) {
                LinearLayout memberContainer = createMemberContainer(i, null);
                layoutTeamMembers.addView(memberContainer);
            }
        }

        Button btnSaveMembers = findViewById(R.id.btnSaveMembers);
        btnSaveMembers.setOnClickListener(v -> saveMembers());
    }

    private LinearLayout createMemberContainer(int index, TeamMember prefill) {
        LinearLayout memberContainer = new LinearLayout(this);
        memberContainer.setOrientation(LinearLayout.VERTICAL);
        memberContainer.setPadding(0, 20, 0, 20);
        memberContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        Spinner spinnerRole = new Spinner(this);
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Técnico", "Pintor", "Soldador"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        spinnerRole.setBackgroundResource(R.drawable.button_border_red);
        int padding = (int) getResources().getDimensionPixelSize(R.dimen.spinner_padding);
        spinnerRole.setPadding(padding, padding, padding, padding);
        memberContainer.addView(spinnerRole);

        EditText etName = new EditText(this);
        etName.setHint("Nombre del técnico " + (index + 1));
        memberContainer.addView(etName);

        EditText etAge = new EditText(this);
        etAge.setHint("Edad del técnico " + (index + 1));
        etAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        memberContainer.addView(etAge);

        EditText etPhone = new EditText(this);
        etPhone.setHint("Teléfono del técnico " + (index + 1));
        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        memberContainer.addView(etPhone);

        EditText etPayment = new EditText(this);
        etPayment.setHint("Pago del técnico " + (index + 1));
        etPayment.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        memberContainer.addView(etPayment);

        if (prefill != null) {
            int pos = roleAdapter.getPosition(prefill.getRole());
            if (pos >= 0) spinnerRole.setSelection(pos);
            etName.setText(prefill.getName());
            etAge.setText(String.valueOf(prefill.getAge()));
            etPhone.setText(prefill.getPhone());
            etPayment.setText(String.valueOf(prefill.getPayment()));
        }

        return memberContainer;
    }

    private void saveMembers() {
        boolean allFieldsValid = true;
        boolean success = true;

        if (isEdit) {
            db.deleteTeamMembersForRequest(requestId);
        }

        for (int i = 0; i < layoutTeamMembers.getChildCount(); i++) {
            LinearLayout memberContainer = (LinearLayout) layoutTeamMembers.getChildAt(i);

            Spinner spinnerRole = (Spinner) memberContainer.getChildAt(0);
            String technicianRole = spinnerRole.getSelectedItem().toString();

            String technicianName = "";
            String technicianPhone = "";
            String technicianAge = "";
            String technicianPayment = "";

            for (int j = 1; j < memberContainer.getChildCount(); j++) {
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

            long payment = 0;
            try {
                payment = Long.parseLong(technicianPayment);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error al ingresar el pago.", Toast.LENGTH_SHORT).show();
                success = false;
                break;
            }

            int age = 0;
            try {
                age = Integer.parseInt(technicianAge);
            } catch (NumberFormatException ignore) {
            }

            TeamMember member = new TeamMember(requestId, technicianName, technicianRole, technicianPhone, age, payment);
            long id = db.insertTeamMemberForRequest(member);
            if (id == -1) {
                success = false;
                break;
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
