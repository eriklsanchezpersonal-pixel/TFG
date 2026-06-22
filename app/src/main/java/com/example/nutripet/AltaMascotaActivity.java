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

public class AltaMascotaActivity extends AppCompatActivity {

    private EditText etMicrochip, etNombre, etFechaNacimiento, etPeso;
    private Spinner spNivelActividad;
    private Button btnGuardar, btnSeleccionarPatologias;
    private AppBaseDeDatos db;
    private int idDueno;
    private List<Patologia> listaPatologias;
    private List<Integer> idsPatologiasSeleccionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_alta_mascota);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarAlta);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppBaseDeDatos.getInstance(this);
        idDueno = getIntent().getIntExtra("ID_DUENIO", -1);

        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFechaNacimiento = findViewById(R.id.etFechaNacimientoMascota);
        etPeso = findViewById(R.id.etPesoMascota);
        spNivelActividad = findViewById(R.id.spNivelActividad);
        btnGuardar = findViewById(R.id.btnGuardarMascota);
        btnSeleccionarPatologias = findViewById(R.id.btnSeleccionarPatologias);

        configurarSpinners();

        toolbar = findViewById(R.id.toolbarAlta);
        toolbar.setTitle("Registrar Mascota"); // Asegura que este sea el texto
        btnGuardar.setText("REGISTRAR MASCOTA");

        btnSeleccionarPatologias.setOnClickListener(v -> mostrarDialogoPatologias());
        btnGuardar.setOnClickListener(v -> guardarMascota());

        // Cargar patologías en segundo plano
        new Thread(() -> listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias()).start();
    }

    private void configurarSpinners() {
        List<String> opcionesActividad = new ArrayList<>();
        opcionesActividad.add("Bajo (Poco ejercicio / Senior)");
        opcionesActividad.add("Moderado (Paseos diarios normales)");
        opcionesActividad.add("Alto (Muy activo / Trabajo)");

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesActividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivelActividad.setAdapter(adapterActividad);
    }

    private void mostrarDialogoPatologias() {
        // Si la lista sigue vacía, no hace nada o avisa
        if (listaPatologias == null || listaPatologias.isEmpty()) {
            Toast.makeText(this, "Las patologías aún se están cargando, espera un segundo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombres = new String[listaPatologias.size()];
        boolean[] seleccionados = new boolean[listaPatologias.size()];

        for (int i = 0; i < listaPatologias.size(); i++) {
            nombres[i] = listaPatologias.get(i).getNombre_patologia();
            // Verificamos si el ID ya está en la lista de seleccionados
            seleccionados[i] = idsPatologiasSeleccionadas.contains(listaPatologias.get(i).getId_patologia());
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecciona patologías")
                .setMultiChoiceItems(nombres, seleccionados, (dialog, which, isChecked) -> {
                    int id = listaPatologias.get(which).getId_patologia();
                    if (isChecked) {
                        if (!idsPatologiasSeleccionadas.contains(id)) {
                            idsPatologiasSeleccionadas.add(id);
                        }
                    } else {
                        idsPatologiasSeleccionadas.remove(Integer.valueOf(id));
                    }
                })
                .setPositiveButton("Aceptar", (dialog, which) -> {
                })
                .show();
    }

    private void guardarMascota() {
        String microchip = etMicrochip.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String nivelActividad = spNivelActividad.getSelectedItem().toString();

        if (microchip.isEmpty() || nombre.isEmpty() || fechaNac.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idsPatologiasSeleccionadas.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos una patología", Toast.LENGTH_SHORT).show();
            return;
        }

        float pesoActual = Float.parseFloat(pesoStr);
        Mascota nuevaMascota = new Mascota(microchip, nombre, fechaNac, pesoActual, nivelActividad, idDueno);

        new Thread(() -> {
            try {
                db.nutriPetDao().registrarMascota(nuevaMascota);
                for (int idPat : idsPatologiasSeleccionadas) {
                    db.nutriPetDao().insertarMascotaPatologia(new MascotaPatologia(microchip, idPat));
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "¡" + nombre + " registrada!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}