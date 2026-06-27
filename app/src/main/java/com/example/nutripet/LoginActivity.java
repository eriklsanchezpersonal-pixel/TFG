package com.example.nutripet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Actividad de inicio de sesión: gestiona la autenticación del usuario contra la base de datos
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_LOGIN";
    private EditText etCorreo, etContrasena;
    private Button btnIngresar;
    private TextView tvIrARegistro;
    private AppBaseDeDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializamos la base de datos local (Room)
        db = AppBaseDeDatos.getInstance(this);

        // Vinculamos componentes de la interfaz XML
        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);

        // Acción al pulsar el botón INGRESAR
        btnIngresar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            Log.d(TAG, "Intento de inicio de sesión para: " + correo);

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Trasladamos la consulta de Room a un hilo secundario para evitar bloquear la UI
            new Thread(() -> {
                try {
                    // Validamos las credenciales en la base de datos
                    final Duenio duenio = db.nutriPetDao().login(correo, contrasena);

                    // Volvemos al hilo de la interfaz (UI Thread) para actualizar la vista
                    runOnUiThread(() -> {
                        if (duenio != null) {
                            Log.d(TAG, "Inicio de sesión exitoso. Usuario ID: " + duenio.getId_dueno());
                            Toast.makeText(LoginActivity.this, "¡Bienvenido " + duenio.getNombre() + "!", Toast.LENGTH_SHORT).show();

                            // Guardamos el ID del usuario en SharedPreferences para persistencia de sesión
                            SharedPreferences prefs = getSharedPreferences("NutriPet_App_Prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("ID_USUARIO", duenio.getId_dueno());
                            editor.apply();

                            // Redirigir a la pantalla principal
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ID_DUENIO", duenio.getId_dueno());
                            startActivity(intent);

                            finish(); // Cerramos el login
                        } else {
                            Log.w(TAG, "Credenciales inválidas para: " + correo);
                            Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error durante la autenticación: " + e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Error de conexión con la Base de Datos", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // Acción para saltar a la pantalla de Registro
        tvIrARegistro.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a pantalla de registro");
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}