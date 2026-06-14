package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
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
    private MascotaAdapter mascotaAdapter;
    private AppBaseDeDatos db;
    private int idDuenioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos el acceso a la base de datos de Room
        db = AppBaseDeDatos.getInstance(this);

        // Recuperamos el ID del dueño enviado desde el LoginActivity
        idDuenioLogueado = getIntent().getIntExtra("ID_DUENIO", -1);

        // Vincular componentes del layout
        fabAnadirMascota = findViewById(R.id.fabAnadirMascota);
        rvMascotas = findViewById(R.id.rvMascotas);

        // Configurar la orientación del RecyclerView
        rvMascotas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador con una lista vacía para evitar errores de referencia
        mascotaAdapter = new MascotaAdapter(new ArrayList<>(), this);
        rvMascotas.setAdapter(mascotaAdapter);

        // Configurar la acción del botón "+"
        fabAnadirMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AltaMascotaActivity.class);
            // Pasamos el ID del dueño verificado de forma estricta (ej: 3)
            intent.putExtra("ID_DUENIO", idDuenioLogueado);
            startActivity(intent);
        });
    }

    /**
     * El método onResume se dispara AUTOMÁTICAMENTE cada vez que el usuario
     * regresa a esta pantalla. Aquí hacemos la recarga en segundo plano.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Si el ID del dueño es válido, cargamos o refrescamos la lista
        if (idDuenioLogueado != -1) {
            cargarMascotasDesdeBD();
        } else {
            Toast.makeText(this, "Error: Sesión de usuario no válida", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Consulta las mascotas del dueño en un hilo secundario y actualiza el RecyclerView
     */
    private void cargarMascotasDesdeBD() {
        new Thread(() -> {
            try {
                // Buscamos en Room solo las mascotas que pertenecen a este dueño
                final List<Mascota> listaActualizada = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenioLogueado);

                // Volvemos al hilo de la interfaz (UI Thread) para pintar los cambios
                runOnUiThread(() -> {
                    if (listaActualizada != null) {
                        // Pasamos los nuevos datos al adaptador para que repinte la lista
                        mascotaAdapter.updateList(listaActualizada);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}