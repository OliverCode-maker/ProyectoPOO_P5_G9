package com.example.comunicadosespol;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

<<<<<<< HEAD
import java.io.File;
=======
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
>>>>>>> origin/master
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
<<<<<<< HEAD
import java.util.Calendar;
import java.util.Locale;

import Modelo.DatosIncompletosException;
import Modelo.TipoComunicado;
=======
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Modelo.DatosIncompletosException;
>>>>>>> origin/master

public class PublComunicadosActivity extends AppCompatActivity {
    // === NUEVO: nombre del archivo que usaremos en almacenamiento interno ===
    private static final String NOMBRE_COMUNICADOS = "comunicados.txt";

    // --- Controles principales ---
    private RadioGroup rtipos;
    private RadioButton rbAnuncio, rbEvento;
    private Spinner spArea, spUrgencia;
    private CheckBox chkEst, chkPrf, chkAdm;
    private EditText editTitle, editDesc;

<<<<<<< HEAD
    // --- Campos específicos + comunes ---
    private LinearLayout boxEvento, boxAnuncio;
    private EditText editLugar, editFecha, editImgName; // fecha es común; imgname visible
    private Button btnImg, btnPublicar, btnCancelar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView vistaImagen;
    private Uri imageUri;
=======
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

>>>>>>> origin/master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_publ_comunicados);

        // === NUEVO: asegurar archivo en almacenamiento interno en el primer arranque ===
        asegurarComunicadosEnInterno();

        // Ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

<<<<<<< HEAD
        // --- FindViews ---
        rtipos = findViewById(R.id.rdTipo);
        rbAnuncio = findViewById(R.id.rbAnuncio);
        rbEvento  = findViewById(R.id.rbEvento);

        spArea   = findViewById(R.id.spArea);
        spUrgencia = findViewById(R.id.spUrgencia);

        chkEst = findViewById(R.id.chkEst);
        chkPrf = findViewById(R.id.chkPrf);
        chkAdm = findViewById(R.id.chkAdm);

        editTitle = findViewById(R.id.editTitle);
        editDesc  = findViewById(R.id.editDesc);

        boxEvento  = findViewById(R.id.boxEvento);
        boxAnuncio = findViewById(R.id.boxAnuncio);
        editLugar  = findViewById(R.id.editLugar);
        editFecha  = findViewById(R.id.editFecha);     // común (para ambos)
        editImgName = findViewById(R.id.editImgName);  // solo lectura en el XML

        btnImg = findViewById(R.id.btnCargarImagen);
        btnPublicar = findViewById(R.id.btnPubl);
        btnCancelar = findViewById(R.id.btnCancelar);
        vistaImagen = findViewById(R.id.imgPreview);

        // --- Lanzador para abrir galería y recibir imagen ---
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri originalUri = result.getData().getData();
                        try {
                            File copiado = copiarImagenAUbicacionApp(originalUri);
                            imageUri = Uri.fromFile(copiado);      // Uri propia en internal storage
                            vistaImagen.setImageURI(imageUri);      // previsualiza
                            // Mostrar nombre con extensión en el campo visible:
                            String base = nombreBase(originalUri);
                            editImgName.setText(base + ".jpg");
                        } catch (IOException e) {
                            Toast.makeText(this, "Error al copiar imagen", Toast.LENGTH_SHORT).show();
                        }
=======
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

        //Configurar el DatePicker
        editFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                int year= calendar.get(Calendar.YEAR);
                int month= calendar.get(Calendar.MONTH);
                int day= calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker= new DatePickerDialog(PublComunicadosActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int anio, int mes, int diaDelmes) {
                        String fechaSelec;
                        if(mes+1<10){
                            fechaSelec= diaDelmes+"/0"+(mes+1)+"/"+anio;
                        }else{
                            fechaSelec= diaDelmes+"/"+(mes+1)+"/"+anio;
                        }
                        editFecha.setText(fechaSelec);
                    }
                },year,month,day);
                datePicker.show();
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
                    if (!(posicion < 0 || posicion > 1)) {
                        mostrarCampo(posicion);
>>>>>>> origin/master
                    }
                }
        );

        // Click del botón para abrir galería
        btnImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        // Carga de opciones
        spArea.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Académico","Educativo","Investigación","Vinculación","Capacitación","Deportes","Cultura","Servicios/Productos"}));

        spUrgencia.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Alta","Media","Baja"}));

        // Tipo: mostrar/ocultar cajas (fecha queda SIEMPRE visible/usable)
        rtipos.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbEvento) {
                boxEvento.setVisibility(View.VISIBLE);   // lugar
                boxAnuncio.setVisibility(View.GONE);     // urgencia oculto
            } else if (checkedId == R.id.rbAnuncio) {
                boxEvento.setVisibility(View.GONE);
                boxAnuncio.setVisibility(View.VISIBLE);
            }
        });

        // Calendario (para ambos tipos)
        editFecha.setOnClickListener(v -> mostrarDatePicker());

<<<<<<< HEAD
        // Publicar
        btnPublicar.setOnClickListener(v -> {
            try {
                publicarComunicado();
                Toast.makeText(this, "Comunicado publicado", Toast.LENGTH_SHORT).show();
                finish(); // volver al menú anterior
            } catch (DatosIncompletosException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Cancelar / Volver
        btnCancelar.setOnClickListener(v -> finish());
    }

    // === NUEVO: Copiar 'comunicados.txt' desde assets a /files si no existe ===
    private void asegurarComunicadosEnInterno() {
        File destino = new File(getFilesDir(), NOMBRE_COMUNICADOS);
        if (!destino.exists()) {
            try (InputStream in = getAssets().open(NOMBRE_COMUNICADOS);
                 FileOutputStream out = new FileOutputStream(destino)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            } catch (IOException e) {
                // Si no existe en assets, crea uno vacío
                try (FileOutputStream out = new FileOutputStream(destino)) {
                    // vacío
                } catch (IOException ignored) {}
=======
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
>>>>>>> origin/master
            }
        }
    }

<<<<<<< HEAD
    // Copia la imagen seleccionada a /files/images/ con extensión .jpg
    private File copiarImagenAUbicacionApp(Uri uri) throws IOException {
        File dir = new File(getFilesDir(), "images");
        if (!dir.exists()) dir.mkdirs();

        String base = nombreBase(uri);
        File destino = new File(dir, base + ".jpg");

        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(destino)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        }
        return destino;
    }

    // Obtiene nombre base (sin extensión) desde el ContentResolver o la URI
    private String nombreBase(Uri uri) {
        String nombre = null;
        try (Cursor c = getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (c != null && c.moveToFirst()) nombre = c.getString(0);
        }
        if (nombre == null) {
            String last = uri.getLastPathSegment();
            nombre = (last != null) ? last : "imagen";
        }
        int punto = nombre.lastIndexOf('.');
        if (punto > 0) nombre = nombre.substring(0, punto);
        return nombre;
    }

    // ========= LÓGICA PRINCIPAL =========
    private void publicarComunicado() throws Exception {
        // 1) Determinar tipo
        TipoComunicado tipo;
        if (rbEvento.isChecked()) {
            tipo = TipoComunicado.EVENTO;
        } else if (rbAnuncio.isChecked()) {
            tipo = TipoComunicado.ANUNCIO;
        } else {
            throw new DatosIncompletosException("Seleccione el tipo de comunicado.");
        }

        // 2) Comunes
        String area = (String) spArea.getSelectedItem();
        String titulo = s(editTitle);
        String audiencia = getAudiencia();
        String descripcion = s(editDesc);
        String fecha = s(editFecha); // requerido en ambos
        String nombreArchivoImagen = (imageUri != null) ? (nombreBase(imageUri) + ".jpg") : null;

        // 3) Validación común
        if (titulo.isEmpty() || audiencia.isEmpty() || descripcion.isEmpty()
                || nombreArchivoImagen == null || fecha.isEmpty()) {
            throw new DatosIncompletosException("No están todos los datos completos.");
        }

        // 4) Construcción de línea CSV según tipo
        String id = String.valueOf(System.currentTimeMillis() % 1000000);
        String lineaCSV;

        switch (tipo) {
            case EVENTO: {
                String lugar = s(editLugar);
                if (lugar.isEmpty()) {
                    throw new DatosIncompletosException("Faltan datos del evento (lugar).");
                }
                // id, tipo, área, título, audiencia, descripción, nombreArchivoImagen, lugar, fecha
                lineaCSV = String.join(", ",
                        id, "evento", area, titulo, audiencia, descripcion, nombreArchivoImagen, lugar, fecha);
                break;
            }
            case ANUNCIO: {
                String urgencia = (String) spUrgencia.getSelectedItem();
                if (urgencia == null || urgencia.isEmpty()) {
                    throw new DatosIncompletosException("Falta seleccionar nivel de urgencia.");
                }
                // id, tipo, área, título, audiencia, descripción, nombreArchivoImagen, urgencia, fecha
                lineaCSV = String.join(", ",
                        id, "anuncio", area, titulo, audiencia, descripcion, nombreArchivoImagen, urgencia, fecha);
                break;
            }
            default:
                throw new IllegalStateException("Tipo de comunicado no reconocido: " + tipo);
        }

        // 5) Guardar en comunicado.txt (append)
        try (FileOutputStream fos = openFileOutput(NOMBRE_COMUNICADOS, MODE_APPEND)) {
            fos.write((lineaCSV + "\n").getBytes());
        }
    }

    private String s(EditText e) { return e.getText().toString().trim(); }

    private String getAudiencia() throws DatosIncompletosException {
        StringBuilder sb = new StringBuilder();
        if (chkEst.isChecked()) sb.append("Estudiantes;");
        if (chkPrf.isChecked()) sb.append("Profesores;");
        if (chkAdm.isChecked()) sb.append("Administrativo;");
        if (sb.length() == 0) throw new DatosIncompletosException("Seleccione la audiencia.");
        sb.setLength(sb.length() - 1); // quitar último ';'
        return sb.toString();
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format(Locale.ROOT, "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            editFecha.setText(fecha);
        }, y, m, d).show();
=======
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
>>>>>>> origin/master
    }
}
