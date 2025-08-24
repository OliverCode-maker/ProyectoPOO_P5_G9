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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Modelo.DatosIncompletosException;

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

    private TextView textLugar;
    private EditText editLugar;
    private TextView textFecha;
    private EditText editFecha;
    private TextView textUrgencia;
    private Spinner spUrgencia;
    private ImageView vistaImagen;
    private static final int PICK_IMAGE = 100;
    private String savedImagePath;

    private LinearLayout layoutUrgencia;
    private LinearLayout layoutLugar;
    private LinearLayout layoutFecha;

    private String userID;

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

        userID = getIntent().getStringExtra(MainActivity.KEY_USER_ID);
        // Inicializar todos los campos
        textLugar = findViewById(R.id.textLugar);
        editLugar = findViewById(R.id.editTextLugar);

        textFecha = findViewById(R.id.textFecha);
        editFecha = findViewById(R.id.editTextFecha);

        textUrgencia = findViewById(R.id.textUrgencia);
        spUrgencia = findViewById(R.id.spinnerUrgencia);

        vistaImagen = findViewById(R.id.imageView);

        editTitle = findViewById(R.id.editTextTitulo);
        editDesc = findViewById(R.id.editTextDescripcion);
        spArea = findViewById(R.id.spinArea);
        chkEst = findViewById(R.id.chEstudiantes);
        chkPrf = findViewById(R.id.Profesores);
        chkAdm = findViewById(R.id.chAdministrativo);


        layoutFecha = findViewById(R.id.layoutFecha);
        layoutLugar = findViewById(R.id.layoutLugar);
        layoutUrgencia = findViewById(R.id.layoutUrgencia);


        btnImg = findViewById(R.id.btnCargarImagen);
        btnPublicar = findViewById(R.id.btnPubl);
        rtipos = findViewById(R.id.rdTipo);
        btnCancelar = findViewById(R.id.btnAtras);
        btnImg.setOnClickListener(v -> openGallery());



        rtipos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 'checkedId' es el id del RadioButton seleccionado
                if (checkedId != -1) { // -1 significa que no hay selección
                    // Buscar el RadioButton por su id
                    RadioButton radioSeleccionado = findViewById(checkedId);
                    int posicion = group.indexOfChild(radioSeleccionado);

                    // Ejemplo: si no es la primera opción
                    if (!(posicion < 0 || posicion > 1)) {
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
            // Guardar la imagen seleccionada en almacenamiento interno
            savedImagePath = saveImageToInternalStorage(imageUri);
            if (savedImagePath != null) {
                vistaImagen.setImageURI(Uri.fromFile(new File(savedImagePath)));
                Toast.makeText(this, "Imagen guardada localmente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File imageFile = new File(getFilesDir(), "comunicado_img_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private void saveImagePathToFile(String path) {
        try {
            FileOutputStream fos = openFileOutput("image_path.txt", MODE_PRIVATE);
            fos.write(path.getBytes());
            fos.close();
        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar la ruta de la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    public void cargarImg(View view){
        // Cargar la imagen guardada desde almacenamiento interno
        try {
            File file = new File(getFilesDir(), "image_path.txt");
            if (file.exists()) {
                InputStream is = openFileInput("image_path.txt");
                byte[] buffer = new byte[(int) file.length()];
                is.read(buffer);
                is.close();
                String path = new String(buffer);
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    vistaImagen.setImageURI(Uri.fromFile(imgFile));
                    Toast.makeText(this, "Imagen cargada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se encontró la imagen guardada", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No hay imagen guardada", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    public void publicarComunicado(View view){
        // Validar que todos los campos necesarios estén llenos
        if (!validarCampos()) {
            return;
        }

        // Obtener datos comunes
        String tipo = obtenerTipoSeleccionado();
        String area = spArea.getSelectedItem().toString();
        String titulo = editTitle.getText().toString().trim();
        String audiencia = obtenerAudienciaSeleccionada();
        String descripcion = editDesc.getText().toString().trim();
        String nombreImagen = savedImagePath != null ? new File(savedImagePath).getName() : "";

        // Generar ID único
        int nuevoId = obtenerSiguienteId();


        // Crear línea para guardar según el tipo
        String lineaComunicado = "";
        if (tipo.equals("anuncio")) {
            String urgencia = spUrgencia.getSelectedItem().toString();
            lineaComunicado = nuevoId + "|" + tipo + "|" + area + "|" + titulo + "|" +
                            audiencia + "|" + descripcion + "|" + nombreImagen + "|" + urgencia + "|" + userID;
        } else if (tipo.equals("evento")) {
            String lugar = editLugar.getText().toString().trim();
            String fecha = editFecha.getText().toString().trim();
            lineaComunicado = nuevoId + "|" + tipo + "|" + area + "|" + titulo + "|" +
                            audiencia + "|" + descripcion + "|" + nombreImagen + "|" +lugar + "|" + fecha + "|" + userID;

        }
        // Guardar en comunicados.txt
        if (guardarComunicado(lineaComunicado)) {
            if (savedImagePath != null) {
                saveImagePathToFile(savedImagePath);
            }
            Toast.makeText(this, "Comunicado publicado exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al publicar el comunicado", Toast.LENGTH_SHORT).show();
        }
    }
    public void cancelar(View view){
        // Eliminar la imagen guardada si existe
        if (savedImagePath != null) {
            File imgFile = new File(savedImagePath);
            if (imgFile.exists()) {
                imgFile.delete();
            }
            // También elimina el archivo de ruta
            File pathFile = new File(getFilesDir(), "image_path.txt");
            if (pathFile.exists()) {
                pathFile.delete();
            }
        }
        finish();
    }

    private boolean validarCampos() {
        try {
            if (editTitle.getText().toString().trim().isEmpty()) {
                throw new DatosIncompletosException("Ingrese un título");
            }
            if (editDesc.getText().toString().trim().isEmpty()) {
                throw new DatosIncompletosException("Ingrese una descripción");
            }
            if (spArea.getSelectedItemPosition() == 0) {
                throw new DatosIncompletosException("Seleccione un área");
            }
            if (!chkEst.isChecked() && !chkPrf.isChecked() && !chkAdm.isChecked()) {
                throw new DatosIncompletosException("Seleccione al menos una audiencia");
            }

            String tipo = obtenerTipoSeleccionado();
            if (tipo.equals("anuncio") && spUrgencia.getSelectedItemPosition() == 0) {
                throw new DatosIncompletosException("Seleccione un nivel de urgencia");
            }
            if (tipo.equals("evento")) {
                if (editLugar.getText().toString().trim().isEmpty()) {
                    throw new DatosIncompletosException("Ingrese el lugar del evento");
                }
                if (editFecha.getText().toString().trim().isEmpty()) {
                    throw new DatosIncompletosException("Ingrese la fecha del evento");
                }
                // Validar formato y que la fecha sea futura
                if (!validarFechaEvento(editFecha.getText().toString().trim())) {
                    return false;
                }
            }

            return true;


        } catch (DatosIncompletosException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validarFechaEvento(String fechaTexto) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        formatoFecha.setLenient(false); // No permitir fechas inválidas como 32/13/2024

        try {
            // Verificar formato
            Date fechaEvento = formatoFecha.parse(fechaTexto);

            // Obtener fecha actual
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            Date fechaActual = hoy.getTime();

            // Verificar que la fecha del evento sea posterior a hoy
            if (fechaEvento.before(fechaActual)) {
                throw new DatosIncompletosException("La fecha del evento debe ser posterior a la fecha actual");
            }

            return true;

        } catch (ParseException e) {
            try {
                throw new DatosIncompletosException("Formato de fecha inválido. Use el formato dd/MM/yyyy (ejemplo: 25/12/2025)");
            } catch (DatosIncompletosException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (DatosIncompletosException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String obtenerTipoSeleccionado() {
        int selectedId = rtipos.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        int posicion = rtipos.indexOfChild(radioButton);
        return posicion == 0 ? "anuncio" : "evento";
    }

    private String obtenerAudienciaSeleccionada() {
        List<String> audiencias = new ArrayList<>();
        if (chkEst.isChecked()) audiencias.add("Estudiantes");
        if (chkPrf.isChecked()) audiencias.add("Profesores");
        if (chkAdm.isChecked()) audiencias.add("Administrativos");
        return String.join(";", audiencias);
    }

    private int obtenerSiguienteId() {
        int maxId = 0;
        try {
            File file = new File(getFilesDir(), "comunicados.txt");
            if (file.exists()) {
                FileInputStream fis = openFileInput("comunicados.txt");
                byte[] buffer = new byte[(int) file.length()];
                fis.read(buffer);
                fis.close();
                String contenido = new String(buffer);
                String[] lineas = contenido.split("\n");
                for (String linea : lineas) {
                    if (!linea.trim().isEmpty()) {
                        String[] partes = linea.split("\\|");
                        if (partes.length > 0) {
                            try {
                                int id = Integer.parseInt(partes[0]);
                                if (id > maxId) maxId = id;
                            } catch (NumberFormatException e) {
                                // Ignorar líneas mal formateadas
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al leer comunicados existentes", Toast.LENGTH_SHORT).show();
        }
        return maxId + 1;
    }

    private boolean guardarComunicado(String lineaComunicado) {
        try {
            FileOutputStream fos = openFileOutput("comunicados.txt", MODE_APPEND);
            fos.write((lineaComunicado + "\n").getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void mostrarCampo(int posicion) {
        // Oculta todos los campos primero
        textLugar.setVisibility(View.GONE);
        editLugar.setVisibility(View.GONE);

        layoutLugar.setVisibility(View.GONE);
        textFecha.setVisibility(View.GONE);
        editFecha.setVisibility(View.GONE);
        layoutFecha.setVisibility(View.GONE);
        textUrgencia.setVisibility(View.GONE);
        spUrgencia.setVisibility(View.GONE);
        layoutLugar.setVisibility(View.GONE);


        // Muestra solo los necesarios según la posición
        switch (posicion) {
            case 0:

                layoutUrgencia.setVisibility(View.VISIBLE);
                textUrgencia.setVisibility(View.VISIBLE);
                spUrgencia.setVisibility(View.VISIBLE);
                break;
            case 1:
                layoutLugar.setVisibility(View.VISIBLE);
                textLugar.setVisibility(View.VISIBLE);
                editLugar.setVisibility(View.VISIBLE);
                layoutFecha.setVisibility(View.VISIBLE);

                textFecha.setVisibility(View.VISIBLE);
                editFecha.setVisibility(View.VISIBLE);
                break;
        }
    }
}