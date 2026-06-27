package com.example.nutripet;

import android.util.Log;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

// Entidad de unión (n:m) que define qué ingredientes están prohibidos para ciertas patologías
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
    private static final String TAG = "DEBUG_INGRE_PROHIBIDO";

    private int id_ingrediente;
    private int id_patologia;

    // Constructor que registra la relación, con log para rastrear la asociación entre ingrediente y patología
    public IngreProhibido(int id_ingrediente, int id_patologia) {
        this.id_ingrediente = id_ingrediente;
        this.id_patologia = id_patologia;
        Log.d(TAG, "Nueva relación creada: Ingrediente ID " + id_ingrediente +
                " marcado como prohibido para Patología ID " + id_patologia);
    }

    // Métodos Getter y Setter estándar
    public int getId_ingrediente() { return id_ingrediente; }
    public void setId_ingrediente(int id_ingrediente) { this.id_ingrediente = id_ingrediente; }

    public int getId_patologia() { return id_patologia; }
    public void setId_patologia(int id_patologia) { this.id_patologia = id_patologia; }
}