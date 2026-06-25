package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DetalleMascotaActivity extends AppCompatActivity {

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

        // Vincular componentes
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

        btnVerDieta.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecomendadorDietaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", microchipMascota);
            intent.putExtra("PATOLOGIA_MASCOTA", tvPatologia.getText().toString());
            startActivity(intent);
        });

        btnModificar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModificarMascotaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", microchipMascota);
            startActivity(intent);
        });

        btnBorrar.setOnClickListener(v -> confirmarBorrado());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosMascota();
        cargarRecetasAsignadas();
    }

    private void cargarDatosMascota() {
        new Thread(() -> {
            Mascota mascota = db.nutriPetDao().obtenerMascotaPorMicrochip(microchipMascota);
            List<Patologia> listaPatologias = db.nutriPetDao().obtenerPatologiasDeMascota(microchipMascota);

            runOnUiThread(() -> {
                if (mascota != null) {
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

    private void cargarRecetasAsignadas() {
        new Thread(() -> {
            List<Receta> recetas = db.nutriPetDao().obtenerRecetasAsignadasAMascota(microchipMascota);
            runOnUiThread(() -> {
                // false = No es modo añadir, true = Sí mostrar botón eliminar
                RecetaAdapter adapter = new RecetaAdapter(recetas, (receta, esEliminar) -> {
                    if (esEliminar) {
                        eliminarRecetaDeMascota(receta);
                    }
                }, false, true);
                rvRecetasAsignadas.setAdapter(adapter);
            });
        }).start();
    }
    private void eliminarRecetaDeMascota(Receta receta) {
        new Thread(() -> {
            // Asegúrate de tener este método en tu DAO
            db.nutriPetDao().eliminarRecetaDeMascota(microchipMascota, receta.getId_receta());
            runOnUiThread(() -> {
                Toast.makeText(this, "Receta eliminada", Toast.LENGTH_SHORT).show();
                cargarRecetasAsignadas(); // Refresca la lista
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

    private void borrarMascota() {
        new Thread(() -> {
            db.nutriPetDao().borrarRelacionesPatologias(microchipMascota);
            db.nutriPetDao().borrarRelacionesRecetas(microchipMascota);
            db.nutriPetDao().borrarMascotaPorMicrochip(microchipMascota);
            runOnUiThread(this::finish);
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}