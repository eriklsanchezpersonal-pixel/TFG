package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMascotas;
    private TextView tvSinMascotas;
    private FloatingActionButton fabAnadirMascota;
    private AppBaseDeDatos db;
    private int idDuenioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppBaseDeDatos.getInstance(this);

        //Recuperamos el ID del dueño que pasamos desde el LoginActivity
        idDuenioLogueado = getIntent().getIntExtra("ID_DUENIO", -1);

        //Vinculamos las vistas
        rvMascotas = findViewById(R.id.rvMascotas);
        tvSinMascotas = findViewById(R.id.tvSinMascotas);
        fabAnadirMascota = findViewById(R.id.fabAnadirMascota);

        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        //Configuramos el botón para añadir nueva mascota
        fabAnadirMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AltaMascotaActivity.class);
            // Usamos la clave estandarizada sin "I" al final para evitar pérdidas de ID
            intent.putExtra("ID_DUENO", idDuenioLogueado);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Cada vez que volvamos a esta pantalla por ejemplo, tras guardar una mascota, refrescamos la lista
        cargarMascotas();
    }

    private void cargarMascotas() {
        //Obtenemos las mascotas asignadas a este dueño desde el DAO
        //OJO: Asegúrate de tener este método en tu NutriPetDao, si cambia el nombre adáptalo
        List<Mascota> listaMascotas = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenioLogueado);

        if (listaMascotas == null || listaMascotas.isEmpty()) {
            tvSinMascotas.setVisibility(View.VISIBLE);
            rvMascotas.setVisibility(View.GONE);
        } else {
            tvSinMascotas.setVisibility(View.GONE);
            rvMascotas.setVisibility(View.VISIBLE);

            // Aquí más adelante le asignaremos un Adaptador (Adapter) para pintarlas en tarjetas bonitas
        }
    }
}