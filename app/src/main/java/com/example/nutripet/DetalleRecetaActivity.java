package com.example.nutripet;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class DetalleRecetaActivity extends AppCompatActivity {

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

        tvTitulo = findViewById(R.id.tvTituloReceta);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvIngredientes = findViewById(R.id.tvIngredientes);
        tvInstrucciones = findViewById(R.id.tvInstrucciones);
        db = AppBaseDeDatos.getInstance(this);

        int idReceta = getIntent().getIntExtra("ID_RECETA", -1);
        cargarDetalles(idReceta);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void cargarDetalles(int id) {
        new Thread(() -> {
            Receta receta = db.nutriPetDao().obtenerRecetaPorId(id);
            List<String> ingredientes = db.nutriPetDao().obtenerIngredientesDeReceta(id);

            runOnUiThread(() -> {
                if (receta != null) {
                    tvTitulo.setText(receta.getNombre_receta());
                    tvTiempo.setText("Tiempo de preparación: " + receta.getTiempo_preparacion() + " min");
                    tvInstrucciones.setText(receta.getInstrucciones());

                    if (ingredientes != null && !ingredientes.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (String ing : ingredientes) {
                            sb.append("• ").append(ing).append("\n");
                        }
                        tvIngredientes.setText(sb.toString());
                    } else {
                        tvIngredientes.setText("No hay ingredientes listados.");
                    }
                }
            });
        }).start();
    }
}