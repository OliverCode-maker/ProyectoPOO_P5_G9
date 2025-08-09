package com.example.comunicadosespol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VerComunicadosActivity extends AppCompatActivity {
    private LinearLayout contenedor;

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

        cargarComunicados("comunicados.txt");
    }
    // Carga todos los comunicados de comunicados.txt
    private void cargarComunicados(String nombreArchivo) {

        int count = 0;
        try (InputStream is = getAssets().open(nombreArchivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                agregarComunicado(linea);
                count++;
            }
        } catch (IOException e) {
            Toast.makeText(this, "No se pudieron cargar los comunicados", Toast.LENGTH_LONG).show();
        }

        if (count == 0) {
            Toast.makeText(this, "No hay comunicados para mostrar", Toast.LENGTH_LONG).show();
        }
    }
    // Construye y agrega 1 anuncio al contenedor
    private void agregarComunicado(String linea) {
        String[] partes = linea.split(";", -1); // Espera: titulo;imagen;info;url  (campos pueden venir vacíos)
        if (partes.length < 4) return;

        String titulo = partes[0].trim();
        String nombre_imagen = partes[1].trim();
        String info   = partes[2].trim();
        String url    = partes[3].trim();

        View item = getLayoutInflater().inflate(R.layout.item_comunicado, contenedor, false);

        // Título
        TextView tvTitulo = item.findViewById(R.id.tvTitulo);
        tvTitulo.setText(titulo.isEmpty() ? "Comunicado" : titulo);

        // Imagen (si hay)
        ImageView img = item.findViewById(R.id.imgComunicado);
        if (nombre_imagen.isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
            int resId = getResources().getIdentifier(nombre_imagen, "drawable", getPackageName());
            if (resId != 0) {
                img.setImageResource(resId);
                img.setVisibility(View.VISIBLE);
            } else {
                img.setImageResource(android.R.drawable.ic_menu_report_image); // Imagen por defecto
                img.setVisibility(View.VISIBLE);
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