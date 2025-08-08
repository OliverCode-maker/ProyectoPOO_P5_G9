package com.example.comunicadosespol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Modelo.CredencialesInvalidasException;
import Modelo.Usuario;

public class MainActivity extends AppCompatActivity {
    private EditText editUser;
    private EditText editPass;
    private Button btnSesion;
    private static ArrayList<Usuario> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editUser=findViewById(R.id.editTextUser);
        editPass=findViewById(R.id.editTextPass);
        btnSesion=findViewById(R.id.btnlogin);

        cargarUsuarios();
    }
    public void iniciarSesion(View view){
        String name= editUser.getText().toString().trim();
        String psw = editPass.getText().toString().trim();

        if(name.isEmpty()||psw.isEmpty()){
            Toast.makeText(this,"Llene todos los campos",Toast.LENGTH_SHORT).show();
        }else{
            try {
                for (Usuario u : usuarios) {
                    if (u.getUsername().equals(name) && u.getPassword().equals(psw)) {
                        Intent intent= new Intent(MainActivity.this,AdminComActivity.class);
                        startActivity(intent);
                    }
                }
                throw new CredencialesInvalidasException("Usuario o contraseña incorrecta");

            }catch(CredencialesInvalidasException e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cargarUsuarios(){
        try (InputStream is = getAssets().open("usuarios.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] info =linea.split(",");
                usuarios.add(new Usuario(Integer.parseInt(info[0]),info[1],info[2]));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error al cargar usuarios...",Toast.LENGTH_SHORT).show();
        }

    }
}