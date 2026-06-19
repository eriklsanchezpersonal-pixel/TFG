package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "Receta_Ingrediente",
        primaryKeys = {"id_receta", "id_ingrediente"},
        foreignKeys = {
                @ForeignKey(
                        entity = Receta.class,
                        parentColumns = "id_receta",
                        childColumns = "id_receta",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Ingrediente.class,
                        parentColumns = "id_ingrediente",
                        childColumns = "id_ingrediente",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"id_receta"}),
                @Index(value = {"id_ingrediente"})
        }
)

public class RecetaIngrediente {
    private int id_receta;
    private int id_ingrediente;
    private float cantidad_gramos;

    public RecetaIngrediente(int id_receta, int id_ingrediente, float cantidad_gramos) {
        this.id_receta = id_receta;
        this.id_ingrediente = id_ingrediente;
        this.cantidad_gramos = cantidad_gramos;
    }

    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }
    public int getId_ingrediente() { return id_ingrediente; }
    public void setId_ingrediente(int id_ingrediente) { this.id_ingrediente = id_ingrediente; }
    public float getCantidad_gramos() { return cantidad_gramos; }
    public void setCantidad_gramos(float cantidad_gramos) { this.cantidad_gramos = cantidad_gramos; }
}