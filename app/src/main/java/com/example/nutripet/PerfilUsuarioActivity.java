package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PerfilUsuarioActivity extends AppCompatActivity {

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

        btnAtras = findViewById(R.id.btnAtrasPerfil);
        tvNombre = findViewById(R.id.tvPerfilNombre);
        tvCorreo = findViewById(R.id.tvPerfilCorreo);
        tvTelefono = findViewById(R.id.tvPerfilTelefono);
        tvMascotasContador = findViewById(R.id.tvPerfilMascotasContador);
        btnCerrarSesion = findViewById(R.id.btnPerfilCerrarSesion);

        // Cargar datos del dueño
        cargarDatosDuenio();
        btnAtras.setOnClickListener(v -> {
            finish();
        });

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarDatosDuenio() {
        new Thread(() -> {
            Duenio duenio = db.nutriPetDao().obtenerDuenioPorId(idDuenio);
            int totalMascotas = db.nutriPetDao().obtenerMascotasPorDuenio(idDuenio).size();

            runOnUiThread(() -> {
                if (duenio != null) {
                    tvNombre.setText(duenio.getNombre());
                    tvCorreo.setText("Correo: " + duenio.getEmail());
                    tvTelefono.setText("Teléfono: " + duenio.getTelefono());
                    tvMascotasContador.setText("Número de mascotas añadidas: " + totalMascotas);

                }
            });
        }).start();
    }

    private void cerrarSesion() {
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}