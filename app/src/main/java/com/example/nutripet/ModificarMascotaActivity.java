package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import java.util.Calendar;

// Actividad para modificar los datos de una mascota existente
public class ModificarMascotaActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_MOD_MASCOTA";
    private EditText etMicrochip, etNombre, etFecha, etPeso;
    private Spinner spActividad;
    private Button btnGuardar, btnSeleccionarPatologias;
    private String microchipMascota;
    private AppBaseDeDatos db;
    private int idDuenioActual;
    private List<Patologia> listaPatologias;
    private List<Integer> idsPatologiasSeleccionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_alta_mascota);
        Log.d(TAG, "onCreate: Iniciando actividad de modificación");

        // Vincular componentes
        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFecha = findViewById(R.id.etFechaNacimientoMascota);

        // Configurar DatePicker para fecha
        etFecha.setFocusable(false);
        etFecha.setClickable(true);
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        etPeso = findViewById(R.id.etPesoMascota);
        spActividad = findViewById(R.id.spNivelActividad);
        btnGuardar = findViewById(R.id.btnGuardarMascota);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarAlta);
        if (toolbar != null) {
            toolbar.setTitle("Modificar Mascota");
        }
        btnGuardar.setText("MODIFICAR MASCOTA");

        configurarSpinners();

        db = AppBaseDeDatos.getInstance(this);
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");
        Log.d(TAG, "Microchip recibido: " + microchipMascota);

        etMicrochip.setEnabled(false);
        etMicrochip.setText(microchipMascota);

        cargarDatosMascota();
        btnGuardar.setOnClickListener(v -> validarYGuardar());
        btnSeleccionarPatologias = findViewById(R.id.btnSeleccionarPatologias);
        btnSeleccionarPatologias.setOnClickListener(v -> mostrarDialogoPatologias());

        // Cargar datos iniciales de patologías
        new Thread(() -> {
            listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias();
            List<Integer> idsActuales = db.nutriPetDao().obtenerIdsPatologiasDeMascota(microchipMascota);
            idsPatologiasSeleccionadas.addAll(idsActuales);
            Log.d(TAG, "Patologías cargadas. IDs actuales: " + idsActuales.size());
        }).start();
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String fechaFormateada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
            etFecha.setText(fechaFormateada);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void configurarSpinners() {
        List<String> opcionesActividad = new ArrayList<>();
        opcionesActividad.add("Bajo (Poco ejercicio / Senior)");
        opcionesActividad.add("Moderado (Paseos diarios normales)");
        opcionesActividad.add("Alto (Muy activo / Trabajo)");

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesActividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActividad.setAdapter(adapterActividad);
    }

    private void cargarDatosMascota() {
        new Thread(() -> {
            Mascota m = db.nutriPetDao().obtenerMascotaPorMicrochip(microchipMascota);
            runOnUiThread(() -> {
                if (m != null) {
                    Log.d(TAG, "Cargando datos de mascota: " + m.getNombre());
                    etNombre.setText(m.getNombre());
                    etFecha.setText(m.getFecha_nacimiento());
                    etPeso.setText(String.valueOf(m.getPeso_actual()));
                    idDuenioActual = m.getId_dueno();
                    seleccionarSpinner(m.getNivel_actividad());
                }
            });
        }).start();
    }

    private void seleccionarSpinner(String nivelActividadGuardado) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spActividad.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(nivelActividadGuardado)) {
                spActividad.setSelection(i);
                break;
            }
        }
    }

    private void validarYGuardar() {
        String nombre = etNombre.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String actividad = spActividad.getSelectedItem().toString();

        if (nombre.isEmpty() || fecha.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fecha.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            etFecha.setError("Formato: dd/mm/aaaa");
            return;
        }

        float peso;
        try {
            peso = Float.parseFloat(pesoStr);
            if (peso <= 0) {
                etPeso.setError("El peso debe ser mayor a 0");
                return;
            }
        } catch (NumberFormatException e) {
            etPeso.setError("Introduce un peso válido");
            return;
        }

        Mascota mActualizada = new Mascota(microchipMascota, nombre, fecha, peso, actividad, idDuenioActual);

        new Thread(() -> {
            Log.d(TAG, "Guardando cambios en BD...");
            db.nutriPetDao().actualizarMascota(mActualizada);
            db.nutriPetDao().borrarPatologiasDeMascota(microchipMascota);
            for (int idPat : idsPatologiasSeleccionadas) {
                db.nutriPetDao().insertarMascotaPatologia(new MascotaPatologia(microchipMascota, idPat));
            }
            Log.d(TAG, "Cambios guardados con éxito");
            runOnUiThread(() -> {
                Toast.makeText(this, "Mascota y patologías actualizadas", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void mostrarDialogoPatologias() {
        if (listaPatologias == null || listaPatologias.isEmpty()) return;

        String[] nombres = new String[listaPatologias.size()];
        boolean[] seleccionados = new boolean[listaPatologias.size()];

        for (int i = 0; i < listaPatologias.size(); i++) {
            nombres[i] = listaPatologias.get(i).getNombre_patologia();
            seleccionados[i] = idsPatologiasSeleccionadas.contains(listaPatologias.get(i).getId_patologia());
        }

        new AlertDialog.Builder(this)
                .setTitle("Modificar patologías")
                .setMultiChoiceItems(nombres, seleccionados, (dialog, which, isChecked) -> {
                    int id = listaPatologias.get(which).getId_patologia();
                    if (isChecked) {
                        if (!idsPatologiasSeleccionadas.contains(id)) idsPatologiasSeleccionadas.add(id);
                    } else {
                        idsPatologiasSeleccionadas.remove(Integer.valueOf(id));
                    }
                })
                .setPositiveButton("Aceptar", null)
                .show();
    }
}