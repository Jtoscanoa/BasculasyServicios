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

    public RequestAdapter(Context context, List<Request> requests, DatabaseHelper db) {
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

        TextView tvServiceType = convertView.findViewById(R.id.tvServiceType);
        TextView tvServiceDate = convertView.findViewById(R.id.tvServiceDate);
        TextView tvServiceTime = convertView.findViewById(R.id.tvServiceTime);
        TextView tvServiceAddress = convertView.findViewById(R.id.tvServiceAddress);  // Campo para la dirección

        tvServiceType.setText(request.getServiceType());
        tvServiceDate.setText(request.getServiceDate());
        tvServiceTime.setText(request.getServiceTime());
        tvServiceAddress.setText(request.getServiceAddress());  // Mostrar la dirección de la solicitud

        return convertView;
    }


}
