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

/**
 * Pantalla de inicio (login) de la aplicación.
 * <p>Permite autenticar contra un archivo de usuarios en assets.</p>
 */
public class MainActivity extends AppCompatActivity {

    /** Campo de texto para el usuario. */
    private EditText editUser;
    /** Campo de texto para la contraseña. */
    private EditText editPass;
    /** Botón de inicio de sesión. */
    private Button btnSesion;

    /** Lista estática en memoria de usuarios cargados (solo demo). */
    private static ArrayList<Usuario> usuarios = new ArrayList<>();

    /** Clave para pasar el nombre de usuario a otras Activities. */
    public static final String KEY_USERNAME = "username";
    /** Clave para pasar el id del usuario a otras Activities. */
    public static final String KEY_USER_ID  = "userId";

    /**
     * Ciclo de vida: creación de la Activity.
     * <p>Carga la UI y los usuarios desde {@code assets/usuarios.txt}.</p>
     */
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
        editUser = findViewById(R.id.editTextUser);
        editPass = findViewById(R.id.editTextPass);
        btnSesion = findViewById(R.id.btnlogin);

        cargarUsuarios();
    }

    /**
     * Autentica contra la lista en memoria.
     *
     * @param username nombre de usuario ingresado
     * @param password contraseña ingresada
     * @return el {@link Usuario} autenticado si coincide
     * @throws CredencialesInvalidasException si no hay coincidencia
     */
    private Usuario autenticar(String username, String password) throws CredencialesInvalidasException {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                System.out.println(u.getIdUser());
                return u; // éxito
            }
        }
        throw new CredencialesInvalidasException("Usuario o contraseña incorrecta");
    }

    /**
     * Manejador del botón de inicio de sesión (definido vía {@code android:onClick}).
     * <p>Valida campos, autentica y navega al menú de administración.</p>
     * @param view vista pulsada
     */
    public void iniciarSesion(View view){
        String username= editUser.getText().toString().trim();
        String password = editPass.getText().toString().trim();

        if(username.isEmpty()||password.isEmpty()){
            Toast.makeText(this,"Llene todos los campos",Toast.LENGTH_SHORT).show();
        }else{
            try {
                Usuario u = autenticar(username, password);
                Intent intent = new Intent(MainActivity.this, AdminComActivity.class);
                intent.putExtra(KEY_USERNAME, u.getUsername());
                intent.putExtra(KEY_USER_ID,  String.valueOf(u.getIdUser()));
                startActivity(intent);
                finish();
            } catch(CredencialesInvalidasException e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Carga usuarios desde el archivo de assets {@code usuarios.txt}.
     * <p>Formato esperado: {@code id,username,password} por línea.</p>
     */
    public void cargarUsuarios(){
        usuarios.clear(); // evita duplicados si onCreate se ejecuta otra vez
        try (InputStream is = getAssets().open("usuarios.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            int i=1;
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] info = linea.split(",");
                if (info.length < 3) continue;
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
