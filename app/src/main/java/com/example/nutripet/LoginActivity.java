package com.example.nutripet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private Button btnIngresar;
    private TextView tvIrARegistro;
    private AppBaseDeDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializamos la base de datos
        db = AppBaseDeDatos.getInstance(this);

        //Vinculamos componentes de la interfaz XML
        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);

        //Acción al pulsar el botón INGRESAR
        btnIngresar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            //Trasladamos la consulta de Room a un hilo secundario
            new Thread(() -> {
                try {
                    // Validamos las credenciales de forma segura fuera del hilo principal
                    final Duenio duenio = db.nutriPetDao().login(correo, contrasena);

                    //Volvemos al hilo de la interfaz (UI Thread) para gestionar las respuestas y cambiar de pantalla
                    runOnUiThread(() -> {
                        if (duenio != null) {
                            Toast.makeText(LoginActivity.this, "¡Bienvenido " + duenio.getNombre() + "!", Toast.LENGTH_SHORT).show();

                            //Redirigir a la pantalla principal de mascotas
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            //Pasamos el ID del dueño verificado de forma segura
                            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("ID_USUARIO", duenio.getId_dueno());
                            editor.apply();

                            intent.putExtra("ID_DUENIO", duenio.getId_dueno());
                            startActivity(intent);

                            finish(); //Cerramos el login de forma definitiva
                        } else {
                            Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Error de conexión con la Base de Datos", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // Acción para saltar a la pantalla de Registro
        tvIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}