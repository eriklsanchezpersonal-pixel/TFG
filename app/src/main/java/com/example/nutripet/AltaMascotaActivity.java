package com.example.nutripet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AltaMascotaActivity extends AppCompatActivity {

    private EditText etMicrochip, etNombre, etFechaNacimiento, etPeso;
    private Spinner spNivelActividad, spPatologias;
    private Button btnGuardar;
    private AppBaseDeDatos db;
    private int idDueno;
    private List<Patologia> listaPatologias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_alta_mascota);

        db = AppBaseDeDatos.getInstance(this);

        //Recuperamos el ID del dueño
        idDueno = getIntent().getIntExtra("ID_DUENO", -1);

        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFechaNacimiento = findViewById(R.id.etFechaNacimientoMascota);
        etPeso = findViewById(R.id.etPesoMascota);
        spNivelActividad = findViewById(R.id.spNivelActividad);
        spPatologias = findViewById(R.id.spPatologias);
        btnGuardar = findViewById(R.id.btnGuardarMascota);

        configurarSpinners();

        btnGuardar.setOnClickListener(v -> guardarMascota());
    }

    private void configurarSpinners() {
        //Opciones estáticas del Nivel de Actividad (No requieren BD)
        List<String> opcionesActividad = new ArrayList<>();
        opcionesActividad.add("Bajo (Poco ejercicio / Senior)");
        opcionesActividad.add("Moderado (Paseos diarios normales)");
        opcionesActividad.add("Alto (Muy activo / Trabajo)");

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesActividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivelActividad.setAdapter(adapterActividad);

        //Mostramos opciones de Patologías
        new Thread(() -> {
            try {
                listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias();
                List<String> nombresPatologias = new ArrayList<>();
                for (Patologia p : listaPatologias) {
                    nombresPatologias.add(p.getNombre_patologia());
                }

                // Devolvemos el resultado al hilo principal para pintar el Spinner
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapterPatologias = new ArrayAdapter<>(AltaMascotaActivity.this,
                            android.R.layout.simple_spinner_item, nombresPatologias);
                    adapterPatologias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spPatologias.setAdapter(adapterPatologias);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void guardarMascota() {
        String microchip = etMicrochip.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String nivelActividad = spNivelActividad.getSelectedItem().toString();

        if (microchip.isEmpty() || nombre.isEmpty() || fechaNac.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaPatologias == null || listaPatologias.isEmpty()) {
            Toast.makeText(this, "Cargando patologías, espera un segundo...", Toast.LENGTH_SHORT).show();
            return;
        }

        float pesoActual = Float.parseFloat(pesoStr);

        Mascota nuevaMascota = new Mascota(
                microchip,
                nombre,
                fechaNac,
                pesoActual,
                nivelActividad,
                idDueno
        );

        //La inserción también debe ir en un hilo secundario
        new Thread(() -> {
            try {
                db.nutriPetDao().registrarMascota(nuevaMascota);

                int posicionSeleccionada = spPatologias.getSelectedItemPosition();
                int idPatologia = listaPatologias.get(posicionSeleccionada).getId_patologia();

                MascotaPatologia relacion = new MascotaPatologia(nuevaMascota.getMicrochip(), idPatologia);
                db.nutriPetDao().insertarMascotaPatologia(relacion);

                runOnUiThread(() -> {
                    Toast.makeText(AltaMascotaActivity.this, "¡" + nombre + " registrada correctamente!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(AltaMascotaActivity.this, "Error: El microchip ya existe o faltan datos", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        }).start();
    }
}