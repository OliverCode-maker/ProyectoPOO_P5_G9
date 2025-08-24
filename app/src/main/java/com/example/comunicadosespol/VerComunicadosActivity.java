package com.example.comunicadosespol;


import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.io.File;
import java.io.FileInputStream;
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
    private List<Comunicado> listaComunicados;
    private EditText fechaFiltro;
    private Button filtroButton;
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
        listaComunicados = new ArrayList<>();
        fechaFiltro = findViewById(R.id.filtroDate);
        filtroButton = findViewById(R.id.filtroButton);
        cargarComunicados("comunicados.txt");


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
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, "No se pudieron cargar los comunicados", Toast.LENGTH_LONG).show();
        }

        if (count == 0) {
            Toast.makeText(this, "No hay comunicados para mostrar", Toast.LENGTH_LONG).show();
        }
    }

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

    // Construye la información a mostrar según el tipo de comunicado



    public void volver(View view) {
        finish(); // cierra esta activity y regresa a la anterior
    }
}