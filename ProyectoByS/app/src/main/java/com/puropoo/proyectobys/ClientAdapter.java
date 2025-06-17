package com.puropoo.proyectobys;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClientAdapter
        extends RecyclerView.Adapter<ClientAdapter.ClientVH> {

    public interface OnDataChanged {
        void onDataChanged();
    }

    private final List<Client> data;
    private final DatabaseHelper db;
    private final Context ctx;
    private final OnDataChanged callback;

    public ClientAdapter(Context ctx, List<Client> data, DatabaseHelper db, OnDataChanged cb) {
        this.ctx = ctx;
        this.data = data;
        this.db = db;
        this.callback = cb;
    }

    @NonNull @Override
    public ClientVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_client, parent, false);
        return new ClientVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientVH h, int pos) {
        Client c = data.get(pos);
        h.tvName.setText(c.name);
        h.tvCedula.setText(c.cedula);
        h.tvPhone.setText(c.phone);
        h.tvAddress.setText(c.address);
        h.tvService.setText(c.serviceType);

        h.btnEdit.setOnClickListener(v -> showEditDialog(c));
        h.btnDelete.setOnClickListener(v -> {
            db.deleteClient(c.id);
            callback.onDataChanged();
        });
    }

    @Override public int getItemCount() { return data.size(); }

    class ClientVH extends RecyclerView.ViewHolder {
        TextView tvName, tvCedula, tvPhone, tvAddress, tvService;
        Button btnEdit, btnDelete;
        ClientVH(View item) {
            super(item);
            tvName    = item.findViewById(R.id.tvName);
            tvCedula  = item.findViewById(R.id.tvCedula);
            tvPhone   = item.findViewById(R.id.tvPhone);
            tvAddress = item.findViewById(R.id.tvAddress);
            tvService = item.findViewById(R.id.tvService);
            btnEdit   = item.findViewById(R.id.btnEdit);
            btnDelete = item.findViewById(R.id.btnDelete);
        }
    }

    private void showEditDialog(Client c) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        b.setTitle("Editar cliente");

        LinearLayout lay = new LinearLayout(ctx);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.setPadding(16,16,16,16);

        EditText eName = new EditText(ctx);
        eName.setText(c.name);
        lay.addView(eName);

        EditText ePhone = new EditText(ctx);
        ePhone.setText(c.phone);
        lay.addView(ePhone);

        b.setView(lay);
        b.setPositiveButton("Guardar", (d,_i) -> {
            String nn = eName.getText().toString().trim();
            String pp = ePhone.getText().toString().trim();
            if (!nn.isEmpty() && pp.matches("\\d{10}")) {
                db.updateClient(c.id, nn, c.cedula, pp, c.address, c.serviceType);
                callback.onDataChanged();
            } else {
                Toast.makeText(ctx, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Cancelar", null);
        b.show();
    }
}
