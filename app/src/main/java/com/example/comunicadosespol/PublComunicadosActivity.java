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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Locale;

import Modelo.DatosIncompletosException;
import Modelo.TipoComunicado;

public class PublComunicadosActivity extends AppCompatActivity {
    // === NUEVO: nombre del archivo que usaremos en almacenamiento interno ===
    private static final String NOMBRE_COMUNICADOS = "comunicados.txt";

    // --- Controles principales ---
    private RadioGroup rtipos;
    private RadioButton rbAnuncio, rbEvento;
    private Spinner spArea, spUrgencia;
    private CheckBox chkEst, chkPrf, chkAdm;
    private EditText editTitle, editDesc;

    // --- Campos específicos + comunes ---
    private LinearLayout boxEvento, boxAnuncio;
    private EditText editLugar, editFecha, editImgName; // fecha es común; imgname visible
    private Button btnImg, btnPublicar, btnCancelar;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView vistaImagen;
    private Uri imageUri;

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
            }
        }
    }

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
    }
}
