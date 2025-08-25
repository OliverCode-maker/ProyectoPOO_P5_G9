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

/**
 * Pantalla principal del menú de administración de comunicados.
 * <p>Recibe el usuario autenticado y permite navegar a ver, publicar o listar mis comunicados.</p>
 */
public class AdminComActivity extends AppCompatActivity {

    /** Botón para ver todos los comunicados. */
    private Button btnVerCom;
    /** Botón para publicar un nuevo comunicado. */
    private Button btnPubCom;
    /** Botón para ver los comunicados del usuario. */
    private Button btnMisCom;
    /** Identificador del usuario autenticado (como String en este flujo). */
    private String userID;

    /**
     * Ciclo de vida: creación de la Activity.
     * <p>Configura la UI, obtiene extras del intent y muestra un saludo.</p>
     */
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
        userID = getIntent().getStringExtra(MainActivity.KEY_USER_ID);

        TextView txtSaludo = findViewById(R.id.txtSaludo);
        btnMisCom = findViewById(R.id.btnMisComunicados);
        btnPubCom = findViewById(R.id.btnPublicarCom);
        btnVerCom = findViewById(R.id.btnMisComunicados); // ojo: id asignado en layout

        txtSaludo.setText(getString(R.string.saludo, username));
    }

    /**
     * Navega a la pantalla de ver comunicados.
     * @param view vista que dispara el evento
     */
    public void verCom(View view){
        Intent intent= new Intent(AdminComActivity.this, VerComunicadosActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a publicar comunicado, pasando el id del usuario.
     * @param view vista que dispara el evento
     */
    public void publCom(View view){
        Intent intent= new Intent(AdminComActivity.this, PublComunicadosActivity.class);
        intent.putExtra(MainActivity.KEY_USER_ID, userID);
        startActivity(intent);
    }

    /**
     * Navega a "Mis comunicados", pasando el id del usuario.
     * @param view vista que dispara el evento
     */
    public void miCom(View view){
        Intent intent= new Intent(AdminComActivity.this, MisComunicadosActivity.class);
        intent.putExtra(MainActivity.KEY_USER_ID, userID);
        startActivity(intent);
    }
}
