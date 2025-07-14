package com.puropoo.proyectobys;

import android.content.Context;
import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsUtils {
    private static final String TAG = "SmsUtils";

    // Crear notificaciones para un servicio
    public static void scheduleSmsForRequest(Context ctx, Request request) {
        DatabaseHelper db = new DatabaseHelper(ctx);
        List<String> teamPhones = db.getAllTeamPhones();
        Client client = db.getClientByCedula(request.getClientCedula());

        String baseMessage = "Servicio " + request.getServiceType() + " el " +
                request.getServiceDate() + " a las " + request.getServiceTime();

        String scheduled = calculateScheduledTime(request.getServiceDate(), request.getServiceTime());

        for (String phone : teamPhones) {
            SmsNotification sms = new SmsNotification(
                    request.getId(),
                    "tecnico",
                    phone,
                    request.getServiceDate(),
                    request.getServiceTime(),
                    request.getServiceType(),
                    baseMessage,
                    scheduled
            );
            db.insertSmsNotification(sms);
            sendSmsToN8n(sms);
        }

        if (client != null) {
            SmsNotification sms = new SmsNotification(
                    request.getId(),
                    "cliente",
                    client.phone,
                    request.getServiceDate(),
                    request.getServiceTime(),
                    request.getServiceType(),
                    baseMessage,
                    scheduled
            );
            db.insertSmsNotification(sms);
            sendSmsToN8n(sms);
        }
    }

    private static String calculateScheduledTime(String serviceDate, String serviceTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = sdf.parse(serviceDate + " " + serviceTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, -24);
            SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return out.format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static void checkAndSendPendingSms(Context ctx) {
        DatabaseHelper db = new DatabaseHelper(ctx);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        List<SmsNotification> pending = db.getPendingSmsDue(now);
        for (SmsNotification sms : pending) {
            try {
                sendSmsToN8n(sms);
                String sentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                db.markSmsAsSent(sms.getId(), sentTime);
                Log.d(TAG, "SMS enviado a traves de n8n a " + sms.getPhone());
            } catch (Exception e) {
                Log.e(TAG, "Error enviando SMS", e);
            }
        }
    }

    private static void sendSmsToN8n(SmsNotification sms) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:5678/webhook-test/enviar-sms");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                String isoDate = iso.format(input.parse(sms.getScheduledSend()));

                String json = String.format(
                        "{\"phone\":\"%s\",\"message\":\"%s\",\"sendAt\":\"%s\"}",
                        sms.getPhone(), sms.getMessage(), isoDate);

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                if (code < 200 || code >= 300) {
                    Log.e(TAG, "Respuesta no exitosa de n8n: " + code);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error enviando SMS a n8n", e);
            }
        }).start();
    }
}
