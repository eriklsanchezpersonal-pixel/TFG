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

        //Inicializar la base de datos local
        db = AppBaseDeDatos.getInstance(this);

        //Vincular los componentes del XML con Java (Añadido el teléfono)
        etNombre = findViewById(R.id.etRegNombre);
        etTelefono = findViewById(R.id.etRegTelefono);
        etCorreo = findViewById(R.id.etRegCorreo);
        etContrasena = findViewById(R.id.etRegContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrarUsuario);
        tvVolverALogin = findViewById(R.id.tvVolverALogin);

        //Acción al pulsar el botón "REGISTRARME"
        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            //Validación de todos los campos, incluyendo teléfono
            if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            //Crear el objeto Duenio con los datos introducidos
            Duenio nuevoDuenio = new Duenio(nombre,correo,telefono,contrasena);

            try {
                //Insertar el usuario en la base de datos usando el DAO
                long idInsertado = db.nutriPetDao().registrarDueno(nuevoDuenio);

                if (idInsertado > 0) {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra esta pantalla y regresa automáticamente al Login
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "El correo ya se encuentra registrado", Toast.LENGTH_LONG).show();
            }
        });

        //Acción para volver al Login sin registrar nada
        tvVolverALogin.setOnClickListener(v -> finish());
    }
}