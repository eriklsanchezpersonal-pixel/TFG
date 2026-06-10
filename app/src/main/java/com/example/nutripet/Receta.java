package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Receta")
public class Receta {
    @PrimaryKey(autoGenerate = true)
    private int id_receta;
    private String nombre_receta;
    private String instrucciones;
    private int tiempo_preparacion;
    private String imagen_url;

    public Receta(String nombre_receta, String instrucciones, int tiempo_preparacion, String imagen_url) {
        this.nombre_receta = nombre_receta;
        this.instrucciones = instrucciones;
        this.tiempo_preparacion = tiempo_preparacion;
        this.imagen_url = imagen_url;
    }

    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }
    public String getNombre_receta() { return nombre_receta; }
    public void setNombre_receta(String nombre_receta) { this.nombre_receta = nombre_receta; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public int getTiempo_preparacion() { return tiempo_preparacion; }
    public void setTiempo_preparacion(int tiempo_preparacion) { this.tiempo_preparacion = tiempo_preparacion; }
    public String getImagen_url() { return imagen_url; }
    public void setImagen_url(String imagen_url) { this.imagen_url = imagen_url; }
}