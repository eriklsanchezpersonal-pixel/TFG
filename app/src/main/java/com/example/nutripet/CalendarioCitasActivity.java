package com.example.nutripet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarioCitasActivity extends AppCompatActivity implements CitaAdapter.OnCitaClickListener {
    private RecyclerView rvCitas;
    private AppBaseDeDatos db;
    private int idDueno;
    private String fechaSeleccionadaActual;
    private FloatingActionButton fabNuevaCita;
    private CitaAdapter citaAdapter;
    private List<CitaMedica> listaCitas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_citas);

        idDueno = getIntent().getIntExtra("ID_DUENIO", -1);
        if (idDueno == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializaciones
        db = AppBaseDeDatos.getInstance(this);
        rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));
        fabNuevaCita = findViewById(R.id.fabNuevaCita);
        Spinner spinnerMascota = findViewById(R.id.spinnerMascota);

        // Inicializar el Adaptador UNA SOLA VEZ (con 'this' para el borrado)
        citaAdapter = new CitaAdapter(new ArrayList<>(), this);
        rvCitas.setAdapter(citaAdapter);

        // Toolbar y UI
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Citas veterinarias");
        toolbar.setNavigationOnClickListener(v -> finish());

        fabNuevaCita.setOnClickListener(v -> mostrarDialogoNuevaCita());

        cargarSpinnerMascotas(spinnerMascota);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fechaSeleccionadaActual = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            actualizarLista();
        });

        spinnerMascota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarLista();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Carga inicial
        fechaSeleccionadaActual = obtenerFechaHoy();
        // No llames a actualizarLista aquí si cargarSpinnerMascotas lo hará al setear el listener
    }
    private void cargarCitasPorFecha(String fecha) {
        new Thread(() -> {
            // Usamos la consulta simplificada
            List<CitaMedica> lista = db.nutriPetDao().obtenerCitasPorFecha(fecha);

            Log.d("DEBUG_BD", "Fecha buscada: " + fecha);
            Log.d("DEBUG_BD", "Citas encontradas: " + (lista != null ? lista.size() : "null"));

            runOnUiThread(() -> {
                if (lista != null && !lista.isEmpty()) {
                    citaAdapter.updateList(lista);
                    rvCitas.getAdapter().notifyDataSetChanged();
                    Log.d("DEBUG_UI", "Datos enviados al adaptador, cantidad: " + lista.size());                } else {
                    citaAdapter.updateList(new ArrayList<>());
                    Toast.makeText(CalendarioCitasActivity.this, "No hay citas este día", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    private void actualizarLista() {
        Spinner spinner = findViewById(R.id.spinnerMascota);
        // Verificamos que el spinner tenga un valor seleccionado
        if (spinner.getSelectedItem() == null) return;

        String mascotaSeleccionada = spinner.getSelectedItem().toString();

        new Thread(() -> {
            List<CitaMedica> lista;

            if (mascotaSeleccionada.equals("Todas las mascotas")) {
                // Buscamos todas las citas de la fecha, sin importar la mascota
                lista = db.nutriPetDao().obtenerCitasPorFecha(fechaSeleccionadaActual);
            } else {
                // Buscamos el objeto mascota por su nombre
                Mascota m = db.nutriPetDao().obtenerMascotaPorNombre(mascotaSeleccionada);

                // Si encontramos la mascota, filtramos por su microchip y la fecha
                if (m != null) {
                    lista = db.nutriPetDao().obtenerCitasPorFechaYMicrochip(fechaSeleccionadaActual, m.getMicrochip());
                } else {
                    // Si por algún error no existe, devolvemos lista vacía
                    lista = new java.util.ArrayList<>();
                }
            }

            // Actualizamos la UI en el hilo principal
            runOnUiThread(() -> {
                citaAdapter.updateList(lista);
            });
        }).start();
    }
    private String obtenerFechaHoy() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        return calendar.get(java.util.Calendar.DAY_OF_MONTH) + "/" +
                (calendar.get(java.util.Calendar.MONTH) + 1) + "/" +
                calendar.get(java.util.Calendar.YEAR);
    }
    private void cargarSpinnerMascotas(Spinner spinner) {
        new Thread(() -> {
            List<Mascota> misMascotas = db.nutriPetDao().obtenerMascotasDeDueno(idDueno);

            android.util.Log.d("DEBUG_MASCOTAS", "Mascotas encontradas para ID " + idDueno + ": " + misMascotas.size());

            List<String> nombres = new ArrayList<>();
            nombres.add("Todas las mascotas");
            for (Mascota m : misMascotas) {
                nombres.add(m.getNombre());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            });
        }).start();
    }
    private void filtrarPorFecha(String fecha) {
        new Thread(() -> {
            // Consultamos la BD
            List<CitaMedica> filtradas = db.nutriPetDao().obtenerCitasPorFecha(fecha);
            // Volvemos al hilo principal para actualizar la interfaz
            runOnUiThread(() -> {
                if (filtradas.isEmpty()) {
                    Toast.makeText(this, "No hay citas este día", Toast.LENGTH_SHORT).show();
                }
                // Actualizamos el adaptador con la nueva lista
                rvCitas.setAdapter(new CitaAdapter(filtradas));
            });
        }).start();
    }
    private void mostrarDialogoNuevaCita() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nueva_cita, null);
        EditText etTitulo = dialogView.findViewById(R.id.etTituloCita);
        Spinner spinnerMascotasDialogo = dialogView.findViewById(R.id.spinnerMascotas);
        Button btnFecha = dialogView.findViewById(R.id.btnSeleccionarFecha);
        Button btnHora = dialogView.findViewById(R.id.btnSeleccionarHora);

        final Calendar calendarTemporal = Calendar.getInstance();

        // Lógica botón FECHA con formato estricto
        btnFecha.setOnClickListener(v -> new DatePickerDialog(this, (view, y, m, d) -> {
            calendarTemporal.set(Calendar.YEAR, y);
            calendarTemporal.set(Calendar.MONTH, m);
            calendarTemporal.set(Calendar.DAY_OF_MONTH, d);
            // Formato con dos dígitos para el día y mes
            btnFecha.setText(String.format("%02d/%02d/%d", d, m + 1, y));
        }, calendarTemporal.get(Calendar.YEAR), calendarTemporal.get(Calendar.MONTH), calendarTemporal.get(Calendar.DAY_OF_MONTH)).show());

        // Lógica botón HORA
        btnHora.setOnClickListener(v -> new TimePickerDialog(this, (view, h, min) -> {
            calendarTemporal.set(Calendar.HOUR_OF_DAY, h);
            calendarTemporal.set(Calendar.MINUTE, min);
            btnHora.setText(String.format("%02d:%02d", h, min));
        }, calendarTemporal.get(Calendar.HOUR_OF_DAY), calendarTemporal.get(Calendar.MINUTE), true).show());

        new Thread(() -> {
            List<Mascota> misMascotas = db.nutriPetDao().obtenerMascotasDeDueno(idDueno);
            List<String> nombres = new ArrayList<>();
            for (Mascota m : misMascotas) nombres.add(m.getNombre());

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombres);
                spinnerMascotasDialogo.setAdapter(adapter);

                new AlertDialog.Builder(this)
                        .setTitle("Nueva Cita Médica")
                        .setView(dialogView)
                        .setPositiveButton("Guardar", (d, w) -> {
                            String titulo = etTitulo.getText().toString();
                            // Obtenemos la fecha del botón (ya formateada)
                            String fechaSeleccionada = btnFecha.getText().toString();

                            if (titulo.isEmpty() || spinnerMascotasDialogo.getSelectedItem() == null || fechaSeleccionada.isEmpty()) {
                                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            new Thread(() -> {
                                int pos = spinnerMascotasDialogo.getSelectedItemPosition();
                                String microchip = misMascotas.get(pos).getMicrochip();

                                CitaMedica nuevaCita = new CitaMedica();
                                nuevaCita.setTitulo(titulo);
                                nuevaCita.setMicrochipMascota(microchip);
                                nuevaCita.setFecha(fechaSeleccionada);

                                nuevaCita.setHora(btnHora.getText().toString());

                                db.nutriPetDao().insertarCita(nuevaCita);

                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Cita guardada", Toast.LENGTH_SHORT).show();
                                    cargarCitasPorFecha(fechaSeleccionada);
                                });
                            }).start();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
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


    @Override
    public void onBorrarClick(CitaMedica cita) {
        new Thread(() -> {
            //Borramos de la BD (Asegúrate de tener este @Delete en tu DAO)
            db.nutriPetDao().borrarCita(cita);

            // Recargamos la lista actualizada
            runOnUiThread(() -> {
                Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show();
                actualizarLista();
            });
        }).start();
    }
}