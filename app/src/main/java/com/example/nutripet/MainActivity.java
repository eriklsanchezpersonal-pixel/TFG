package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAnadirMascota;
    private RecyclerView rvMascotas;
    private FloatingActionButton fabPerfil;
    private TextView tvListaVacia;
    private MascotaAdapter mascotaAdapter;
    private AppBaseDeDatos db;
    private int idDuenioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos el acceso a la base de datos de Room
        db = AppBaseDeDatos.getInstance(this);

        //Recuperamos el ID del dueño enviado desde el LoginActivity
        idDuenioLogueado = getIntent().getIntExtra("ID_DUENIO", -1);

        //Vinculamos componentes del layout
        fabAnadirMascota = findViewById(R.id.fabAnadirMascota);
        rvMascotas = findViewById(R.id.rvMascotas);
        tvListaVacia = findViewById(R.id.tvListaVacia);
        fabPerfil = findViewById(R.id.fabPerfil);

        // Configurar la orientación del RecyclerView
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        //Inicializamos el adaptador pasándole la lista vacía y el contexto (this)
        mascotaAdapter = new MascotaAdapter(new ArrayList<>(), this);
        rvMascotas.setAdapter(mascotaAdapter);

        //Configuramos la acción del botón "+"
        fabAnadirMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AltaMascotaActivity.class);
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });

        fabPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idDuenioLogueado != -1) {
            cargarMascotasDesdeBD();
        } else {
            Toast.makeText(this, "Error: Sesión de usuario no válida", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarMascotasDesdeBD() {
        new Thread(() -> {
            try {
                //Obtenemos la lista de mascotas del dueño
                final List<Mascota> listaActualizada = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenioLogueado);

                if (listaActualizada != null) {
                    for (Mascota m : listaActualizada) {
                        // Obtenemos la LISTA de patologías, no una sola
                        List<Patologia> listaPats = db.nutriPetDao().obtenerPatologiasDeMascota(m.getMicrochip());

                        // Concatenamos los nombres de las patologías
                        StringBuilder sb = new StringBuilder();
                        if (listaPats != null && !listaPats.isEmpty()) {
                            for (int i = 0; i < listaPats.size(); i++) {
                                sb.append(listaPats.get(i).getNombre_patologia());
                                if (i < listaPats.size() - 1) sb.append(", ");
                            }
                            m.setNombrePatologia(sb.toString());
                        } else {
                            m.setNombrePatologia("Sano / Ninguna");
                        }
                    }
                }

                //Actualizamos el adaptador en el hilo principal
                runOnUiThread(() -> {
                    if (listaActualizada != null && !listaActualizada.isEmpty()) {
                        tvListaVacia.setVisibility(android.view.View.GONE);
                        mascotaAdapter.updateList(listaActualizada);
                    } else {
                        tvListaVacia.setVisibility(android.view.View.VISIBLE);
                        mascotaAdapter.updateList(new ArrayList<>());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}