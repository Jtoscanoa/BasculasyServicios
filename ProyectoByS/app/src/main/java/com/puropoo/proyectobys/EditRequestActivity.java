package com.puropoo.proyectobys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class EditRequestActivity extends AppCompatActivity {

    EditText etServiceDate, etServiceTime;
    Spinner spinnerServiceType;
    Button btnSaveChanges;
    DatabaseHelper db;
    Request selectedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        etServiceDate = findViewById(R.id.etServiceDate);
        etServiceTime = findViewById(R.id.etServiceTime);
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        db = new DatabaseHelper(this);

        // Obtener el ID de la solicitud que se va a modificar
        int requestId = getIntent().getIntExtra("requestId", -1);

        // Obtener la solicitud desde la base de datos usando el ID
        selectedRequest = db.getRequestById(requestId);

        if (selectedRequest != null) {
            // Rellenar los campos con los valores actuales de la solicitud
            etServiceDate.setText(selectedRequest.getServiceDate());
            etServiceTime.setText(selectedRequest.getServiceTime());
            setSpinnerServiceType(selectedRequest.getServiceType());
        }

        // Guardar los cambios
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServiceDate = etServiceDate.getText().toString();
                String newServiceTime = etServiceTime.getText().toString();
                String newServiceType = spinnerServiceType.getSelectedItem().toString();

                // Validación de campos vacíos
                if (newServiceDate.isEmpty() || newServiceTime.isEmpty() || newServiceType.isEmpty()) {
                    Toast.makeText(EditRequestActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizar la solicitud en la base de datos
                int rowsUpdated = db.updateRequest(selectedRequest.getId(), newServiceType, newServiceDate, newServiceTime);

                if (rowsUpdated > 0) {
                    Toast.makeText(EditRequestActivity.this, "Solicitud actualizada correctamente", Toast.LENGTH_SHORT).show();
                    finish();  // Volver a la actividad anterior
                } else {
                    Toast.makeText(EditRequestActivity.this, "Error al actualizar la solicitud", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para configurar el Spinner según el servicio actual
    private void setSpinnerServiceType(String serviceType) {
        // Configura el Spinner con el tipo de servicio actual
        // Aquí asumo que tienes tres opciones de servicio: "Servicio Técnico", "Servicio de Mantenimiento", "Servicio de Instalación"
        String[] services = getResources().getStringArray(R.array.service_types);
        for (int i = 0; i < services.length; i++) {
            if (services[i].equals(serviceType)) {
                spinnerServiceType.setSelection(i);
                break;
            }
        }
    }
}
