package com.example.nutripet;

import android.util.Log;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Entidad de Room que representa la tabla "citas_medicas" en la base de datos
@Entity(tableName = "citas_medicas")
public class CitaMedica {
    private static final String TAG = "DEBUG_CITA_MODEL";

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String microchipMascota;
    public String titulo;
    public String fecha;
    public String hora;

    // Constructor vacío requerido por Room para el mapeo de objetos
    public CitaMedica(){
    }

    // Constructor para crear nuevas citas, con log para rastrear la creación
    public CitaMedica(String microchipMascota, String titulo, String fecha, String hora) {
        this.microchipMascota = microchipMascota;
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        Log.d(TAG, "Nueva instancia de CitaMedica creada: " + titulo + " para mascota " + microchipMascota);
    }

    // Métodos Getter y Setter estándar para acceso a las propiedades
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMicrochipMascota() {
        return microchipMascota;
    }

    public void setMicrochipMascota(String microchipMascota) {
        this.microchipMascota = microchipMascota;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}