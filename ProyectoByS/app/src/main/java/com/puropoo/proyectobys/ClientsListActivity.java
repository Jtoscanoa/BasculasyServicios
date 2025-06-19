package com.puropoo.proyectobys;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import java.util.List;

public class ClientsListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_clients);

        rv = findViewById(R.id.rvClients);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        loadData();
    }

    private void loadData() {
        List<Client> list = db.getAllClients();
        ClientAdapter adapter = new ClientAdapter(
                this, list, db,
                this::loadData    // callback para recargar tras editar/borrar
        );
        rv.setAdapter(adapter);
    }
}




