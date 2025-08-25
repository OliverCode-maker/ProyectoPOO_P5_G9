package com.example.comunicadosespol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory; // NUEVO
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import java.io.File;              // NUEVO
import java.io.FileInputStream;  // NUEVO
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VerComunicadosActivity extends AppCompatActivity {
    private LinearLayout contenedor;

    // NUEVO: usamos el mismo nombre de archivo que escribe PublComunicadosActivity
    private static final String NOMBRE_COMUNICADOS = "comunicados.txt";

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
                }
            }
        } catch (IOException e) {
            // si no existe en assets, no pasa nada
        }

        if (count == 0) {
            Toast.makeText(this, "No hay comunicados para mostrar", Toast.LENGTH_LONG).show();
        }
    }

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
        tvTitulo.setText(titulo.isEmpty() ? "Comunicado" : titulo);

        // Imagen (si hay)
        ImageView img = item.findViewById(R.id.imgComunicado);
        if (nombre_imagen.isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
            // Buscar primero en /files/images/<nombre>.jpg
            File imgFile = new File(new File(getFilesDir(), "images"),
                    nombre_imagen.endsWith(".jpg") ? nombre_imagen : (nombre_imagen + ".jpg"));
            if (imgFile.exists()) {
                img.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                img.setVisibility(View.VISIBLE);
            } else {
                // Fallback: drawable
                int resId = getResources().getIdentifier(nombre_imagen, "drawable", getPackageName());
                if (resId != 0) {
                    img.setImageResource(resId);
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setImageResource(android.R.drawable.ic_menu_report_image);
                    img.setVisibility(View.VISIBLE);
                }
            }
        }

        // Información
        TextView tvInfo = item.findViewById(R.id.tvInfo);
        tvInfo.setText(info);

        // Link (si hay)
        TextView tvLink = item.findViewById(R.id.tvLink);
        if (url.isEmpty()) {
            tvLink.setVisibility(View.GONE);
        } else {
            tvLink.setText(url);
            tvLink.setOnClickListener(v -> abrirEnlace(url));
            tvLink.setVisibility(View.VISIBLE);
        }

        contenedor.addView(item);
    }


    // Lógica separada para abrir el enlace final
    private void abrirEnlace(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
        }
    }

    public void volver(View view) {
        finish(); // cierra esta activity y regresa a la anterior
    }
}
