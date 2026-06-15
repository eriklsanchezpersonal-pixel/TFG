package com.example.nutripet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView tvNombre, tvCorreo, tvTelefono; // 🌟 Añadido tvTelefono
    private Button btnCerrarSesion;
    private AppBaseDeDatos db;
    private int idDuenio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        db = AppBaseDeDatos.getInstance(this);
        idDuenio = getIntent().getIntExtra("ID_DUENIO", -1);

        tvNombre = findViewById(R.id.tvPerfilNombre);
        tvCorreo = findViewById(R.id.tvPerfilCorreo);
        tvTelefono = findViewById(R.id.tvPerfilTelefono);
        btnCerrarSesion = findViewById(R.id.btnPerfilCerrarSesion);

        // Cargar datos del dueño
        cargarDatosDuenio();

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarDatosDuenio() {
        new Thread(() -> {
            Duenio duenio = db.nutriPetDao().obtenerDuenioPorId(idDuenio);

            runOnUiThread(() -> {
                if (duenio != null) {
                    tvNombre.setText("Nombre: " + duenio.getNombre());
                    tvCorreo.setText("Correo: " + duenio.getEmail());
                    tvTelefono.setText("Teléfono: " + duenio.getTelefono());
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