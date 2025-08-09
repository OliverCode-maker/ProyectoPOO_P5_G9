package com.example.comunicadosespol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.IOException;

public class PublComunicadosActivity extends AppCompatActivity {
    private RadioGroup rtipos;
    private Spinner spArea;
    private CheckBox chkEst;
    private CheckBox chkPrf;
    private CheckBox chkAdm;
    private EditText editTitle;
    private EditText editDesc;
    private Button btnImg;
    private Button btnPublicar;
    private Button btnCancelar;
    private Uri imageUri;

    private ImageView vistaImagen;
    private static final int PICK_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_publ_comunicados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnImg = findViewById(R.id.btnCargarImagen);
        btnPublicar = findViewById(R.id.btnPubl);
        rtipos = findViewById(R.id.rdTipo);

        btnImg.setOnClickListener(v -> openGallery());

        btnPublicar.setOnClickListener(v -> {
            if (imageUri != null) {
                saveUriToFile(imageUri.toString());
                Toast.makeText(this, "URI guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Seleccione una imagen primero", Toast.LENGTH_SHORT).show();
            }
        });

        rtipos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 'checkedId' es el id del RadioButton seleccionado
                if (checkedId != -1) { // -1 significa que no hay selección
                    // Buscar el RadioButton por su id
                    RadioButton radioSeleccionado = findViewById(checkedId);
                    int posicion = group.indexOfChild(radioSeleccionado);

                    // Ejemplo: si no es la primera opción
                    if (posicion != 0) {
                        mostrarCampo(posicion);
                    }
                }
            }
        });



    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            vistaImagen.setImageURI(imageUri);
        }
    }

    private void saveUriToFile(String uriString) {
        try {
            FileOutputStream fos = openFileOutput("image_uri.txt", MODE_PRIVATE);
            fos.write(uriString.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void cargarImg(View view){

    }
    public void publicarComunicado(View view){

    }
    public void cancelar(View view){

    }

    public void mostrarCampo(int posicion) {
        switch (posicion) {
            case 1:
                
        }
    }
}