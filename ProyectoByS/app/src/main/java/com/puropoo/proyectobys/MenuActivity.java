package com.puropoo.proyectobys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button btnRegisterClient, btnViewClients, btnRegisterTeam;
    Button btnRegisterRequirements, btnRegisterEquipment, btnRegisterSecondVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnRegisterClient = findViewById(R.id.btnRegisterClient);
        btnViewClients = findViewById(R.id.btnViewClients);
        btnRegisterTeam = findViewById(R.id.btnRegisterTeam);
        btnRegisterRequirements = findViewById(R.id.btnRegisterRequirements);
        btnRegisterEquipment = findViewById(R.id.btnRegisterEquipment);
        btnRegisterSecondVisit = findViewById(R.id.btnRegisterSecondVisit);

        btnRegisterClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnViewClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ClientsListActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegisterTeamActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterRequirements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegisterRequirementsActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegisterEquipmentActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterSecondVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegisterSecondVisitActivity.class);
                startActivity(intent);
            }
        });
    }
}
