package com.puropoo.proyectobys;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemoteSupportListActivity extends AppCompatActivity {

    private LinearLayout containerRemoteSupports;
    private TextView tvNoRemoteSupports;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_support_list);

        // Inicializar vistas
        containerRemoteSupports = findViewById(R.id.containerRemoteSupports);
        tvNoRemoteSupports = findViewById(R.id.tvNoRemoteSupports);

        // Configurar base de datos
        db = new DatabaseHelper(this);

        // Cargar datos
        loadRemoteSupports();
    }

    private void loadRemoteSupports() {
        List<RemoteSupport> remoteSupports = db.getAllRemoteSupports();

        if (remoteSupports.isEmpty()) {
            tvNoRemoteSupports.setVisibility(View.VISIBLE);
            containerRemoteSupports.setVisibility(View.GONE);
        } else {
            tvNoRemoteSupports.setVisibility(View.GONE);
            containerRemoteSupports.setVisibility(View.VISIBLE);

            // Limpiar contenedor
            containerRemoteSupports.removeAllViews();

            // Agregar cada soporte remoto
            for (RemoteSupport support : remoteSupports) {
                addRemoteSupportCard(support);
            }
        }
    }

    private void addRemoteSupportCard(RemoteSupport support) {
        // Crear CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 8, 16, 8);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4);
        cardView.setRadius(8);

        // Crear LinearLayout interno
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(16, 16, 16, 16);

        // Formatear fecha
        String formattedDate = formatDate(support.getSupportDate());

        // Crear TextViews
        TextView tvServiceNumber = createTextView("Servicio #: " + support.getRequestId(), true);
        TextView tvDate = createTextView("Fecha: " + formattedDate, true);
        TextView tvTime = createTextView("Hora: " + support.getSupportTime(), true);
        TextView tvClient = createTextView("Cliente: " + support.getClientCedula(), true);
        TextView tvMedium = createTextView("Medio: " + support.getMedium(), false);
        
        // Agregar link solo si existe
        cardContent.addView(tvServiceNumber);
        cardContent.addView(tvDate);
        cardContent.addView(tvTime);
        cardContent.addView(tvClient);
        cardContent.addView(tvMedium);

        if (support.getLink() != null && !support.getLink().trim().isEmpty()) {
            TextView tvLink = createTextView("Link: " + support.getLink(), false);
            cardContent.addView(tvLink);
        }

        cardView.addView(cardContent);
        containerRemoteSupports.addView(cardView);
    }

    private TextView createTextView(String text, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        if (bold) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        textView.setLayoutParams(params);
        return textView;
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            return date; // Devolver fecha original si no se puede parsear
        }
    }
}