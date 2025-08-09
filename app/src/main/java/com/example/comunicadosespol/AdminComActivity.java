package com.example.comunicadosespol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    String username = getIntent().getStringExtra(MainActivity.KEY_USERNAME);
    // Lee el valor "username" que pusiste en el Intent al abrir esta Activity desde el login
    TextView txtSaludo = findViewById(R.id.txtSaludo);
    // Busca en el layout el TextView con id txtSaludo y lo guarda en la variable txtSaludo
    txtSaludo.setText(getString(R.string.saludo, username));

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