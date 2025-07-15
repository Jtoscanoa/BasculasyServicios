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

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendSmsWebhookActivity extends AppCompatActivity {

    private EditText etPhone, etMessage;
    private DatePicker dpDate;
    private TimePicker tpTime;
    private Spinner spinnerServiceType;
    private Button btnSend;

    private static final String WEBHOOK_URL = "https://fhurtadoa2116.app.n8n.cloud/webhook-test/enviar-sms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_webhook_sms);

        etPhone = findViewById(R.id.etPhone);
        etMessage = findViewById(R.id.etMessage);
        dpDate = findViewById(R.id.dpDate);
        tpTime = findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        spinnerServiceType = findViewById(R.id.spinnerServiceTypeSms);
        btnSend = findViewById(R.id.btnSendSms);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendSms());
    }

    private void sendSms() {
        String phoneInput = etPhone.getText().toString().trim();
        if (!phoneInput.matches("\\d{10}")) {
            Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        int servicePos = spinnerServiceType.getSelectedItemPosition();
        if (servicePos <= 0) {
            Toast.makeText(this, getString(R.string.select_service), Toast.LENGTH_SHORT).show();
            return;
        }

        String serviceType = spinnerServiceType.getSelectedItem().toString();

        int day = dpDate.getDayOfMonth();
        int month = dpDate.getMonth() + 1;
        int year = dpDate.getYear();
        int hour = tpTime.getHour();
        int minute = tpTime.getMinute();

        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, minute);
        ZonedDateTime zoned = ZonedDateTime.of(date, time, ZoneId.of("America/Bogota"));
        String sendAt = zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String optional = etMessage.getText().toString().trim();
        String fullMessage = (optional + " " + serviceType + " " +
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).trim();

        try {
            JSONObject json = new JSONObject();
            json.put("phone", "+57" + phoneInput);
            json.put("message", fullMessage);
            json.put("sendAt", sendAt);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8"));

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(WEBHOOK_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(SendSmsWebhookActivity.this,
                                    "Error de conexión", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(SendSmsWebhookActivity.this,
                                    "Enviado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SendSmsWebhookActivity.this,
                                    "Error " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    response.close();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error creando datos", Toast.LENGTH_SHORT).show();
        }
    }
}
