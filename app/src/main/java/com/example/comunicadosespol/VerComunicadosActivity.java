package com.example.comunicadosespol;

<<<<<<< HEAD
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory; // NUEVO
=======

import android.app.DatePickerDialog;
>>>>>>> origin/master
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
<<<<<<< HEAD
import java.io.File;              // NUEVO
import java.io.FileInputStream;  // NUEVO
=======
import java.io.File;
import java.io.FileInputStream;
>>>>>>> origin/master
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Modelo.Comunicado;
import Modelo.Anuncio;
import Modelo.DatosIncompletosException;
import Modelo.Evento;

public class VerComunicadosActivity extends AppCompatActivity {
    private LinearLayout contenedor;
<<<<<<< HEAD

    // NUEVO: usamos el mismo nombre de archivo que escribe PublComunicadosActivity
    private static final String NOMBRE_COMUNICADOS = "comunicados.txt";

=======
    private List<Comunicado> listaComunicados;
    private EditText fechaFiltro;
    private Button filtroButton;
>>>>>>> origin/master
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_comunicados);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contenedor = findViewById(R.id.layoutContenedor);
<<<<<<< HEAD

        // NUEVO: ahora carga desde almacenamiento interno (con respaldo en assets)
        cargarComunicados(NOMBRE_COMUNICADOS);
    }

    // Carga todos los comunicados de comunicados.txt
    private void cargarComunicados(String nombreArchivo) {
        contenedor.removeAllViews();
        java.util.HashSet<String> yaMostradas = new java.util.HashSet<>();
        int count = 0;

        // 1) Leer del almacenamiento interno si existe
        java.io.File interno = new java.io.File(getFilesDir(), nombreArchivo);
        if (interno.exists()) {
            try (InputStream is = new java.io.FileInputStream(interno);
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;
                    if (yaMostradas.add(linea)) { // evita duplicados exactos
                        agregarComunicado(linea);  // (ya soporta ; y ,)
                        count++;
                    }
                }
            } catch (IOException ignored) {}
        }

        // 2) Leer también de assets como respaldo
        try (InputStream is = getAssets().open(nombreArchivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (yaMostradas.add(linea)) {
                    agregarComunicado(linea);
                    count++;
=======
        listaComunicados = new ArrayList<>();
        fechaFiltro = findViewById(R.id.filtroDate);
        filtroButton = findViewById(R.id.filtroButton);
        cargarComunicados("comunicados.txt");

        //Configurar el DatePicker
        fechaFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar= Calendar.getInstance();
                int year= calendar.get(Calendar.YEAR);
                int month= calendar.get(Calendar.MONTH);
                int day= calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker= new DatePickerDialog(VerComunicadosActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int anio, int mes, int diaDelmes) {
                        String fechaSeleccionada;
                        if(mes+1<10){
                            fechaSeleccionada= diaDelmes+"/0"+(mes+1)+"/"+anio;
                        }else{
                            fechaSeleccionada= diaDelmes+"/"+(mes+1)+"/"+anio;
                        }

                        fechaFiltro.setText(fechaSeleccionada);
                    }
                },year,month,day);
                datePicker.show();
            }
        });
    }

    public void onClickFiltrar(View view) {
        String fechaTexto = fechaFiltro.getText().toString().trim();
        if (fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una fecha para filtrar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de fecha
        if (!validarFechaEvento(fechaTexto)) {
            return; // Si la fecha es inválida, no continuar
        }

        // Limpiar el contenedor antes de aplicar el filtro
        contenedor.removeAllViews();

        // Filtrar comunicados por fecha
        for (Comunicado comunicado : listaComunicados) {
            if (comunicado instanceof Evento) {
                Evento evento = (Evento) comunicado;
                if (evento.getFecha().equals(fechaTexto)) {
                    agregarComunicadoAlLayout(evento);
                }
            }
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
    // Carga todos los comunicados del archivo interno comunicados.txt
    private void cargarComunicados(String nombreArchivo) {
        int count = 0;

        try {
            // Primero intentar cargar desde almacenamiento interno
            File file = new File(getFilesDir(), nombreArchivo);
            if (file.exists()) {
                FileInputStream fis = openFileInput(nombreArchivo);
                byte[] buffer = new byte[(int) file.length()];
                fis.read(buffer);
                fis.close();
                String contenido = new String(buffer);
                String[] lineas = contenido.split("\n");

                for (String linea : lineas) {
                    linea = linea.trim();
                    if (!linea.isEmpty()) {
                        Comunicado comunicado = crearComunicado(linea);
                        if (comunicado != null ) {
                            listaComunicados.add(comunicado);
                            agregarComunicadoAlLayout(comunicado);
                            count++;
                        }
                    }
                }
            } else {
                // Si no existe en almacenamiento interno, cargar desde assets
                try (InputStream is = getAssets().open(nombreArchivo);
                     BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                    String linea;
                    while ((linea = br.readLine()) != null) {
                        linea = linea.trim();
                        if (!linea.isEmpty()) {
                            Comunicado comunicado = crearComunicado(linea);
                            if (comunicado != null) {

                                listaComunicados.add(comunicado);
                                agregarComunicadoAlLayout(comunicado);
                                count++;
                            }
                        }
                    }
>>>>>>> origin/master
                }
            }
        } catch (IOException e) {
            // si no existe en assets, no pasa nada
        }

        if (count == 0) {
            Toast.makeText(this, "No hay comunicados para mostrar", Toast.LENGTH_LONG).show();
        }
    }
<<<<<<< HEAD

    // NUEVO: abre desde /data/data/.../files si existe; si no, desde assets
    private InputStream archivoComunicadosStream(String nombreArchivo) throws IOException {
        File interno = new File(getFilesDir(), nombreArchivo);
        if (interno.exists()) {
            return new FileInputStream(interno);
        } else {
            return getAssets().open(nombreArchivo);
        }
    }

    // Construye y agrega 1 anuncio al contenedor
    // Construye y agrega 1 anuncio al contenedor (soporta formato antiguo y nuevo CSV)
    private void agregarComunicado(String linea) {
        String titulo, nombre_imagen, info, url;

        if (linea.contains(";")) {
            // Formato antiguo: titulo;imagen;info;url
            String[] partes = linea.split(";", -1);
            if (partes.length < 4) return;
=======

    // Crea instancias de Anuncio o Evento según el tipo
    private Comunicado crearComunicado(String linea) {
        String[] partes = linea.split("\\|");

        // Formato esperado: id,tipo,area,titulo,audiencia,descripcion,imagen,campo_extra[,campo_extra2]
        if (partes.length < 7) return null;

        try {
            String tipo = partes[1].trim();
            String area = partes[2].trim();
            String titulo = partes[3].trim();
            String audiencia = partes[4].trim();
            String descripcion = partes[5].trim();
            String nombreImagen = partes[6].trim();

            if (tipo.equals("anuncio") && partes.length >= 8) {
                String nivelUrgencia = partes[7].trim();
                return new Anuncio(tipo, area, titulo, audiencia, descripcion, nombreImagen, nivelUrgencia);
            } else if (tipo.equals("evento") && partes.length >= 9) {
                String lugar = partes[7].trim();
                String fecha = partes[8].trim();
                return new Evento(tipo, area, titulo, audiencia, descripcion, nombreImagen, lugar, fecha);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    // Agrega un comunicado al layout visual
    private void agregarComunicadoAlLayout(Comunicado comunicado) {
>>>>>>> origin/master

            titulo = partes[0].trim();
            nombre_imagen = partes[1].trim();
            info = partes[2].trim();
            url = partes[3].trim();
        } else {
            // Formato nuevo CSV: id, tipo, área, título, audiencia, descripción, nombreImg, extra, fecha
            String[] p = linea.split("\\s*,\\s*");
            if (p.length < 9) return;

            titulo = p[3].trim();
            nombre_imagen = p[6].trim();
            String tipo = p[1].trim();
            String area = p[2].trim();
            String audiencia = p[4].trim();
            String descripcion = p[5].trim();
            String extra = p[7].trim(); // lugar u urgencia
            String fecha = p[8].trim();

            // Armar info legible
            String cabecera = tipo.toUpperCase() + " · " + area + " · " + fecha;
            info = cabecera + "\nAudiencia: " + audiencia +
                    (tipo.equalsIgnoreCase("evento") ? ("\nLugar: " + extra) : ("\nUrgencia: " + extra)) +
                    "\n\n" + descripcion;

            // En este formato no hay URL
            url = "";
        }

        // Inflar y llenar la vista como antes
        View item = getLayoutInflater().inflate(R.layout.item_comunicado, contenedor, false);

        // Título
        TextView tvTitulo = item.findViewById(R.id.tvTitulo);
        tvTitulo.setText(comunicado.getTitulo());

        // Imagen
        ImageView img = item.findViewById(R.id.imgComunicado);
        String nombreImagen = comunicado.getNombreArchivoImg();
        if (nombreImagen.isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
<<<<<<< HEAD
            // Buscar primero en /files/images/<nombre>.jpg
            File imgFile = new File(new File(getFilesDir(), "images"),
                    nombre_imagen.endsWith(".jpg") ? nombre_imagen : (nombre_imagen + ".jpg"));
            if (imgFile.exists()) {
                img.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                img.setVisibility(View.VISIBLE);
            } else {
                // Fallback: drawable
                int resId = getResources().getIdentifier(nombre_imagen, "drawable", getPackageName());
=======
            // Intentar cargar desde almacenamiento interno primero
            File imagenInterna = new File(getFilesDir(), nombreImagen);
            if (imagenInterna.exists()) {
                // Cargar imagen desde almacenamiento interno
                Uri imageUri = Uri.fromFile(imagenInterna);
                img.setImageURI(imageUri);
                img.setVisibility(View.VISIBLE);
            } else {
                // Si no existe en almacenamiento interno, intentar cargar desde drawable resources
                int resId = getResources().getIdentifier(
                    nombreImagen.replace(".png", "").replace(".jpg", ""),
                    "drawable",
                    getPackageName()
                );
>>>>>>> origin/master
                if (resId != 0) {
                    img.setImageResource(resId);
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setImageResource(android.R.drawable.ic_menu_report_image);
                    img.setVisibility(View.VISIBLE);
                }
            }
        }

        // Información detallada según el tipo
        TextView tvInfo = item.findViewById(R.id.tvInfo);
        String info = comunicado.getDescripcion();
        tvInfo.setText(info);

        // Link - por ahora no se usa, se puede ocultar
        TextView tvLink = item.findViewById(R.id.tvLink);
        tvLink.setVisibility(View.GONE);

        contenedor.addView(item);
    }

<<<<<<< HEAD

    // Lógica separada para abrir el enlace final
    private void abrirEnlace(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
        }
    }
=======
    // Construye la información a mostrar según el tipo de comunicado


>>>>>>> origin/master

    public void volver(View view) {
        finish(); // cierra esta activity y regresa a la anterior
    }
}
