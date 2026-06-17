package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetalleMascotaActivity extends AppCompatActivity {

    private TextView tvNombre, tvMicrochip, tvFecha, tvPeso, tvActividad, tvPatologia, tvTitulo;
    private RecyclerView rvRecetasAsignadas; // 🌟 Declarado
    private Button btnVerDieta;
    private AppBaseDeDatos db;
    private String microchipMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppBaseDeDatos.getInstance(this);
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");

        // Vincular componentes
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvMicrochip = findViewById(R.id.tvDetalleMicrochip);
        tvFecha = findViewById(R.id.tvDetalleFecha);
        tvPeso = findViewById(R.id.tvDetallePeso);
        tvActividad = findViewById(R.id.tvDetalleActividad);
        tvPatologia = findViewById(R.id.tvDetallePatologia);
        btnVerDieta = findViewById(R.id.btnVerDieta);

        rvRecetasAsignadas = findViewById(R.id.rvRecetasAsignadas);
        rvRecetasAsignadas.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos
        cargarDatosMascota();
        cargarRecetasAsignadas();

        btnVerDieta.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleMascotaActivity.this, RecomendadorDietaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", microchipMascota);
            intent.putExtra("PATOLOGIA_MASCOTA", tvPatologia.getText().toString());
            startActivity(intent);
        });
    }

    private void cargarRecetasAsignadas() {
        new Thread(() -> {
            List<Receta> recetas = db.nutriPetDao().obtenerRecetasAsignadasAMascota(microchipMascota);

            runOnUiThread(() -> {
                // Pasamos null al listener porque aquí solo queremos mostrar la lista
                RecetaAdapter adapter = new RecetaAdapter(recetas, null);
                rvRecetasAsignadas.setAdapter(adapter);
            });
        }).start();
    }

    private void cargarDatosMascota() {
        new Thread(() -> {
            Mascota mascota = db.nutriPetDao().obtenerMascotaPorMicrochip(microchipMascota);
            String nombrePatologia = db.nutriPetDao().obtenerPatologiaDeMascota(microchipMascota);

            runOnUiThread(() -> {
                if (mascota != null) {
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Perfil de " + mascota.getNombre());
                    tvNombre.setText("Nombre: " + mascota.getNombre());
                    tvMicrochip.setText("Microchip: " + mascota.getMicrochip());
                    tvFecha.setText("Fecha Nacimiento: " + mascota.getFecha_nacimiento());
                    tvPeso.setText("Peso Actual: " + mascota.getPeso_actual() + " kg");
                    tvActividad.setText("Nivel de Actividad: " + mascota.getNivel_actividad());

                    if (nombrePatologia != null && !nombrePatologia.isEmpty()) {
                        tvPatologia.setText("Patología: " + nombrePatologia);
                    } else {
                        tvPatologia.setText("Patología: Ninguna / Sano");
                    }
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}