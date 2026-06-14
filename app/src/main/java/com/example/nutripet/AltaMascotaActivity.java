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

        // Inicializar la base de datos
        db = AppBaseDeDatos.getInstance(this);

        // Recuperar el ID del dueño enviado desde la actividad anterior
        idDueno = getIntent().getIntExtra("ID_DUENIO", -1);

        // Vincular componentes de la interfaz
        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFechaNacimiento = findViewById(R.id.etFechaNacimientoMascota);
        etPeso = findViewById(R.id.etPesoMascota);
        spNivelActividad = findViewById(R.id.spNivelActividad);
        spPatologias = findViewById(R.id.spPatologias);
        btnGuardar = findViewById(R.id.btnGuardarMascota);

        // Cargar los datos de los selectores (Spinners)
        configurarSpinners();

        // Configurar el evento del botón de registro
        btnGuardar.setOnClickListener(v -> guardarMascota());
    }

    private void configurarSpinners() {
        // 1. Configurar Spinner de Nivel de Actividad (Opciones fijas)
        List<String> opcionesActividad = new ArrayList<>();
        opcionesActividad.add("Bajo (Poco ejercicio / Senior)");
        opcionesActividad.add("Moderado (Paseos diarios normales)");
        opcionesActividad.add("Alto (Muy activo / Trabajo)");

        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesActividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivelActividad.setAdapter(adapterActividad);

        // 2. Configurar Spinner de Patologías cargando los datos desde Room en un hilo secundario
        new Thread(() -> {
            try {
                listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias();
                List<String> nombresPatologias = new ArrayList<>();

                for (Patologia p : listaPatologias) {
                    nombresPatologias.add(p.getNombre_patologia());
                }

                // Actualizar la interfaz gráfica desde el hilo principal
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

        // Validar que los campos de texto no estén vacíos
        if (microchip.isEmpty() || nombre.isEmpty() || fechaNac.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las patologías de la base de datos hayan terminado de cargar
        if (listaPatologias == null || listaPatologias.isEmpty()) {
            Toast.makeText(this, "Cargando catálogo de patologías. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }

        float pesoActual = Float.parseFloat(pesoStr);

        // Obtener el ID real de la patología seleccionada en el Spinner
        int posicionSeleccionada = spPatologias.getSelectedItemPosition();
        int idPatologiaDetectado = listaPatologias.get(posicionSeleccionada).getId_patologia();

        // 🌟 MENSAJE DE DIAGNÓSTICO: Te mostrará en pantalla qué IDs se procesan antes del fallo
        Toast.makeText(this, "DEBUG -> ID Dueño: " + idDueno + " | ID Patología: " + idPatologiaDetectado, Toast.LENGTH_LONG).show();

        // Crear la entidad Mascota con el ID del dueño
        Mascota nuevaMascota = new Mascota(
                microchip,
                nombre,
                fechaNac,
                pesoActual,
                nivelActividad,
                idDueno
        );

        // Operaciones de inserción en segundo plano
        new Thread(() -> {
            try {
                // 1. Insertar la mascota en la base de datos
                db.nutriPetDao().registrarMascota(nuevaMascota);

                // 2. Crear e insertar la relación en la tabla puente (MascotaPatologia)
                MascotaPatologia relacion = new MascotaPatologia(nuevaMascota.getMicrochip(), idPatologiaDetectado);
                db.nutriPetDao().insertarMascotaPatologia(relacion);

                // Volver al hilo principal para avisar al usuario y cerrar la pantalla
                runOnUiThread(() -> {
                    Toast.makeText(AltaMascotaActivity.this, "¡" + nombre + " registrada correctamente!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                // Si la clave foránea falla, aquí veremos el motivo exacto en pantalla
                runOnUiThread(() -> {
                    Toast.makeText(AltaMascotaActivity.this, "Error BD: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                e.printStackTrace();
            }
        }).start();
    }
}