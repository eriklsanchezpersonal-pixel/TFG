package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Actividad que muestra la información detallada de una mascota y permite gestionar su dieta, modificarla o eliminarla
public class DetalleMascotaActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_DETALLE_MASCOTA";
    private TextView tvNombre, tvMicrochip, tvFecha, tvPeso, tvActividad, tvPatologia;
    private RecyclerView rvRecetasAsignadas;
    private Button btnVerDieta, btnModificar, btnBorrar;
    private AppBaseDeDatos db;
    private String microchipMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = AppBaseDeDatos.getInstance(this);
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");
        Log.d(TAG, "Iniciando DetalleMascotaActivity con microchip: " + microchipMascota);

        // Vincular componentes de la interfaz
        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvMicrochip = findViewById(R.id.tvDetalleMicrochip);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        tvPeso = findViewById(R.id.tvDetallePeso);
        tvActividad = findViewById(R.id.tvDetalleActividad);
        tvPatologia = findViewById(R.id.tvDetallePatologia);
        btnVerDieta = findViewById(R.id.btnVerDieta);
        btnModificar = findViewById(R.id.btnModificarMascota);
        btnBorrar = findViewById(R.id.btnBorrarMascota);

        rvRecetasAsignadas = findViewById(R.id.rvRecetasAsignadas);
        rvRecetasAsignadas.setLayoutManager(new LinearLayoutManager(this));

        // Configuración de listeners para navegación y acciones
        btnVerDieta.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a RecomendadorDietaActivity");
            Intent intent = new Intent(this, RecomendadorDietaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", microchipMascota);
            intent.putExtra("PATOLOGIA_MASCOTA", tvPatologia.getText().toString());
            startActivity(intent);
        });

        btnModificar.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a ModificarMascotaActivity");
            Intent intent = new Intent(this, ModificarMascotaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", microchipMascota);
            startActivity(intent);
        });

        btnBorrar.setOnClickListener(v -> confirmarBorrado());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Recargando datos de la mascota");
        cargarDatosMascota();
        cargarRecetasAsignadas();
    }

    // Consulta en segundo plano para obtener info básica y patologías
    private void cargarDatosMascota() {
        new Thread(() -> {
            Mascota mascota = db.nutriPetDao().obtenerMascotaPorMicrochip(microchipMascota);
            List<Patologia> listaPatologias = db.nutriPetDao().obtenerPatologiasDeMascota(microchipMascota);

            runOnUiThread(() -> {
                if (mascota != null) {
                    Log.d(TAG, "Datos de mascota cargados: " + mascota.getNombre());
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Perfil de " + mascota.getNombre());
                    tvNombre.setText("Nombre: " + mascota.getNombre());
                    tvMicrochip.setText("Microchip: " + mascota.getMicrochip());
                    tvFecha.setText("Fecha Nacimiento: " + mascota.getFecha_nacimiento());
                    tvPeso.setText("Peso Actual: " + mascota.getPeso_actual() + " kg");
                    tvActividad.setText("Nivel de Actividad: " + mascota.getNivel_actividad());

                    if (listaPatologias != null && !listaPatologias.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < listaPatologias.size(); i++) {
                            sb.append(listaPatologias.get(i).getNombre_patologia());
                            if (i < listaPatologias.size() - 1) sb.append(", ");
                        }
                        tvPatologia.setText("Patologías: " + sb.toString());
                    } else {
                        tvPatologia.setText("Patologías: Ninguna");
                    }
                }
            });
        }).start();
    }

    // Consulta en segundo plano para obtener recetas asignadas
    private void cargarRecetasAsignadas() {
        new Thread(() -> {
            List<Receta> recetas = db.nutriPetDao().obtenerRecetasAsignadasAMascota(microchipMascota);
            Log.d(TAG, "Recetas encontradas para la mascota: " + (recetas != null ? recetas.size() : 0));
            runOnUiThread(() -> {
                RecetaAdapter adapter = new RecetaAdapter(recetas, (receta, esEliminar) -> {
                    if (esEliminar) {
                        eliminarRecetaDeMascota(receta);
                    }
                }, false, true);
                rvRecetasAsignadas.setAdapter(adapter);
            });
        }).start();
    }

    // Proceso de eliminación de una receta específica asignada a la mascota
    private void eliminarRecetaDeMascota(Receta receta) {
        new Thread(() -> {
            Log.d(TAG, "Eliminando receta ID: " + receta.getId_receta());
            db.nutriPetDao().eliminarRecetaDeMascota(microchipMascota, receta.getId_receta());
            runOnUiThread(() -> {
                Toast.makeText(this, "Receta eliminada", Toast.LENGTH_SHORT).show();
                cargarRecetasAsignadas();
            });
        }).start();
    }

    private void confirmarBorrado() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Mascota")
                .setMessage("¿Estás seguro? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (d, w) -> borrarMascota())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Proceso completo de eliminación de la mascota y sus relaciones
    private void borrarMascota() {
        new Thread(() -> {
            Log.d(TAG, "Iniciando borrado total de la mascota: " + microchipMascota);
            db.nutriPetDao().borrarRelacionesPatologias(microchipMascota);
            db.nutriPetDao().borrarRelacionesRecetas(microchipMascota);
            db.nutriPetDao().borrarMascotaPorMicrochip(microchipMascota);
            Log.d(TAG, "Mascota eliminada correctamente");
            runOnUiThread(this::finish);
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}