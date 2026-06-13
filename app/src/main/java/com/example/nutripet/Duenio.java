package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Duenio")
public class Duenio {
    @PrimaryKey(autoGenerate = true)
    private int id_dueno;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    public Duenio(String nombre, String email, String telefono, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }


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