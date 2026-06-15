package com.example.nutripet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetalleMascotaActivity extends AppCompatActivity {

    private TextView tvNombre, tvMicrochip, tvFecha, tvPeso, tvActividad, tvPatologia, tvTitulo;
    private Button btnVerDieta;
    private AppBaseDeDatos db;
    private String microchipMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        //Configurar la Toolbar superior
        Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = AppBaseDeDatos.getInstance(this);

        //Recuperamos el microchip de la mascota que el usuario pulsó en la lista
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

        //Cargar los datos desde Room
        cargarDatosMascota();

        //Acción del botón de la dieta
        btnVerDieta.setOnClickListener(v -> {
            //Aquí meteremos la lógica de las dietas en el siguiente paso
        });
    }

    private void cargarDatosMascota() {
        new Thread(() -> {
            //Buscamos los datos básicos de la mascota
            Mascota mascota = db.nutriPetDao().obtenerMascotaPorMicrochip(microchipMascota);

            //Buscamos el nombre de su patología a través de la tabla intermedia
            String nombrePatologia = db.nutriPetDao().obtenerPatologiaDeMascota(microchipMascota);

            runOnUiThread(() -> {
                if (mascota != null) {
                    // Cambiamos el título de la barra por el nombre de la mascota
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Perfil de " + mascota.getNombre());
                    }

                    tvNombre.setText("Nombre: " + mascota.getNombre());
                    tvMicrochip.setText("Microchip: " + mascota.getMicrochip());
                    tvFecha.setText("Fecha Nacimiento: " + mascota.getFecha_nacimiento());
                    tvPeso.setText("Peso Actual: " + mascota.getPeso_actual() + " kg");
                    tvActividad.setText("Nivel de Actividad: " + mascota.getNivel_actividad());

                    if (nombrePatologia != null && !nombrePatologia.isEmpty()) {
                        tvPatologia.setText("Patología: " + nombrePatologia);
                    } else {
                        tvPatologia.setText("Patología: Ninguna / Sano");
                        tvPatologia.setTextColor(android.graphics.Color.parseColor("#2E7D32")); // Verde si está sano
                    }
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Vuelve a la pantalla anterior al pulsar la flecha de arriba
        return true;
    }
}