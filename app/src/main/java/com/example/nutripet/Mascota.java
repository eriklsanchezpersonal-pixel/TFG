package com.example.nutripet;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Mascota",
        foreignKeys = @ForeignKey(entity = Duenio.class,
                parentColumns = "id_dueno",
                childColumns = "id_dueno",
                onDelete = ForeignKey.CASCADE))
public class Mascota {
    @PrimaryKey
    @NonNull
    private String microchip;
    private String nombre;
    private String fecha_nacimiento;
    private float peso_actual;
    private String nivel_actividad;
    private int id_dueno;

    public Mascota(@NonNull String microchip, String nombre, String fecha_nacimiento, float peso_actual, String nivel_actividad, int id_dueno) {
        this.microchip = microchip;
        this.nombre = nombre;
        this.fecha_nacimiento = fecha_nacimiento;
        this.peso_actual = peso_actual;
        this.nivel_actividad = nivel_actividad;
        this.id_dueno = id_dueno;
    }

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
}