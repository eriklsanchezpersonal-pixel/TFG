package com.example.nutripet;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

// Actividad: Recomendador de dietas basado en ingredientes y patologías
public class RecomendadorDietaActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_RECOMENDADOR";

    private LinearLayout contenedorIngredientes;
    private RecyclerView rvRecetas;
    private FloatingActionButton btnAtras;
    private AppBaseDeDatos db;
    private String microchipMascota;
    private List<String> ingredientesSeleccionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendar_dieta);

        db = AppBaseDeDatos.getInstance(this);
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");
        Log.d(TAG, "Recomendador iniciado para Mascota: " + microchipMascota);

        contenedorIngredientes = findViewById(R.id.contenedorIngredientes);
        rvRecetas = findViewById(R.id.rvRecetasFiltradas);
        btnAtras = findViewById(R.id.btnAtrasRecomendador);

        rvRecetas.setLayoutManager(new LinearLayoutManager(this));
        btnAtras.setOnClickListener(v -> finish());

        cargarIngredientes();
    }

    private void cargarIngredientes() {
        new Thread(() -> {
            List<Ingrediente> listaIngredientes = db.nutriPetDao().obtenerTodosLosIngredientes();
            runOnUiThread(() -> {
                for (Ingrediente ing : listaIngredientes) {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(ing.getNombre());
                    cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        String nombreIng = ing.getNombre().toLowerCase();
                        if (isChecked) {
                            ingredientesSeleccionados.add(nombreIng);
                            verificarAlertaMedica(nombreIng);
                        } else {
                            ingredientesSeleccionados.remove(nombreIng);
                        }
                        filtrarRecetas();
                    });
                    contenedorIngredientes.addView(cb);
                }
            });
        }).start();
    }

    private void verificarAlertaMedica(String ingrediente) {
        new Thread(() -> {
            int prohibido = db.nutriPetDao().esIngredienteProhibidoParaMascota(microchipMascota, ingrediente);
            if (prohibido > 0) {
                String patologia = db.nutriPetDao().obtenerNombrePatologiaConflicto(microchipMascota, ingrediente);
                Log.w(TAG, "Alerta: Ingrediente prohibido seleccionado: " + ingrediente);
                runOnUiThread(() -> mostrarAlertaCritica("Alerta Nutricional", "El ingrediente '" + ingrediente + "' es peligroso para la patología: " + patologia));
            }
        }).start();
    }

    private void filtrarRecetas() {
        new Thread(() -> {
            List<Receta> todasLasRecetas = db.nutriPetDao().obtenerTodasLasRecetas();
            List<Receta> recetasAptas = new ArrayList<>();

            for (Receta receta : todasLasRecetas) {
                List<String> ings = db.nutriPetDao().obtenerIngredientesDeReceta(receta.getId_receta());
                // Lógica de filtrado: si la receta contiene algún ingrediente seleccionado
                if (ings != null) {
                    for (String ing : ings) {
                        if (ingredientesSeleccionados.contains(ing.toLowerCase().trim())) {
                            recetasAptas.add(receta);
                            break;
                        }
                    }
                }
            }
            Log.d(TAG, "Recetas encontradas tras filtrado: " + recetasAptas.size());
            runOnUiThread(() -> rvRecetas.setAdapter(new RecetaAdapter(recetasAptas, (r, e) -> asignarRecetaAMascota(r), true, false)));
        }).start();
    }

    private void asignarRecetaAMascota(Receta receta) {
        new Thread(() -> {
            db.nutriPetDao().asignarRecetaAMascota(new MascotaReceta(microchipMascota, receta.getId_receta()));
            runOnUiThread(() -> {
                Toast.makeText(this, "Receta '" + receta.getNombre_receta() + "' asignada", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void mostrarAlertaCritica(String titulo, String mensaje) {
        new AlertDialog.Builder(this).setTitle("⚠️ " + titulo).setMessage(mensaje)
                .setPositiveButton("COMPRENDO EL RIESGO", null).show()
                .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }
}