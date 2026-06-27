package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

// Pantalla principal: muestra la lista de mascotas registradas por el usuario logueado
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_MAIN_ACTIVITY";
    private FloatingActionButton fabAnadirMascota, fabCalendario, fabPerfil;
    private RecyclerView rvMascotas;
    private TextView tvListaVacia;
    private MascotaAdapter mascotaAdapter;
    private AppBaseDeDatos db;
    private int idDuenioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos la base de datos de Room
        db = AppBaseDeDatos.getInstance(this);

        // Recuperamos el ID del dueño enviado desde el LoginActivity
        idDuenioLogueado = getIntent().getIntExtra("ID_DUENIO", -1);
        Log.d(TAG, "MainActivity inicializada con ID_DUENIO: " + idDuenioLogueado);

        // Vinculamos componentes del layout
        fabAnadirMascota = findViewById(R.id.fabAnadirMascota);
        rvMascotas = findViewById(R.id.rvMascotas);
        tvListaVacia = findViewById(R.id.tvListaVacia);
        fabPerfil = findViewById(R.id.fabPerfil);
        fabCalendario = findViewById(R.id.fabCalendario);

        // Configurar la orientación del RecyclerView
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador
        mascotaAdapter = new MascotaAdapter(new ArrayList<>(), this);
        rvMascotas.setAdapter(mascotaAdapter);

        // Configuración de los botones de navegación
        fabAnadirMascota.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a AltaMascotaActivity");
            Intent intent = new Intent(MainActivity.this, AltaMascotaActivity.class);
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });

        fabPerfil.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a PerfilUsuarioActivity");
            Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });

        fabCalendario.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a CalendarioCitasActivity");
            Intent intent = new Intent(this, CalendarioCitasActivity.class);
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Recargando lista de mascotas");
        if (idDuenioLogueado != -1) {
            cargarMascotasDesdeBD();
        } else {
            Toast.makeText(this, "Error: Sesión de usuario no válida", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: ID_DUENIO es -1 en onResume");
        }
    }

    // Consulta en segundo plano para obtener mascotas y sus patologías asociadas
    private void cargarMascotasDesdeBD() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Consultando mascotas para dueño: " + idDuenioLogueado);
                final List<Mascota> listaActualizada = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenioLogueado);

                if (listaActualizada != null) {
                    for (Mascota m : listaActualizada) {
                        List<Patologia> listaPats = db.nutriPetDao().obtenerPatologiasDeMascota(m.getMicrochip());

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
                    Log.d(TAG, "Mascotas procesadas: " + listaActualizada.size());
                }

                // Actualizamos la UI en el hilo principal
                runOnUiThread(() -> {
                    if (listaActualizada != null && !listaActualizada.isEmpty()) {
                        tvListaVacia.setVisibility(View.GONE);
                        mascotaAdapter.updateList(listaActualizada);
                    } else {
                        Log.d(TAG, "No hay mascotas para mostrar");
                        tvListaVacia.setVisibility(View.VISIBLE);
                        mascotaAdapter.updateList(new ArrayList<>());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar mascotas: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}