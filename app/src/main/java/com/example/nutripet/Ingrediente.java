package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Ingrediente")
public class Ingrediente {
    @PrimaryKey(autoGenerate = true)
    private int id_ingrediente;
    private String nombre;
    private float kcal;
    private float gramos_proteina;
    private float gramos_grasas;
    private float gramos_fosforo;

    public Ingrediente(String nombre, float kcal, float gramos_proteina, float gramos_grasas, float gramos_fosforo) {
        this.nombre = nombre;
        this.kcal = kcal;
        this.gramos_proteina = gramos_proteina;
        this.gramos_grasas = gramos_grasas;
        this.gramos_fosforo = gramos_fosforo;
    }

    public int getId_ingrediente() { return id_ingrediente; }
    public void setId_ingrediente(int id_ingrediente) { this.id_ingrediente = id_ingrediente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public float getKcal() { return kcal; }
    public void setKcal(float kcal) { this.kcal = kcal; }
    public float getGramos_proteina() { return gramos_proteina; }
    public void setGramos_proteina(float gramos_proteina) { this.gramos_proteina = gramos_proteina; }
    public float getGramos_grasas() { return gramos_grasas; }
    public void setGramos_grasas(float gramos_grasas) { this.gramos_grasas = gramos_grasas; }
    public float getGramos_fosforo() { return gramos_fosforo; }
    public void setGramos_fosforo(float gramos_fosforo) { this.gramos_fosforo = gramos_fosforo; }
}