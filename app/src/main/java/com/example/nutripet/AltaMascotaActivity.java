package com.example.nutripet;

import android.os.Bundle;
import android.util.Log; // Importación necesaria para el uso del Logcat
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

/**
 * Activity que gestiona el formulario de alta y registro de mascotas.
 * Se encarga de la validación de campos, selección de patologías y persistencia en BD.
 */
public class AltaMascotaActivity extends AppCompatActivity {

    // Componentes de la interfaz de usuario (UI)
    private EditText etMicrochip, etNombre, etFechaNacimiento, etPeso;
    private Spinner spNivelActividad;
    private Button btnGuardar, btnSeleccionarPatologias;

    // Instancia de la base de datos Room y variables de control
    private AppBaseDeDatos db;
    private int idDueno; // Enlace clave foránea con la tabla Dueños
    private List<Patologia> listaPatologias; // Almacena el catálogo completo de patologías desde la BD
    private List<Integer> idsPatologiasSeleccionadas = new ArrayList<>(); // Registra las patologías elegidas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_alta_mascota);

        Log.d("NUTRIPET_ALTA", "onCreate ejecutado. Iniciando componentes visuales...");

        // Configuración de la barra de herramientas (Toolbar) superior
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarAlta);
        setSupportActionBar(toolbar);

        // Habilita el botón de navegación hacia atrás en la Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicialización de la base de datos (Patrón Singleton)
        db = AppBaseDeDatos.getInstance(this);

        // Recuperación del ID del dueño que mandó la Activity anterior (-1 por defecto si hay error)
        idDueno = getIntent().getIntExtra("ID_DUENIO", -1);
        Log.d("NUTRIPET_ALTA", "ID del dueño recibido del Intent: " + idDueno);

        // Vinculación de los objetos Java con los componentes del XML (Layout)
        etMicrochip = findViewById(R.id.etMicrochipMascota);
        etNombre = findViewById(R.id.etNombreMascota);
        etFechaNacimiento = findViewById(R.id.etFechaNacimientoMascota);

        // Bloqueamos el teclado en el campo fecha para obligar a usar el calendario gráfico
        etFechaNacimiento.setFocusable(false);
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        etPeso = findViewById(R.id.etPesoMascota);
        spNivelActividad = findViewById(R.id.spNivelActividad);
        btnGuardar = findViewById(R.id.btnGuardarMascota);
        btnSeleccionarPatologias = findViewById(R.id.btnSeleccionarPatologias);

        // Carga los datos del menú desplegable de actividad física
        configurarSpinners();

        // Configuración estética de textos
        toolbar = findViewById(R.id.toolbarAlta);
        toolbar.setTitle("Registrar Mascota");
        btnGuardar.setText("REGISTRAR MASCOTA");

        // Asignación de oyentes de eventos (Listeners) de los botones
        btnSeleccionarPatologias.setOnClickListener(v -> mostrarDialogoPatologias());
        btnGuardar.setOnClickListener(v -> guardarMascota());

        // HILO SECUNDARIO: Consulta asíncrona para no bloquear el hilo principal (UI Thread)
        // Carga preventivamente todas las patologías disponibles en el sistema
        new Thread(() -> {
            Log.d("NUTRIPET_ALTA", "Hilo asíncrono iniciado: Cargando lista global de patologías...");
            listaPatologias = db.nutriPetDao().obtenerTodasLasPatologias();
            Log.d("NUTRIPET_ALTA", "Patologías cargadas con éxito. Total encontradas: " + (listaPatologias != null ? listaPatologias.size() : 0));
        }).start();
    }

    /**
     * Despliega un calendario nativo de Android para seleccionar la fecha de nacimiento de forma controlada.
     */
    private void mostrarDatePicker() {
        Log.d("NUTRIPET_ALTA", "Se pulsó el campo de fecha. Desplegando DatePickerDialog...");
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Creación del diálogo del calendario
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Formatea la fecha seleccionada en formato DD/MM/AAAA asegurando dos dígitos
            String fechaFormateada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
            etFechaNacimiento.setText(fechaFormateada);
            Log.d("NUTRIPET_ALTA", "Fecha de nacimiento seleccionada y asignada: " + fechaFormateada);
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Inicializa y rellena el Spinner de niveles de actividad con opciones estáticas.
     */
    private void configurarSpinners() {
        Log.d("NUTRIPET_ALTA", "Configurando opciones estáticas para el Spinner de actividad...");
        List<String> opcionesActividad = new ArrayList<>();
        opcionesActividad.add("Bajo (Poco ejercicio / Senior)");
        opcionesActividad.add("Moderado (Paseos diarios normales)");
        opcionesActividad.add("Alto (Muy activo / Trabajo)");

        // Adaptador que conecta la lista de Strings con el diseño visual del Spinner
        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesActividad);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivelActividad.setAdapter(adapterActividad);
    }

    /**
     * Construye un cuadro de diálogo con casillas de selección múltiple basado en las patologías cargadas.
     */
    private void mostrarDialogoPatologias() {
        Log.d("NUTRIPET_ALTA", "Boton 'Seleccionar Patologías' pulsado.");
        // Control de concurrencia: Evita fallos si el usuario pulsa el botón antes de terminar la consulta del hilo
        if (listaPatologias == null || listaPatologias.isEmpty()) {
            Log.d("NUTRIPET_ALTA", "Advertencia: listaPatologias es nula o vacía en memoria todavía.");
            Toast.makeText(this, "Las patologías aún se están cargando, espera un segundo.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombres = new String[listaPatologias.size()];
        boolean[] seleccionados = new boolean[listaPatologias.size()];

        // Mapea la lista de objetos de la BD a vectores primitivos requeridos por el AlertDialog
        for (int i = 0; i < listaPatologias.size(); i++) {
            nombres[i] = listaPatologias.get(i).getNombre_patologia();
            // Mantiene marcadas las patologías si el usuario abre el diálogo por segunda vez
            seleccionados[i] = idsPatologiasSeleccionadas.contains(listaPatologias.get(i).getId_patologia());
        }

        // Construcción del componente de selección múltiple
        new AlertDialog.Builder(this)
                .setTitle("Selecciona patologías")
                .setMultiChoiceItems(nombres, seleccionados, (dialog, which, isChecked) -> {
                    int id = listaPatologias.get(which).getId_patologia();
                    String nombrePatologia = listaPatologias.get(which).getNombre_patologia();
                    if (isChecked) {
                        // Si se marca la casilla, se añade el ID a nuestra lista de seleccionados
                        if (!idsPatologiasSeleccionadas.contains(id)) {
                            idsPatologiasSeleccionadas.add(id);
                            Log.d("NUTRIPET_ALTA", "Patología seleccionada (Añadida): " + nombrePatologia + " (ID: " + id + ")");
                        }
                    } else {
                        // Si se desmarca, se elimina el ID mediante envoltura Integer para evitar confusión con índices
                        idsPatologiasSeleccionadas.remove(Integer.valueOf(id));
                        Log.d("NUTRIPET_ALTA", "Patología desmarcada (Eliminada): " + nombrePatologia + " (ID: " + id + ")");
                    }
                })
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // El botón aceptar simplemente cierra el cuadro conservando la lista actualizada
                    Log.d("NUTRIPET_ALTA", "Diálogo cerrado. Recuento actual de IDs seleccionados: " + idsPatologiasSeleccionadas.size());
                })
                .show();
    }

    /**
     * Recolecta, valida y persiste los datos del formulario de la mascota en la Base de Datos.
     */
    private void guardarMascota() {
        Log.d("NUTRIPET_ALTA", "Iniciando proceso de guardado del formulario...");
        // Recolección y limpieza de espacios en blanco
        String microchip = etMicrochip.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String nivelActividad = spNivelActividad.getSelectedItem().toString();

        Log.d("NUTRIPET_ALTA", "Datos recolectados -> Microchip: " + microchip + ", Nombre: " + nombre + ", Peso: " + pesoStr + ", Actividad: " + nivelActividad);

        // VALIDACIÓN 1: Asegura que no existan campos de texto vacíos
        if (microchip.isEmpty() || nombre.isEmpty() || fechaNac.isEmpty() || pesoStr.isEmpty()) {
            Log.d("NUTRIPET_ALTA", "Fallo de validación: Existen campos de texto vacíos.");
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // VALIDACIÓN 2: Regla de negocio específica (Obligatorio asignar salud/patología para cálculos de nutrición)
        if (idsPatologiasSeleccionadas.isEmpty()) {
            Log.d("NUTRIPET_ALTA", "Fallo de validación: No se ha seleccionado ninguna patología.");
            Toast.makeText(this, "Selecciona al menos una patología", Toast.LENGTH_SHORT).show();
            return;
        }

        float pesoActual = Float.parseFloat(pesoStr);

        // Instanciación del modelo Mascota listo para inserción
        Mascota nuevaMascota = new Mascota(microchip, nombre, fechaNac, pesoActual, nivelActividad, idDueno);

        // HILO SECUNDARIO: Ejecución transaccional de operaciones de escritura en base de datos
        new Thread(() -> {
            try {
                Log.d("NUTRIPET_ALTA", "Abriendo hilo secundario de persistencia para insertar mascota...");
                // 1. Registra a la mascota en su respectiva tabla
                db.nutriPetDao().registrarMascota(nuevaMascota);
                Log.d("NUTRIPET_ALTA", "Mascota persistida con éxito en la tabla 'Mascota'.");

                // 2. Alimenta la tabla asociativa (MascotaPatologia) resolviendo la relación N:M
                Log.d("NUTRIPET_ALTA", "Escribiendo relaciones N:M en MascotaPatologia para el microchip: " + microchip);
                for (int idPat : idsPatologiasSeleccionadas) {
                    db.nutriPetDao().insertarMascotaPatologia(new MascotaPatologia(microchip, idPat));
                }
                Log.d("NUTRIPET_ALTA", "Todas las relaciones de patología insertadas correctamente.");

                // Sincronización con el hilo principal para actualizar la interfaz visual tras el éxito
                runOnUiThread(() -> {
                    Log.d("NUTRIPET_ALTA", "Operación en BD terminada con éxito. Mostrando Toast y cerrando Activity.");
                    Toast.makeText(this, "¡" + nombre + " registrada!", Toast.LENGTH_SHORT).show();
                    finish(); // Destruye esta Activity y regresa a la anterior
                });
            } catch (Exception e) {
                // Gestión de excepciones en caso de microchips duplicados o fallos de constraints
                Log.d("NUTRIPET_ALTA", "ERROR FATAL durante la persistencia en BD: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    /**
     * Intercepta la pulsación de la flecha de regreso física o de la Toolbar para cerrar la pantalla de forma segura.
     */
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("NUTRIPET_ALTA", "Navegación 'Hacia Atrás' interceptada. Cerrando pantalla.");
        finish();
        return true;
    }
}