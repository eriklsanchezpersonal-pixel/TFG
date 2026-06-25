package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "citas_medicas")
public class CitaMedica {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String microchipMascota;
    public String titulo;
    public String fecha;
    public String hora;
    public CitaMedica(){
    }
    public CitaMedica(String microchipMascota, String titulo, String fecha, String hora) {
        this.microchipMascota = microchipMascota;
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
    }

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
