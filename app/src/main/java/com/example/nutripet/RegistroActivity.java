package com.example.nutripet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etCorreo, etContrasena;
    private Button btnRegistrar;
    private TextView tvVolverALogin;
    private AppBaseDeDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar la base de datos local
        db = AppBaseDeDatos.getInstance(this);

        // Vincular los componentes del XML con Java
        etNombre = findViewById(R.id.etRegNombre);
        etTelefono = findViewById(R.id.etRegTelefono);
        etCorreo = findViewById(R.id.etRegCorreo);
        etContrasena = findViewById(R.id.etRegContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrarUsuario);
        tvVolverALogin = findViewById(R.id.tvVolverALogin);

        // Acción al pulsar el botón "REGISTRARME"
        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            // Validaciones de formato en el hilo principal (Rápido)
            if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (correo.length() < 6) {
                etCorreo.setError("Correo demasiado corto");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.setError("Introduce un correo electrónico válido (ejemplo@gmail.com)");
                return;
            }

            if (!telefono.matches("^[0-9]{9,11}$")) {
                etTelefono.setError("El teléfono debe tener entre 9 y 11 números, sin letras");
                return;
            }

            if (contrasena.length() < 6 || !contrasena.matches(".*[a-zA-Z].*") || !contrasena.matches(".*[0-9].*")) {
                etContrasena.setError("La contraseña debe tener al menos 6 caracteres, incluyendo letras y números");
                return;
            }

            // Crear el objeto Duenio
            Duenio nuevoDuenio = new Duenio(nombre, correo, telefono, contrasena);

            // Ejecutar la inserción en un hilo secundario
            new Thread(() -> {
                try {
                    long idInsertado = db.nutriPetDao().registrarDueno(nuevoDuenio);

                    // Volvemos al hilo de la interfaz para mostrar mensajes visuales
                    runOnUiThread(() -> {
                        if (idInsertado > 0) {
                            Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                            finish(); // Regresa al Login
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (android.database.sqlite.SQLiteConstraintException e) {
                    // Esto se ejecuta SÓLO si el correo viola el 'unique = true' que pusimos
                    runOnUiThread(() -> {
                        etCorreo.setError("Este correo ya está en uso");
                        Toast.makeText(RegistroActivity.this, "El correo ya se encuentra registrado", Toast.LENGTH_LONG).show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroActivity.this, "Error interno del sistema", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // Acción para volver al Login sin registrar nada
        tvVolverALogin.setOnClickListener(v -> finish());
    }
}