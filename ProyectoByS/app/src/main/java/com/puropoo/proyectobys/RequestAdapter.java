package com.puropoo.proyectobys;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RequestAdapter extends BaseAdapter {

    private Context context;
    private List<Request> requests;
    private DatabaseHelper db;

    public RequestAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
        this.db = new DatabaseHelper(context); // Crear la instancia de DatabaseHelper
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        }

        Request request = requests.get(position);

        // Obtener los elementos de la vista
        TextView tvServiceType = convertView.findViewById(R.id.tvServiceType);
        TextView tvServiceDate = convertView.findViewById(R.id.tvServiceDate);
        TextView tvServiceTime = convertView.findViewById(R.id.tvServiceTime);
        TextView tvClientName = convertView.findViewById(R.id.tvClientName);  // Nombre del Cliente
        TextView tvClientAddress = convertView.findViewById(R.id.tvClientAddress);  // Dirección del Cliente

        // Asignar valores a los TextViews
        tvServiceType.setText(request.getServiceType());
        tvServiceDate.setText(request.getServiceDate());
        tvServiceTime.setText(request.getServiceTime());

        // Obtener los datos del cliente asociado
        Client client = db.getClientByCedula(request.getClientCedula());  // Método para obtener el cliente usando la cédula
        if (client != null) {
            tvClientName.setText(client.getName());  // Mostrar nombre del cliente
            tvClientAddress.setText(client.getAddress());  // Mostrar dirección del cliente
        }

        // Eliminar solicitud
        Button btnDeleteRequest = convertView.findViewById(R.id.btnDeleteRequest);  // El botón de eliminar
        btnDeleteRequest.setOnClickListener(v -> {
            // Mostrar confirmación antes de eliminar
            new AlertDialog.Builder(context)
                    .setMessage("¿Seguro que deseas eliminar esta solicitud?")
                    .setPositiveButton("Sí", (dialog, id) -> {
                        int rowsDeleted = db.deleteRequest(request.getId());  // Llamar a deleteRequest() de DatabaseHelper
                        if (rowsDeleted > 0) {
                            requests.remove(position);  // Eliminar de la lista local
                            notifyDataSetChanged();  // Notificar al adapter que se eliminó un elemento
                            Toast.makeText(context, "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar la solicitud", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }

}
