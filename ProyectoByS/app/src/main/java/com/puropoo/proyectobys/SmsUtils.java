package com.puropoo.proyectobys;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

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
        SmsManager smsManager = SmsManager.getDefault();
        for (SmsNotification sms : pending) {
            try {
                smsManager.sendTextMessage(sms.getPhone(), null, sms.getMessage(), null, null);
                String sentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                db.markSmsAsSent(sms.getId(), sentTime);
                Log.d(TAG, "SMS enviado a " + sms.getPhone());
            } catch (Exception e) {
                Log.e(TAG, "Error enviando SMS", e);
            }
        }
    }
}
