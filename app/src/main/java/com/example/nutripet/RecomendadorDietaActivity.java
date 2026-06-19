package com.example.nutripet;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
public class RecomendadorDietaActivity extends AppCompatActivity {

    private LinearLayout contenedorIngredientes;
    private RecyclerView rvRecetas;
    private FloatingActionButton btnAtras;
    private AppBaseDeDatos db;
    private String patologiaMascota;
    private String microchipMascota;
    private List<String> ingredientesSeleccionados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendar_dieta);

        db = AppBaseDeDatos.getInstance(this);

        // Recibir los datos
        patologiaMascota = getIntent().getStringExtra("PATOLOGIA_MASCOTA");
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");

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
                    cb.setTextSize(16);

                    //Listener en tiempo real cada vez que marcas/desmarcas un ingrediente
                    cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        String nombreIngrediente = ing.getNombre().toLowerCase();

                        if (isChecked) {
                            ingredientesSeleccionados.add(nombreIngrediente);
                            //COMPROBACIÓN MÉDICA VETERINARIA
                            verificarAlertaMedica(nombreIngrediente);
                        } else {
                            ingredientesSeleccionados.remove(nombreIngrediente);
                        }

                        //Filtrar recetas basándose en la lista actualizada
                        filtrarRecetas();
                    });

                    contenedorIngredientes.addView(cb);
                }
            });
        }).start();
    }

    private void verificarAlertaMedica(String ingrediente) {
        new Thread(() -> {
            // Le preguntamos a Room si existe un veto registrado para este ingrediente y esta patología
            int prohibido = db.nutriPetDao().esIngredienteProhibido(ingrediente, patologiaMascota);

            if (prohibido > 0) {
                runOnUiThread(() -> {
                    mostrarAlertaCritica("Alerta Nutricional Crítica",
                            "El ingrediente '" + ingrediente + "' está estrictamente contraindicado para animales con " + patologiaMascota + ".");
                });
            }
        }).start();
    }

    private void mostrarAlertaCritica(String titulo, String mensaje) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("⚠️ " + titulo)
                .setMessage(mensaje)
                .setPositiveButton("COMPRENDO EL RIESGO", null)
                .show();

        //Ponemos el botón en un color rojo de advertencia
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    private void filtrarRecetas() {
        new Thread(() -> {
            if (ingredientesSeleccionados.isEmpty()) {
                runOnUiThread(() -> rvRecetas.setAdapter(new RecetaAdapter(new ArrayList<>(), null)));
                return;
            }

            List<Receta> todasLasRecetas = db.nutriPetDao().obtenerTodasLasRecetas();
            List<Receta> recetasAptas = new ArrayList<>();

            for (Receta receta : todasLasRecetas) {
                List<String> ingredientesDeEstaReceta = db.nutriPetDao().obtenerIngredientesDeReceta(receta.getId_receta());
                boolean contieneAlMenosUno = false;
                if (ingredientesDeEstaReceta != null) {
                    for (String ing : ingredientesDeEstaReceta) {
                        if (ingredientesSeleccionados.contains(ing.toLowerCase().trim())) {
                            contieneAlMenosUno = true;
                            break;
                        }
                    }
                }
                if (contieneAlMenosUno) recetasAptas.add(receta);
            }

            // 🌟 ADAPTACIÓN PARA ASIGNACIÓN
            runOnUiThread(() -> {
                RecetaAdapter adapter = new RecetaAdapter(recetasAptas, recetaSeleccionada -> {
                    // Acción al hacer clic en una receta:
                    asignarRecetaAMascota(recetaSeleccionada);
                });
                rvRecetas.setAdapter(adapter);
            });
        }).start();
    }

    private void asignarRecetaAMascota(Receta receta) {
        new Thread(() -> {
            if (microchipMascota != null) {
                MascotaReceta mr = new MascotaReceta(microchipMascota, receta.getId_receta());
                db.nutriPetDao().asignarRecetaAMascota(mr);

                runOnUiThread(() -> {
                    Toast.makeText(this, "¡Receta '" + receta.getNombre_receta() + "' asignada!", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa al perfil de la mascota
                });
            }
        }).start();
    }
}