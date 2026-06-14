package com.example.nutripet;

import android.content.Intent;
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

            //Validamos las credenciales
            Duenio duenio = db.nutriPetDao().login(correo, contrasena);

            if (duenio != null) {
                Toast.makeText(this, "¡Bienvenido " + duenio.getNombre() + "!", Toast.LENGTH_SHORT).show();

                //Redirigir a la pantalla principal de mascotas
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //Pasamos el ID del dueño a la siguiente pantalla para saber de quién son los perros
                intent.putExtra("ID_DUENO", duenio.getId_dueno());
                startActivity(intent);
                finish(); //Cerramos el login para que si el usuario pulsa "atrás" no vuelva a pedir credenciales

            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        //Acción para saltar a la pantalla de Registro
        tvIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}