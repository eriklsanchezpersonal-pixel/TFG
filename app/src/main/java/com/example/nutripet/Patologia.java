package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Patologia")
public class Patologia {
    @PrimaryKey(autoGenerate = true)
    private int id_patologia;
    private String nombre_patologia;
    private float max_proteina;
    private float max_grasa;
    private float max_fosforo;

    public Patologia(String nombre_patologia, float max_proteina, float max_grasa, float max_fosforo) {
        this.nombre_patologia = nombre_patologia;
        this.max_proteina = max_proteina;
        this.max_grasa = max_grasa;
        this.max_fosforo = max_fosforo;
    }

    public int getId_patologia() { return id_patologia; }
    public void setId_patologia(int id_patologia) { this.id_patologia = id_patologia; }
    public String getNombre_patologia() { return nombre_patologia; }
    public void setNombre_patologia(String nombre_patologia) { this.nombre_patologia = nombre_patologia; }
    public float getMax_proteina() { return max_proteina; }
    public void setMax_proteina(float max_proteina) { this.max_proteina = max_proteina; }
    public float getMax_grasa() { return max_grasa; }
    public void setMax_grasa(float max_grasa) { this.max_grasa = max_grasa; }
    public float getMax_fosforo() { return max_fosforo; }
    public void setMax_fosforo(float max_fosforo) { this.max_fosforo = max_fosforo; }
}