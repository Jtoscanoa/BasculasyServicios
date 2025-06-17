package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.btnGoRegister)
                .setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        findViewById(R.id.btnViewClients)
                .setOnClickListener(v -> startActivity(new Intent(this, ClientsListActivity.class)));
    }
}
