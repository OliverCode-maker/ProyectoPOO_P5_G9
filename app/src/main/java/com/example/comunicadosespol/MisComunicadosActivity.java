package com.example.comunicadosespol;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Modelo.Anuncio;
import Modelo.Comunicado;
import Modelo.Evento;

public class MisComunicadosActivity extends AppCompatActivity {
    private Button btnOrdenar;
    private LinearLayout misComLayout;
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
        misComLayout = findViewById(R.id.scrollLayout);

        cargarComunicados();
        agregarComunicadosAlLayout(comunicados);
    }

    public void cargarComunicados() {
        comunicados.clear();
        try {
            java.io.FileInputStream fis = openFileInput("comunicados.txt");
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                // Anuncio: id,tipo,area,titulo,audiencia,descripcion,nombreImagen,urgencia
                // Evento: id,tipo,area,titulo,audiencia,descripcion,nombreImagen,lugar,fecha
                String[] parts = line.split(",");
                if (parts.length == 8 && parts[1].equalsIgnoreCase("Anuncio")) {
                    // Los índices correctos para Anuncio: area, titulo, audiencia, descripcion, nombreImagen, urgencia
                    comunicados.add(new Anuncio(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]));
                } else if (parts.length == 9 && parts[1].equalsIgnoreCase("Evento")) {
                    // Los índices correctos para Evento: area, titulo, audiencia, descripcion, nombreImagen, lugar, fecha
                    comunicados.add(new Evento(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]));
                }
            }
            reader.close();
            fis.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los comunicados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void agregarComunicadosAlLayout(ArrayList<Comunicado> comunicados) {
        // Limpiar el contenedor antes de agregar el nuevo comunicado
        misComLayout.removeAllViews();


        int i = 0;

        for (Comunicado comunicado : comunicados) {
            View item = getLayoutInflater().inflate(R.layout.item_mis_comunicados, misComLayout, false);
            TextView Titulo = item.findViewById(R.id.Titulo);
            // Título
            if (i == 0){
                Titulo.setText("Titulo");
            }
            else{
                Titulo.setText(comunicado.getTitulo());
            }
            TextView Fecha = item.findViewById(R.id.Fecha);
            if ( i == 0) {
                Fecha.setText("Fecha");
                i++;
            }
            else if (comunicado instanceof Anuncio) {
                Fecha.setText("Anuncio no posee fecha");
            } else if (comunicado instanceof Evento) {
                Fecha.setText(((Evento) comunicado).getFecha());
            }

            // IMPORTANTE: Agregar la vista al layout
            misComLayout.addView(item);
        }
    }

    public void ordenar(View view) {
        comunicados.sort(Comunicado::compareTo);
        agregarComunicadosAlLayout(comunicados);
    }

    public void serializarComunicado(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String fecha = sdf.format(new java.util.Date());
        String fileName = "comunicados_al_" + fecha + ".dat";
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(comunicados);
            oos.close();
            fos.close();
            Toast.makeText(this, "Lista serializada en " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al serializar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void volver(View view) {
        finish();
    }
}