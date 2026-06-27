package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Actividad para mostrar la información del perfil del usuario y gestionar la sesión
public class PerfilUsuarioActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_PERFIL_USUARIO";
    private TextView tvNombre, tvCorreo, tvTelefono, tvMascotasContador;
    private Button btnCerrarSesion;
    private FloatingActionButton btnAtras;
    private AppBaseDeDatos db;
    private int idDuenio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        db = AppBaseDeDatos.getInstance(this);
        idDuenio = getIntent().getIntExtra("ID_DUENIO", -1);
        Log.d(TAG, "PerfilUsuarioActivity inicializado para ID_DUENIO: " + idDuenio);

        // Vincular componentes del layout
        btnAtras = findViewById(R.id.btnAtrasPerfil);
        tvNombre = findViewById(R.id.tvPerfilNombre);
        tvCorreo = findViewById(R.id.tvPerfilCorreo);
        tvTelefono = findViewById(R.id.tvPerfilTelefono);
        tvMascotasContador = findViewById(R.id.tvPerfilMascotasContador);
        btnCerrarSesion = findViewById(R.id.btnPerfilCerrarSesion);

        // Cargar datos del dueño
        cargarDatosDuenio();

        btnAtras.setOnClickListener(v -> finish());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    // Consulta en segundo plano para obtener datos del usuario y contar sus mascotas
    private void cargarDatosDuenio() {
        new Thread(() -> {
            Log.d(TAG, "Consultando datos de usuario y contador de mascotas...");
            Duenio duenio = db.nutriPetDao().obtenerDuenioPorId(idDuenio);
            int totalMascotas = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenio).size();

            runOnUiThread(() -> {
                if (duenio != null) {
                    Log.d(TAG, "Datos cargados para: " + duenio.getNombre());
                    tvNombre.setText(duenio.getNombre());
                    tvCorreo.setText("Correo: " + duenio.getEmail());
                    tvTelefono.setText("Teléfono: " + duenio.getTelefono());
                    tvMascotasContador.setText("Número de mascotas añadidas: " + totalMascotas);
                } else {
                    Log.e(TAG, "No se encontraron datos para el usuario con ID: " + idDuenio);
                }
            });
        }).start();
    }

    private void cerrarSesion() {
        Log.d(TAG, "Cerrando sesión de usuario");
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        // Limpiar el historial de navegación para que no se pueda volver al perfil tras salir
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}