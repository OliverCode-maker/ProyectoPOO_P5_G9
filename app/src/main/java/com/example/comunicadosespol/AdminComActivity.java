package com.example.comunicadosespol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminComActivity extends AppCompatActivity {
    private Button btnVerCom;
    private Button btnPubCom;
    private Button btnMisCom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_com);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void verCom(View view){
        Intent intent= new Intent(AdminComActivity.this, VerComunicadosActivity.class);
        startActivity(intent);
    }
    public void publCom(View view){
        Intent intent= new Intent(AdminComActivity.this, PublComunicadosActivity.class);
        startActivity(intent);
    }
    public void miCom(View view){
        Intent intent= new Intent(AdminComActivity.this, MisComunicadosActivity.class);
        startActivity(intent);
    }
}