package com.example.comunicadosespol;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Modelo.Comunicado;
import Modelo.Anuncio;
import Modelo.DatosIncompletosException;
import Modelo.Evento;

/**
 * Pantalla que muestra comunicados desde archivo (interno o assets)
 * y permite filtrar los eventos por fecha exacta.
 */
public class VerComunicadosActivity extends AppCompatActivity {

    /** Contenedor visual donde se agregan las tarjetas de comunicados. */
    private LinearLayout contenedor;
    /** Lista en memoria de comunicados cargados. */
    private List<Comunicado> listaComunicados;
    /** Campo para ingresar la fecha de filtro (dd/MM/yyyy). */
    private EditText fechaFiltro;
    /** Botón para aplicar el filtro. */
    private Button filtroButton;

    /**
     * Ciclo de vida: inicializa la UI, carga comunicados y configura el date picker.
     */
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

        // DatePicker para seleccionar la fecha del filtro
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

    /**
     * Manejador del botón de filtro: valida formato y filtra eventos
     * cuya fecha coincide exactamente con la ingresada.
     * @param view vista que dispara el evento
     */
    public void onClickFiltrar(View view) {
        String fechaTexto = fechaFiltro.getText().toString().trim();
        if (fechaTexto.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una fecha para filtrar", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validarFechaEvento(fechaTexto)) {
            return;
        }

        contenedor.removeAllViews();

        for (Comunicado comunicado : listaComunicados) {
            if (comunicado instanceof Evento) {
                Evento evento = (Evento) comunicado;
                if (evento.getFecha().equals(fechaTexto)) {
                    agregarComunicadoAlLayout(evento);
                }
            }
        }
    }

    /**
     * Valida el formato de la fecha y que sea posterior a hoy.
     * <p>Esta validación replica la lógica del formulario de publicación.</p>
     * @param fechaTexto fecha en formato dd/MM/yyyy
     * @return {@code true} si es válida; {@code false} en caso contrario
     */
    private boolean validarFechaEvento(String fechaTexto) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        formatoFecha.setLenient(false);

        try {
            Date fechaEvento = formatoFecha.parse(fechaTexto);

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            Date fechaActual = hoy.getTime();

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

    /**
     * Carga comunicados desde almacenamiento interno; si no existe, intenta desde assets.
     * Por cada línea válida agrega el comunicado a la lista y lo pinta en el contenedor.
     * @param nombreArchivo nombre del archivo a cargar
     */
    private void cargarComunicados(String nombreArchivo) {
        int count = 0;

        try {
            File file = new File(getFilesDir(), nombreArchivo);
            if (file.exists()) {
                // Interno
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
                // Assets
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

    /**
     * Parsea una línea del archivo y crea un {@link Anuncio} o {@link Evento} según el tipo.
     *
     * <p>Formatos aceptados:
     * <ul>
     *   <li>Anuncio: id|anuncio|area|titulo|audiencia|descripcion|img|urgencia[|userId]</li>
     *   <li>Evento:  id|evento |area|titulo|audiencia|descripcion|img|lugar|fecha[|userId]</li>
     * </ul>
     * El tipo se compara de forma case-insensitive.</p>
     *
     * @param linea línea del archivo con separador {@code |}
     * @return instancia de {@link Comunicado} o {@code null} si la línea no es válida
     */
    private Comunicado crearComunicado(String linea) {
        String[] partes = linea.split("\\|");
        if (partes.length < 7) return null;

        try {
            String tipo = partes[1].trim();
            String area = partes[2].trim();
            String titulo = partes[3].trim();
            String audiencia = partes[4].trim();
            String descripcion = partes[5].trim();
            String nombreImagen = partes[6].trim();

            if (tipo.equalsIgnoreCase("anuncio") && partes.length >= 8) {
                String nivelUrgencia = partes[7].trim();
                return new Anuncio(tipo, area, titulo, audiencia, descripcion, nombreImagen, nivelUrgencia);
            } else if (tipo.equalsIgnoreCase("evento") && partes.length >= 9) {
                String lugar = partes[7].trim();
                String fecha = partes[8].trim();
                return new Evento(tipo, area, titulo, audiencia, descripcion, nombreImagen, lugar, fecha);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * Infla una vista de tarjeta y muestra el comunicado con su imagen (si existe) y descripción.
     * @param comunicado objeto a representar en la UI
     */
    private void agregarComunicadoAlLayout(Comunicado comunicado) {
        View item = getLayoutInflater().inflate(R.layout.item_comunicado, contenedor, false);

        // Título
        TextView tvTitulo = item.findViewById(R.id.tvTitulo);
        tvTitulo.setText(comunicado.getTitulo());

        // Imagen: intenta primero en almacenamiento interno, luego en drawables
        ImageView img = item.findViewById(R.id.imgComunicado);
        String nombreImagen = comunicado.getNombreArchivoImg();
        if (nombreImagen.isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
            File imagenInterna = new File(getFilesDir(), nombreImagen);
            if (imagenInterna.exists()) {
                Uri imageUri = Uri.fromFile(imagenInterna);
                img.setImageURI(imageUri);
                img.setVisibility(View.VISIBLE);
            } else {
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

        // Descripción / info
        TextView tvInfo = item.findViewById(R.id.tvInfo);
        String info = comunicado.getDescripcion();
        tvInfo.setText(info);

        // Enlace (oculto en esta versión)
        TextView tvLink = item.findViewById(R.id.tvLink);
        tvLink.setVisibility(View.GONE);

        contenedor.addView(item);
    }

    /**
     * Cierra la Activity.
     * @param view vista que dispara el evento
     */
    public void volver(View view) {
        finish();
    }
}
