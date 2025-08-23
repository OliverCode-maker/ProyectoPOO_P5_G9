package com.example.comunicadosespol;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import Modelo.Anuncio;
import Modelo.Comunicado;
import Modelo.Evento;

public class MisComunicadosActivity extends AppCompatActivity {
    private Button btnOrdenar;

    private TableLayout misComLayout;

    private Button btnGuardar;
    private Button btnCancelar;

    //static arralist de comunicados
    private static ArrayList<Comunicado> comunicados = new ArrayList<>();

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

        btnOrdenar = findViewById(R.id.btnOrdenPorTitulo);
        btnCancelar = findViewById(R.id.btnCancel);
        btnGuardar = findViewById(R.id.btnSave);

        misComLayout = findViewById(R.id.tablaMisComunicados);

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

        agregarComunicadosAlLayout(comunicados);
    }

    public void cargarComunicados() {
        comunicados.clear();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("comunicados.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    if ("Anuncio".equalsIgnoreCase(parts[1])) {
                        comunicados.add(new Anuncio(parts[1], parts[2], parts[3],
                                parts[4], parts[5], parts[6], parts[7]));
                    } else if ("Evento".equalsIgnoreCase(parts[1]) && parts.length == 9) {
                        comunicados.add(new Evento(parts[1], parts[2], parts[3], parts[4],
                                parts[5], parts[6], parts[7], parts[8]));
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public void agregarComunicadosAlLayout(ArrayList<Comunicado> comunicados) {
        misComLayout.removeAllViews();

        // Crear encabezado
        TableRow headerRow = new TableRow(this);

        TextView headerTitulo = new TextView(this);
        headerTitulo.setText("Título");
        headerTitulo.setTextAppearance(this, R.style.TablaHeader);

        TextView headerFecha = new TextView(this);
        headerFecha.setText("Fecha");
        headerFecha.setTextAppearance(this, R.style.TablaHeader);

        // Layout params para que se expandan igual
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        headerTitulo.setLayoutParams(params);
        headerFecha.setLayoutParams(params);

        headerRow.addView(headerTitulo);
        headerRow.addView(headerFecha);
        misComLayout.addView(headerRow);

        // Agregar comunicados
        for (Comunicado comunicado : comunicados) {
            TableRow row = new TableRow(this);

            TextView tituloCell = new TextView(this);
            tituloCell.setText(comunicado.getTitulo());
            tituloCell.setTextAppearance(this, R.style.TablaCelda);

            TextView fechaCell = new TextView(this);
            String fecha = comunicado instanceof Evento ? ((Evento) comunicado).getFecha() : "Sin fecha";
            fechaCell.setText(fecha);
            fechaCell.setTextAppearance(this, R.style.TablaCelda);

            // Mismo layout params
            tituloCell.setLayoutParams(params);
            fechaCell.setLayoutParams(params);

            row.addView(tituloCell);
            row.addView(fechaCell);
            misComLayout.addView(row);
        }
    }

    public void ordenar(View view) {
        comunicados.sort(Comunicado::compareTo);
        agregarComunicadosAlLayout(comunicados);
    }

    // Simplificar la serialización
    public void serializarComunicado(View view) {
        String fileName = "comunicados_al_" + new SimpleDateFormat("dd_MM_yyyy").format(new Date()) + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE))) {
            oos.writeObject(comunicados);

            Toast.makeText(this, "Lista serializada en " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al serializar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void volver(View view) {
        finish();
    }
}