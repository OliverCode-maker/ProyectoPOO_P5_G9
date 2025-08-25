package com.example.comunicadosespol;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Modelo.Anuncio;
import Modelo.Comunicado;
import Modelo.Evento;

/**
 * Pantalla que lista los comunicados pertenecientes al usuario autenticado.
 * <p>Lee un archivo interno plano y muestra los datos en una tabla.</p>
 */
public class MisComunicadosActivity extends AppCompatActivity {

    /** Botón para ordenar por título. */
    private Button btnOrdenar;
    /** Tabla que contiene los comunicados. */
    private TableLayout misComLayout;
    /** Botón para serializar la lista. */
    private Button btnGuardar;
    /** Botón para volver/cancelar. */
    private Button btnCancelar;
    /** Identificador del usuario dueño de los comunicados (en texto). */
    private String userID;

    /** Lista en memoria de comunicados del usuario. */
    private static ArrayList<Comunicado> comunicados = new ArrayList<>();

    /**
     * Ciclo de vida: crea la UI, carga los comunicados del usuario y los pinta.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_comunicados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userID = getIntent().getStringExtra(MainActivity.KEY_USER_ID);
        btnOrdenar = findViewById(R.id.btnOrdenPorTitulo);
        btnCancelar = findViewById(R.id.btnCancel);
        btnGuardar = findViewById(R.id.btnSave);
        misComLayout = findViewById(R.id.tablaMisComunicados);

        // Carga síncrona (en el hilo principal) usando un Thread + join()
        Thread cargarComunicadosThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cargarComunicados();
            }
        });
        cargarComunicadosThread.start();
        try {
            cargarComunicadosThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (comunicados != null){
            agregarComunicadosAlLayout(comunicados);
        }else{
            Toast.makeText(this, "No hay comunicados para mostrar", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lee el archivo interno {@code comunicados.txt}, filtra por {@code userID}
     * y construye objetos {@link Anuncio} o {@link Evento}.
     *
     * <p>Formato esperado:
     * <ul>
     *   <li>Anuncio: id|Anuncio|area|titulo|audiencia|descripcion|img|urgencia|userId</li>
     *   <li>Evento:  id|Evento |area|titulo|audiencia|descripcion|img|lugar|fecha|userId</li>
     * </ul></p>
     */
    public void cargarComunicados() {
        comunicados.clear();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("comunicados.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    if ("Anuncio".equalsIgnoreCase(parts[1])) {
                        if (parts[8].equalsIgnoreCase(userID)) {
                            comunicados.add(new Anuncio(parts[1], parts[2], parts[3],
                                    parts[4], parts[5], parts[6], parts[7]));
                        }
                    } else if ("Evento".equalsIgnoreCase(parts[1]) && parts.length == 10) {
                        if (parts[9].equalsIgnoreCase(userID)) {
                            comunicados.add(new Evento(parts[1], parts[2], parts[3], parts[4],
                                    parts[5], parts[6], parts[7], parts[8]));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        }
    }

    /**
     * Crea las filas de la tabla a partir de la lista de comunicados.
     * @param comunicados lista a mostrar
     */
    public void agregarComunicadosAlLayout(ArrayList<Comunicado> comunicados) {
        misComLayout.removeAllViews();

        // Encabezado
        TableRow headerRow = new TableRow(this);
        TextView headerTitulo = new TextView(this);
        headerTitulo.setText("Título");
        headerTitulo.setTextAppearance(this, R.style.TablaHeader);

        TextView headerFecha = new TextView(this);
        headerFecha.setText("Fecha");
        headerFecha.setTextAppearance(this, R.style.TablaHeader);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        headerTitulo.setLayoutParams(params);
        headerFecha.setLayoutParams(params);

        headerRow.addView(headerTitulo);
        headerRow.addView(headerFecha);
        misComLayout.addView(headerRow);

        // Filas de datos
        for (Comunicado comunicado : comunicados) {
            TableRow row = new TableRow(this);

            TextView tituloCell = new TextView(this);
            tituloCell.setText(comunicado.getTitulo());
            tituloCell.setTextAppearance(this, R.style.TablaCelda);

            TextView fechaCell = new TextView(this);
            String fecha = comunicado instanceof Evento ? ((Evento) comunicado).getFecha() : "Sin fecha";
            fechaCell.setText(fecha);
            fechaCell.setTextAppearance(this, R.style.TablaCelda);

            tituloCell.setLayoutParams(params);
            fechaCell.setLayoutParams(params);

            row.addView(tituloCell);
            row.addView(fechaCell);
            misComLayout.addView(row);
        }
    }

    /**
     * Ordena la lista por el orden natural ({@link Comunicado#compareTo(Comunicado)})
     * y vuelve a pintar la tabla.
     */
    public void ordenar(android.view.View view) {
        comunicados.sort(Comunicado::compareTo);
        agregarComunicadosAlLayout(comunicados);
    }

    /**
     * Serializa la lista a un archivo con fecha en el nombre.
     * <p>Nombre: {@code comunicados_al_dd_MM_yyyy.dat}</p>
     */
    public void serializarComunicado(android.view.View view) {
        String fileName = "comunicados_al_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date()) + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE))) {
            oos.writeObject(comunicados);
            Toast.makeText(this, "Lista serializada en " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al serializar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cierra la Activity y vuelve a la anterior.
     */
    public void volver(android.view.View view) {
        finish();
    }
}
