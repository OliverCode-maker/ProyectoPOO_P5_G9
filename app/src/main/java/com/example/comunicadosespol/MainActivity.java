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
    private static ArrayList<Usuario> usuarios=new ArrayList<>();

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
    // Lógica de autenticación
    private Usuario autenticar(String username, String password) throws CredencialesInvalidasException {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u; // éxito
            }
        }
        // si no se encontró, lanzamos la excepción verificada
        throw new CredencialesInvalidasException("Usuario o contraseña incorrecta");
    }

    public void iniciarSesion(View view){
        String username= editUser.getText().toString().trim();
        String password = editPass.getText().toString().trim();

        if(username.isEmpty()||password.isEmpty()){
            Toast.makeText(this,"Llene todos los campos",Toast.LENGTH_SHORT).show();
        }else{
            try {
                Usuario u = autenticar(username, password); // puede lanzar la excepción verificada
                Intent intent = new Intent(MainActivity.this, AdminComActivity.class);
                //Agregar id del usuario para trackearlo en la siguiente vista
                intent.putExtra("userId", u.getIdUser());
                startActivity(intent);
                finish();
            } catch(CredencialesInvalidasException e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void cargarUsuarios(){
        usuarios.clear(); // evita duplicados si onCreate se ejecuta otra vez
        // verificación de errores al cargar usuarios
        try (InputStream is = getAssets().open("usuarios.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            int i=1;
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] info = linea.split(",");
                if (info.length < 3) continue; // o reporta
                // verificación de errores al darle formato a los datos
                try {
                    int id = Integer.parseInt(info[0].trim());
                    String user = info[1].trim();
                    String pass = info[2].trim();
                    usuarios.add(new Usuario(id, user, pass));
                } catch (NumberFormatException ex) {
                    System.out.println("Error de formato en línea " +i+": " + linea);
                }

                i++;
            }
        } catch (IOException e) {
            Toast.makeText(this,"Error al cargar usuarios...",Toast.LENGTH_SHORT).show();
        }
    }

}