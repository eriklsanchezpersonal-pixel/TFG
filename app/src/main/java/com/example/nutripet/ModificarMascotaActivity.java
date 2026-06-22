package com.example.nutripet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ModificarMascotaActivity extends AppCompatActivity {

    // Variables que coinciden exactamente con tu XML
    private EditText etMicrochip, etNombre, etFecha, etPeso;
    private Spinner spActividad;
    private Button btnGuardar;
    private String microchipMascota;
    private AppBaseDeDatos db;
    private int idDuenioActual;

    private Button btnSeleccionarPatologias;
    private List<Patologia> listaPatologias;
    private List<Integer> idsPatologiasSeleccionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_alta_mascota);

        // 1. Vincular componentes
        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFecha = findViewById(R.id.etFechaNacimientoMascota);
        etPeso = findViewById(R.id.etPesoMascota);
        spActividad = findViewById(R.id.spNivelActividad);
        btnGuardar = findViewById(R.id.btnGuardarMascota);
        // Cambiar Título (si usas Toolbar con ID 'toolbarAlta' como en AltaMascota)
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarAlta);
        if (toolbar != null) {
            toolbar.setTitle("Modificar Mascota");
        }

        // Cambiar texto del botón
        btnGuardar.setText("MODIFICAR MASCOTA");

        // Configura el adaptador ANTES de cargar los datos
        configurarSpinners();

        // 3. El resto de tu código
        db = AppBaseDeDatos.getInstance(this);
        microchipMascota = getIntent().getStringExtra("MICROCHIP_MASCOTA");

        etMicrochip.setEnabled(false);
        etMicrochip.setText(microchipMascota);

        cargarDatosMascota();
        btnGuardar.setOnClickListener(v -> validarYGuardar());
        btnSeleccionarPatologias = findViewById(R.id.btnSeleccionarPatologias);
        btnSeleccionarPatologias.setOnClickListener(v -> mostrarDialogoPatologias());
        new Thread(() -> {
            listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias();
            // Obtener IDs de patologías de ESTA mascota (necesitas este método en tu DAO)
            List<Integer> idsActuales = db.nutriPetDao().obtenerIdsPatologiasDeMascota(microchipMascota);
            idsPatologiasSeleccionadas.addAll(idsActuales);
        }).start();
    }

    // Añade este método que ya tenías en tu AltaMascotaActivity
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

        // 1. Validaciones básicas (lo que ya tenías)
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

        // Ejecutar la actualización en un hilo secundario
        Mascota mActualizada = new Mascota(microchipMascota, nombre, fecha, peso, actividad, idDuenioActual);

        new Thread(() -> {
            // Actualizamos los datos principales
            db.nutriPetDao().actualizarMascota(mActualizada);

            // Actualizamos las patologías (borrar antiguas e insertar nuevas)
            db.nutriPetDao().borrarPatologiasDeMascota(microchipMascota);
            for (int idPat : idsPatologiasSeleccionadas) {
                db.nutriPetDao().insertarMascotaPatologia(new MascotaPatologia(microchipMascota, idPat));
            }

            // Volver a la pantalla anterior
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
            // Comprueba si el ID actual está en la lista de seleccionados
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