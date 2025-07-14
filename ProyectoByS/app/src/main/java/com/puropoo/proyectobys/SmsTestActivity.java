package com.puropoo.proyectobys;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SmsTestActivity extends AppCompatActivity {

    // Replace with your n8n webhook URL
    private static final String WEBHOOK_URL = "http://localhost:5678/webhook/REPLACE_WITH_UUID/enviar-sms";

    private Spinner spinnerType;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private EditText etPhone;
    private EditText etMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_funcional);

        spinnerType = findViewById(R.id.spinnerServiceTypeTest);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        timePicker = findViewById(R.id.timePickerServiceTimeTest);
        datePicker = findViewById(R.id.datePickerSendDate);
        etPhone = findViewById(R.id.etPhoneTest);
        etMessage = findViewById(R.id.etOptionalMessage);
        btnSend = findViewById(R.id.btnSendTest);

        btnSend.setOnClickListener(v -> sendTestSms());
    }

    private void sendTestSms() {
        String phone = etPhone.getText().toString().trim();
        if (spinnerType.getSelectedItemPosition() == 0 || phone.isEmpty()) {
            Toast.makeText(this, "Ingresa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String serviceType = spinnerType.getSelectedItem().toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), hour, minute, 0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        iso.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
        String sendAt = iso.format(cal.getTime());

        Calendar serviceCal = (Calendar) cal.clone();
        serviceCal.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String serviceDate = dateFmt.format(serviceCal.getTime());
        String serviceTime = timeFmt.format(serviceCal.getTime());

        String message = "Recordatorio de servicio ByS - " + serviceType + " el " + serviceDate +
                " a las " + serviceTime;
        String extra = etMessage.getText().toString().trim();
        if (!extra.isEmpty()) {
            message += ". " + extra;
        }

        String json = String.format(Locale.US,
                "{\"phone\":\"%s\",\"message\":\"%s\",\"sendAt\":\"%s\"}",
                phone, message, sendAt);

        new Thread(() -> {
            try {
                URL url = new URL(WEBHOOK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                conn.disconnect();
                runOnUiThread(() -> Toast.makeText(this,
                        code >= 200 && code < 300 ? "Enviado" : "Error " + code,
                        Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error enviando", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
