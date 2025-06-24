package com.puropoo.proyectobys;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {

    ListView lvRequests;
    DatabaseHelper db;
    RequestAdapter adapter;
    List<Request> requestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        lvRequests = findViewById(R.id.lvRequests);
        db = new DatabaseHelper(this);

        // Obtener todas las solicitudes guardadas
        requestsList = db.getAllRequests();

        if (requestsList.isEmpty()) {
            Toast.makeText(this, "No hay solicitudes registradas", Toast.LENGTH_SHORT).show();
        } else {
            // Crear un adaptador para mostrar las solicitudes
            adapter = new RequestAdapter(this, requestsList);
            lvRequests.setAdapter(adapter);
        }

        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request selectedRequest = requestsList.get(position);

                // Crear un diálogo de confirmación para eliminar
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewRequestsActivity.this);
                builder.setMessage("¿Seguro que deseas eliminar esta solicitud?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            int rowsDeleted = db.deleteRequest(selectedRequest.getId());
                            if (rowsDeleted > 0) {
                                requestsList.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(ViewRequestsActivity.this, "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ViewRequestsActivity.this, "Error al eliminar la solicitud", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        // Modificar solicitud (si haces clic en el item o un botón en cada item)
        lvRequests.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener la solicitud seleccionada
                Request selectedRequest = requestsList.get(position);

                // Pasar los datos de la solicitud seleccionada a una nueva actividad para editarla
                Intent intent = new Intent(ViewRequestsActivity.this, EditRequestActivity.class);
                intent.putExtra("requestId", selectedRequest.getId());
                startActivity(intent);

                return true;
            }
        });
    }
}
