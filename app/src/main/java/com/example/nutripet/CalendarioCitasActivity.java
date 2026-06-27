package com.example.nutripet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // Asegura el uso correcto del sistema de logs de Android
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

        Log.d("NUTRIPET_CITAS", "onCreate iniciado. Configurando vistas y capturando Intent...");

        idDueno = getIntent().getIntExtra("ID_DUENIO", -1);
        if (idDueno == -1) {
            Log.d("NUTRIPET_CITAS", "Error crítico: ID_DUENIO recibido es -1. Abortando Activity.");
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("NUTRIPET_CITAS", "Sesión validada correctamente para ID de dueño: " + idDueno);

        // Inicializaciones
        db = AppBaseDeDatos.getInstance(this);
        rvCitas = findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(this));
        fabNuevaCita = findViewById(R.id.fabNuevaCita);
        Spinner spinnerMascota = findViewById(R.id.spinnerMascota);

        // Inicializar el Adaptador UNA SOLA VEZ (con 'this' para el borrado)
        citaAdapter = new CitaAdapter(new ArrayList<>(), this);
        rvCitas.setAdapter(citaAdapter);
        Log.d("NUTRIPET_CITAS", "RecyclerView y CitaAdapter enlazados en el hilo principal.");

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
            Log.d("NUTRIPET_CITAS", "Cambio detectado en CalendarView. Nueva fecha seleccionada: " + fechaSeleccionadaActual);
            actualizarLista();
        });

        spinnerMascota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("NUTRIPET_CITAS", "Elemento seleccionado en spinnerMascota. Posición: " + position + ", Texto: " + parent.getItemAtPosition(position).toString());
                actualizarLista();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("NUTRIPET_CITAS", "Spinner mascota: nada seleccionado.");
            }
        });

        // Carga inicial
        fechaSeleccionadaActual = obtenerFechaHoy();
        Log.d("NUTRIPET_CITAS", "Fecha base de hoy inicializada por defecto: " + fechaSeleccionadaActual);
        // No llames a actualizarLista aquí si cargarSpinnerMascotas lo hará al setear el listener
    }
    private void cargarCitasPorFecha(String fecha) {
        new Thread(() -> {
            Log.d("NUTRIPET_CITAS", "Iniciando consulta asíncrona en cargarCitasPorFecha para: " + fecha);
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
                    Log.d("NUTRIPET_CITAS", "No se encontraron citas en la BD para la fecha: " + fecha + ". Limpiando adaptador.");
                    Toast.makeText(CalendarioCitasActivity.this, "No hay citas este día", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
    private void actualizarLista() {
        Spinner spinner = findViewById(R.id.spinnerMascota);
        // Verificamos que el spinner tenga un valor seleccionado
        if (spinner.getSelectedItem() == null) {
            Log.d("NUTRIPET_CITAS", "actualizarLista cancelado: El spinner no tiene selección actual.");
            return;
        }

        String mascotaSeleccionada = spinner.getSelectedItem().toString();
        Log.d("NUTRIPET_CITAS", "actualizarLista ejecutado. Mascota: " + mascotaSeleccionada + " | Fecha: " + fechaSeleccionadaActual);

        new Thread(() -> {
            List<CitaMedica> lista;

            if (mascotaSeleccionada.equals("Todas las mascotas")) {
                // Buscamos todas las citas de la fecha, sin importar la mascota
                Log.d("NUTRIPET_CITAS", "Buscando global de citas (todas las mascotas) para la fecha seleccionada.");
                lista = db.nutriPetDao().obtenerCitasPorFecha(fechaSeleccionadaActual);
            } else {
                // Buscamos el objeto mascota por su nombre
                Log.d("NUTRIPET_CITAS", "Buscando en BD mascota con nombre: " + mascotaSeleccionada);
                Mascota m = db.nutriPetDao().obtenerMascotaPorNombre(mascotaSeleccionada);

                // Si encontramos la mascota, filtramos por su microchip y la fecha
                if (m != null) {
                    Log.d("NUTRIPET_CITAS", "Mascota localizada. Microchip: " + m.getMicrochip() + ". Filtrando citas por fecha y microchip.");
                    lista = db.nutriPetDao().obtenerCitasPorFechaYMicrochip(fechaSeleccionadaActual, m.getMicrochip());
                } else {
                    // Si por algún error no existe, devolvemos lista vacía
                    Log.d("NUTRIPET_CITAS", "Error interno: La mascota seleccionada devolvió null de la BD.");
                    lista = new java.util.ArrayList<>();
                }
            }

            // Actualizamos la UI en el hilo principal
            runOnUiThread(() -> {
                Log.d("NUTRIPET_CITAS", "Volviendo al hilo de la UI. Pasando " + (lista != null ? lista.size() : 0) + " elementos al adapter.");
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
            Log.d("NUTRIPET_CITAS", "Hilo secundario iniciado: Consultando mascotas asociadas al dueño.");
            List<Mascota> misMascotas = db.nutriPetDao().obtenerMascotasDeDueno(idDueno);

            android.util.Log.d("DEBUG_MASCOTAS", "Mascotas encontradas para ID " + idDueno + ": " + misMascotas.size());

            List<String> nombres = new ArrayList<>();
            nombres.add("Todas las mascotas");
            for (Mascota m : misMascotas) {
                nombres.add(m.getNombre());
            }

            runOnUiThread(() -> {
                Log.d("NUTRIPET_CITAS", "Poblando adapter del spinner en la UI con " + nombres.size() + " opciones.");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            });
        }).start();
    }
    private void filtrarPorFecha(String fecha) {
        new Thread(() -> {
            Log.d("NUTRIPET_CITAS", "Ejecutando método de contingencia filtrarPorFecha para: " + fecha);
            // Consultamos la BD
            List<CitaMedica> filtradas = db.nutriPetDao().obtenerCitasPorFecha(fecha);
            // Volvemos al hilo principal para actualizar la interfaz
            runOnUiThread(() -> {
                if (filtradas.isEmpty()) {
                    Log.d("NUTRIPET_CITAS", "Filtrado alternativo: No se hallaron citas.");
                    Toast.makeText(this, "No hay citas este día", Toast.LENGTH_SHORT).show();
                }
                // Actualizamos el adaptador con la nueva lista
                Log.d("NUTRIPET_CITAS", "Re-enlazando nuevo adaptador con " + filtradas.size() + " citas.");
                rvCitas.setAdapter(new CitaAdapter(filtradas));
            });
        }).start();
    }
    private void mostrarDialogoNuevaCita() {
        Log.d("NUTRIPET_CITAS", "Invocando diálogo modal para creación de nueva cita.");
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
            String fechaEstricta = String.format("%02d/%02d/%d", d, m + 1, y);
            btnFecha.setText(fechaEstricta);
            Log.d("NUTRIPET_CITAS", "DatePicker diálogo: fecha capturada = " + fechaEstricta);
        }, calendarTemporal.get(Calendar.YEAR), calendarTemporal.get(Calendar.MONTH), calendarTemporal.get(Calendar.DAY_OF_MONTH)).show());

        // Lógica botón HORA
        btnHora.setOnClickListener(v -> new TimePickerDialog(this, (view, h, min) -> {
            calendarTemporal.set(Calendar.HOUR_OF_DAY, h);
            calendarTemporal.set(Calendar.MINUTE, min);
            String horaEstricta = String.format("%02d:%02d", h, min);
            btnHora.setText(horaEstricta);
            Log.d("NUTRIPET_CITAS", "TimePicker diálogo: hora capturada = " + horaEstricta);
        }, calendarTemporal.get(Calendar.HOUR_OF_DAY), calendarTemporal.get(Calendar.MINUTE), true).show());

        new Thread(() -> {
            Log.d("NUTRIPET_CITAS", "Cargando lista de mascotas para el diálogo de nueva cita de forma asíncrona...");
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

                            Log.d("NUTRIPET_CITAS", "Pulsado 'Guardar' en diálogo. Validando datos recibidos...");

                            if (titulo.isEmpty() || spinnerMascotasDialogo.getSelectedItem() == null || fechaSeleccionada.isEmpty()) {
                                Log.d("NUTRIPET_CITAS", "Fallo de validación: campos incompletos en el formulario de la cita.");
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

                                Log.d("NUTRIPET_CITAS", "Persistiendo nueva cita en BD. Mascota microchip: " + microchip + ", Fecha: " + fechaSeleccionada);
                                db.nutriPetDao().insertarCita(nuevaCita);

                                runOnUiThread(() -> {
                                    Log.d("NUTRIPET_CITAS", "Cita registrada con éxito. Actualizando UI...");
                                    Toast.makeText(this, "Cita guardada", Toast.LENGTH_SHORT).show();
                                    cargarCitasPorFecha(fechaSeleccionada);
                                });
                            }).start();
                        })
                        .setNegativeButton("Cancelar", (d, w) -> Log.d("NUTRIPET_CITAS", "Creación de cita cancelada por el usuario."))
                        .show();
            });
        }).start();
    }

    private void filtrarPorMascota(String microchip) {
        new Thread(() -> {
            Log.d("NUTRIPET_CITAS", "Filtrando citas asíncronas para el microchip: " + microchip);
            List<CitaMedica> filtradas = db.nutriPetDao().obtenerCitasPorMascota(microchip);
            runOnUiThread(() -> {
                rvCitas.setAdapter(new CitaAdapter(filtradas));
            });
        }).start();
    }

    private void cargarCitas() {
        new Thread(() -> {
            Log.d("NUTRIPET_CITAS", "Cargando el volcado completo de todas las citas del sistema...");
            List<CitaMedica> todas = db.nutriPetDao().obtenerTodasLasCitas();
            runOnUiThread(() -> {
                rvCitas.setAdapter(new CitaAdapter(todas));
            });
        }).start();
    }


    @Override
    public void onBorrarClick(CitaMedica cita) {
        Log.d("NUTRIPET_CITAS", "Evento onBorrarClick capturado para la cita ID: " + cita.getId() + " (" + cita.getTitulo() + ")");
        new Thread(() -> {
            //Borramos de la BD (Asegúrate de tener este @Delete en tu DAO)
            db.nutriPetDao().borrarCita(cita);
            Log.d("NUTRIPET_CITAS", "Cita borrada satisfactoriamente de la base de datos local.");

            // Recargamos la lista actualizada
            runOnUiThread(() -> {
                Log.d("NUTRIPET_CITAS", "Notificando eliminación al usuario e iniciando refresco de la lista.");
                Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show();
                actualizarLista();
            });
        }).start();
    }
}