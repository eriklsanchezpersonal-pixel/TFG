package com.example.nutripet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarioCitasActivity extends AppCompatActivity {

    private RecyclerView rvCitas;
    private AppBaseDeDatos db;
    private int idDueno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_citas);

        db = AppBaseDeDatos.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        idDueno = prefs.getInt("idDueno", -1);

        // ... (tu código de toolbar) ...

        // Configurar RecyclerView
        rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));

        // Configurar Calendario
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Formato: 2026-06-25 (ajusta esto al formato que guardas en tu BD)
            String fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
            filtrarPorFecha(fechaSeleccionada);
        });
    }
    private void filtrarPorFecha(String fecha) {
        new Thread(() -> {
            // OJO: Debes crear este método en tu NutriPetDao
            List<CitaMedica> filtradas = db.nutriPetDao().obtenerCitasPorFecha(fecha);
            runOnUiThread(() -> {
                if (filtradas.isEmpty()) {
                    Toast.makeText(this, "No hay citas este día", Toast.LENGTH_SHORT).show();
                }
                rvCitas.setAdapter(new CitaAdapter(filtradas));
            });
        }).start();
    }
    private void mostrarDialogoFiltroMascotas() {
        new Thread(() -> {
            List<Mascota> misMascotas = db.nutriPetDao().obtenerMascotasDeDueno(idDueno);
            android.util.Log.d("DEBUG_MASCOTAS", "Mascotas encontradas: " + misMascotas.size());
            String[] nombres = new String[misMascotas.size() + 1];
            nombres[0] = "Todas las mascotas";

            for(int i = 0; i < misMascotas.size(); i++) {
                nombres[i+1] = misMascotas.get(i).getNombre();
            }

            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Filtrar por mascota")
                        .setItems(nombres, (dialog, which) -> {
                            if (which == 0) {
                                cargarCitas();
                            } else {

                                String microchipSeleccionado = misMascotas.get(which - 1).getMicrochip();
                                filtrarPorMascota(microchipSeleccionado);
                            }
                        }).show();
            });
        }).start();
    }

    private void filtrarPorMascota(String microchip) {
        new Thread(() -> {
            List<CitaMedica> filtradas = db.nutriPetDao().obtenerCitasPorMascota(microchip);
            runOnUiThread(() -> {
                rvCitas.setAdapter(new CitaAdapter(filtradas));
            });
        }).start();
    }

    private void cargarCitas() {
        new Thread(() -> {
            List<CitaMedica> todas = db.nutriPetDao().obtenerTodasLasCitas();
            runOnUiThread(() -> {
                rvCitas.setAdapter(new CitaAdapter(todas));
            });
        }).start();
    }

    private void mostrarDialogoNuevaCita() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_cita, null);
        EditText etTitulo = dialogView.findViewById(R.id.etTituloCita);

        new AlertDialog.Builder(this)
                .setTitle("Nueva Cita Médica")
                .setView(dialogView)
                .setPositiveButton("Guardar", (d, w) -> {
                    String titulo = etTitulo.getText().toString();

                    // Validación básica
                    if(titulo.isEmpty()){
                        Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Guardamos en la base de datos en un hilo secundario
                    new Thread(() -> {
                        // Crea el objeto CitaMedica (ajusta los campos según tu clase)
                        CitaMedica nuevaCita = new CitaMedica();
                        nuevaCita.setTitulo(titulo);
                        // Aquí deberías añadir fecha y microchip, por ahora dejamos el título

                        db.nutriPetDao().insertarCita(nuevaCita);

                        // Refrescamos la lista en la pantalla principal
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Cita guardada correctamente", Toast.LENGTH_SHORT).show();
                            cargarCitas(); // Esto vuelve a consultar la BD y actualiza el RecyclerView
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}