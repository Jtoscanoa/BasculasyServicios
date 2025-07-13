package com.puropoo.proyectobys;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class EditRequestActivity extends AppCompatActivity {

    DatePicker datePickerServiceDate;
    TimePicker timePickerServiceTime;
    EditText etServiceAddress;
    Spinner spinnerServiceType;
    Button btnSaveChanges;
    DatabaseHelper db;
    Request selectedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        datePickerServiceDate = findViewById(R.id.datePickerServiceDate);
        timePickerServiceTime = findViewById(R.id.timePickerServiceTime);
        timePickerServiceTime.setIs24HourView(true);
        etServiceAddress = findViewById(R.id.etServiceAddress);
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        db = new DatabaseHelper(this);

        // Obtener el ID de la solicitud que se va a modificar
        int requestId = getIntent().getIntExtra("requestId", -1);

        // Obtener la solicitud desde la base de datos usando el ID
        selectedRequest = db.getRequestById(requestId);

        if (selectedRequest != null) {
            // Rellenar los campos con los valores actuales de la solicitud
            String[] dateParts = selectedRequest.getServiceDate().split("/");
            if (dateParts.length == 3) {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int year = Integer.parseInt(dateParts[2]);
                datePickerServiceDate.updateDate(year, month, day);
            }

            String[] timeParts = selectedRequest.getServiceTime().split(":");
            if (timeParts.length >= 2) {
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                timePickerServiceTime.setHour(hour);
                timePickerServiceTime.setMinute(minute);
            }
            setSpinnerServiceType(selectedRequest.getServiceType());
        }

        // Guardar los cambios
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServiceDate = String.format("%02d/%02d/%04d",
                        datePickerServiceDate.getDayOfMonth(),
                        datePickerServiceDate.getMonth() + 1,
                        datePickerServiceDate.getYear());

                String newServiceTime = String.format("%02d:%02d",
                        timePickerServiceTime.getHour(),
                        timePickerServiceTime.getMinute());

                int servicePos = spinnerServiceType.getSelectedItemPosition();
                if (servicePos <= 0) {
                    Toast.makeText(EditRequestActivity.this, getString(R.string.select_service), Toast.LENGTH_SHORT).show();
                    return;
                }

                String newServiceType = spinnerServiceType.getSelectedItem().toString();
                String newServiceAddress = etServiceAddress.getText().toString();  // Dirección de la solicitud

                if (!isDateValid(newServiceDate)) {
                    Toast.makeText(EditRequestActivity.this, "La fecha debe ser mayor a 3 días desde hoy.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isTimeValid(newServiceTime)) {
                    Toast.makeText(EditRequestActivity.this, "La hora debe estar entre 6am y 7pm.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Actualizar solo la dirección de la solicitud (no la del cliente)
                int rowsUpdated = db.updateRequest(selectedRequest.getId(), newServiceType, newServiceDate, newServiceTime, newServiceAddress);

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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);

        // Establecer la selección según el tipo de servicio actual
        int position = adapter.getPosition(serviceType);
        spinnerServiceType.setSelection(position);
    }

    private boolean isDateValid(String serviceDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(serviceDate);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 3);
            Date minDate = calendar.getTime();
            return date.after(minDate);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTimeValid(String serviceTime) {
        try {
            if (!serviceTime.matches("^(0[6-9]|1[0-7]):[0-5][0-9]$")) {
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date time = sdf.parse(serviceTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour >= 6 && hour <= 19;
        } catch (Exception e) {
            return false;
        }
    }

}
