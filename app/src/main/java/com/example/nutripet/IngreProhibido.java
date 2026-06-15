package com.example.nutripet;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "Ingre_Prohibido",
        primaryKeys = {"id_ingrediente", "id_patologia"},
        foreignKeys = {
                @ForeignKey(
                        entity = Ingrediente.class,
                        parentColumns = "id_ingrediente",
                        childColumns = "id_ingrediente",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Patologia.class,
                        parentColumns = "id_patologia",
                        childColumns = "id_patologia",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"id_ingrediente"}),
                @Index(value = {"id_patologia"})
        }
)
public class IngreProhibido {
    private int id_ingrediente;
    private int id_patologia;

    public IngreProhibido(int id_ingrediente, int id_patologia) {
        this.id_ingrediente = id_ingrediente;
        this.id_patologia = id_patologia;
    }

    public int getId_ingrediente() { return id_ingrediente; }
    public void setId_ingrediente(int id_ingrediente) { this.id_ingrediente = id_ingrediente; }
    public int getId_patologia() { return id_patologia; }
    public void setId_patologia(int id_patologia) { this.id_patologia = id_patologia; }
}