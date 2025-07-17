package com.puropoo.proyectobys;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SmsManagementActivity extends AppCompatActivity {

    private Button btnSmsSent, btnSmsPending;
    private ListView lvSms;
    private DatabaseHelper db;
    private String recipientType;
    private List<SmsNotification> currentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_management);

        btnSmsSent = findViewById(R.id.btnSmsSent);
        btnSmsPending = findViewById(R.id.btnSmsPending);
        lvSms = findViewById(R.id.lvSms);

        recipientType = getIntent().getStringExtra("recipientType");
        db = new DatabaseHelper(this);

        btnSmsSent.setOnClickListener(v -> loadSms(true));
        btnSmsPending.setOnClickListener(v -> loadSms(false));

        lvSms.setOnItemClickListener((parent, view, position, id) -> {
            SmsNotification sms = currentList.get(position);
            if (sms.getSentTime() == null) {
                showInstructionDialog(sms);
            }
        });

        loadSms(false);
    }

    private void loadSms(boolean sent) {
        currentList = db.getSmsNotifications(recipientType, sent);
        List<String> display = new ArrayList<>();
        for (SmsNotification sms : currentList) {
            String text;
            if (sent) {
                text = "Enviado: " + sms.getSentTime()
                        + " a " + sms.getPhone()
                        + " - Servicio: " + sms.getServiceType()
                        + " " + sms.getServiceDate() + " " + sms.getServiceTime();
            } else {
                text = "Programada " + sms.getScheduledSend()
                        + " a " + sms.getPhone()
                        + " - Servicio: " + sms.getServiceType()
                        + " " + sms.getServiceDate() + " " + sms.getServiceTime();
                if (sms.getMessage() != null && !sms.getMessage().isEmpty()) {
                    text += " Mensaje Opcional: " + sms.getMessage();
                }
            }
            display.add(text);
        }
        lvSms.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
    }

    private void showInstructionDialog(SmsNotification sms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar instrucciones");
        builder.setMessage("Tel\u00e9fono: " + sms.getPhone());
        final EditText input = new EditText(this);
        input.setText(sms.getMessage());
        builder.setView(input);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String msg = input.getText().toString();
            db.updateSmsMessage(sms.getId(), msg);
            loadSms(false);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmsUtils.checkAndSendPendingSms(this);
    }
}
