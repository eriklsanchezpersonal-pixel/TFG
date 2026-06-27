package com.example.nutripet;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Actividad para el registro de nuevos usuarios en el sistema
public class RegistroActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_REGISTRO";
    private EditText etNombre, etTelefono, etCorreo, etContrasena;
    private Button btnRegistrar;
    private TextView tvVolverALogin;
    private AppBaseDeDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = AppBaseDeDatos.getInstance(this);

        // Vincular componentes
        etNombre = findViewById(R.id.etRegNombre);
        etTelefono = findViewById(R.id.etRegTelefono);
        etCorreo = findViewById(R.id.etRegCorreo);
        etContrasena = findViewById(R.id.etRegContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrarUsuario);
        tvVolverALogin = findViewById(R.id.tvVolverALogin);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        tvVolverALogin.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        // Validaciones de formato
        if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Formato de correo inválido");
            return;
        }

        if (!telefono.matches("^[0-9]{9,11}$")) {
            etTelefono.setError("Teléfono inválido (9-11 dígitos)");
            return;
        }

        if (contrasena.length() < 6 || !contrasena.matches(".*[a-zA-Z].*") || !contrasena.matches(".*[0-9].*")) {
            etContrasena.setError("La contraseña debe tener letras y números");
            return;
        }

        Duenio nuevoDuenio = new Duenio(nombre, correo, telefono, contrasena);
        Log.d(TAG, "Iniciando proceso de registro para: " + correo);

        // Hilo secundario para base de datos
        new Thread(() -> {
            try {
                long idInsertado = db.nutriPetDao().registrarDueno(nuevoDuenio);

                runOnUiThread(() -> {
                    if (idInsertado > 0) {
                        Log.d(TAG, "Usuario registrado correctamente con ID: " + idInsertado);
                        Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.w(TAG, "Falló la inserción en BD (ID devuelto: " + idInsertado + ")");
                        Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (android.database.sqlite.SQLiteConstraintException e) {
                Log.e(TAG, "Error: El correo " + correo + " ya existe.");
                runOnUiThread(() -> {
                    etCorreo.setError("Este correo ya está en uso");
                    Toast.makeText(RegistroActivity.this, "El correo ya está registrado", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error crítico: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}