package com.example.nutripet;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Entidad de Room que representa a una mascota asociada a un dueño
@Entity(
        tableName = "Mascota",
        foreignKeys = @ForeignKey(
                entity = Duenio.class,
                parentColumns = "id_dueno",
                childColumns = "id_dueno",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"id_dueno"})}
)
public class Mascota {
    private static final String TAG = "DEBUG_MASCOTA_MODEL";

    @PrimaryKey
    @NonNull
    private String microchip;
    private String nombre;
    private String fecha_nacimiento;
    private float peso_actual;
    private String nivel_actividad;
    private int id_dueno;

    // Campo ignorado por la base de datos, útil para mostrar información extra en la UI
    @Ignore
    private String nombrePatologia;

    // Constructor con log para rastrear la creación de instancias de Mascota
    public Mascota(@NonNull String microchip, String nombre, String fecha_nacimiento, float peso_actual, String nivel_actividad, int id_dueno) {
        this.microchip = microchip;
        this.nombre = nombre;
        this.fecha_nacimiento = fecha_nacimiento;
        this.peso_actual = peso_actual;
        this.nivel_actividad = nivel_actividad;
        this.id_dueno = id_dueno;
        Log.d(TAG, "Instancia Mascota creada: " + nombre + " con microchip: " + microchip);
    }

    // Métodos Getter y Setter estándar
    @NonNull
    public String getMicrochip() { return microchip; }
    public void setMicrochip(@NonNull String microchip) { this.microchip = microchip; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(String fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public float getPeso_actual() { return peso_actual; }
    public void setPeso_actual(float peso_actual) { this.peso_actual = peso_actual; }

    public String getNivel_actividad() { return nivel_actividad; }
    public void setNivel_actividad(String nivel_actividad) { this.nivel_actividad = nivel_actividad; }

    public int getId_dueno() { return id_dueno; }
    public void setId_dueno(int id_dueno) { this.id_dueno = id_dueno; }

    public String getNombrePatologia() { return nombrePatologia; }
    public void setNombrePatologia(String nombrePatologia) { this.nombrePatologia = nombrePatologia; }
}