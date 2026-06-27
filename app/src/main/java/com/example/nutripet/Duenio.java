package com.example.nutripet;

import android.util.Log;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Entidad de Room que representa al usuario (Dueño) de la mascota
@Entity(
        tableName = "Duenio",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class Duenio {
    private static final String TAG = "DEBUG_DUENIO_MODEL";

    @PrimaryKey(autoGenerate = true)
    private int id_dueno;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    // Constructor utilizado para registrar un nuevo dueño, con log para rastrear la creación
    public Duenio(String nombre, String email, String telefono, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
        Log.d(TAG, "Instancia Duenio creada: " + nombre + " con email: " + email);
    }

    // Métodos Getter y Setter estándar
    public int getId_dueno() { return id_dueno; }
    public void setId_dueno(int id_dueno) { this.id_dueno = id_dueno; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}