package com.example.nutripet;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

// Actividad responsable de mostrar la información detallada de una receta seleccionada
public class DetalleRecetaActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_DETALLE_RECETA";
    private TextView tvTitulo, tvTiempo, tvIngredientes, tvInstrucciones;
    private AppBaseDeDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_receta);

        Toolbar toolbar = findViewById(R.id.toolbarDetalleReceta);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Detalle de Receta");
        }

        // Vincular componentes de la interfaz
        tvTitulo = findViewById(R.id.tvTituloReceta);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvIngredientes = findViewById(R.id.tvIngredientes);
        tvInstrucciones = findViewById(R.id.tvInstrucciones);

        db = AppBaseDeDatos.getInstance(this);

        // Recuperar ID pasado por el Intent anterior
        int idReceta = getIntent().getIntExtra("ID_RECETA", -1);
        Log.d(TAG, "Cargando detalle para ID_RECETA: " + idReceta);

        cargarDetalles(idReceta);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Consulta los datos de la receta e ingredientes en un hilo secundario
    private void cargarDetalles(int id) {
        new Thread(() -> {
            Log.d(TAG, "Consultando datos de receta en BD...");
            Receta receta = db.nutriPetDao().obtenerRecetaPorId(id);
            List<String> ingredientes = db.nutriPetDao().obtenerIngredientesDeReceta(id);

            runOnUiThread(() -> {
                if (receta != null) {
                    Log.d(TAG, "Receta encontrada: " + receta.getNombre_receta());
                    tvTitulo.setText(receta.getNombre_receta());
                    tvTiempo.setText("Tiempo de preparación: " + receta.getTiempo_preparacion() + " min");
                    tvInstrucciones.setText(receta.getInstrucciones());

                    // Procesar la lista de ingredientes para mostrarla como texto con viñetas
                    if (ingredientes != null && !ingredientes.isEmpty()) {
                        Log.d(TAG, "Ingredientes encontrados: " + ingredientes.size());
                        StringBuilder sb = new StringBuilder();
                        for (String ing : ingredientes) {
                            sb.append("• ").append(ing).append("\n");
                        }
                        tvIngredientes.setText(sb.toString());
                    } else {
                        Log.d(TAG, "No se encontraron ingredientes para esta receta");
                        tvIngredientes.setText("No hay ingredientes listados.");
                    }
                } else {
                    Log.e(TAG, "Error: La receta con ID " + id + " no existe en la BD");
                }
            });
        }).start();
    }
}