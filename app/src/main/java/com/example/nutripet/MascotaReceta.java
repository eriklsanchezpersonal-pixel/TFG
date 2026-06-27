package com.example.nutripet;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

// Entidad de unión (n:m) que relaciona una mascota con una receta específica (dieta asignada)
@Entity(
        tableName = "Mascota_Receta",
        primaryKeys = {"id_mascota", "id_receta"},
        foreignKeys = {
                @ForeignKey(entity = Mascota.class,
                        parentColumns = "microchip",
                        childColumns = "id_mascota",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Receta.class,
                        parentColumns = "id_receta",
                        childColumns = "id_receta",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class MascotaReceta {
    private static final String TAG = "DEBUG_MASCOTA_RECETA";

    @NonNull
    private String id_mascota;
    private int id_receta;

    // Constructor que registra la relación, con log para rastrear la asignación de dieta
    public MascotaReceta(@NonNull String id_mascota, int id_receta) {
        this.id_mascota = id_mascota;
        this.id_receta = id_receta;
        Log.d(TAG, "Asignación de dieta creada: Mascota (Microchip: " + id_mascota +
                ") ahora tiene asignada la Receta ID: " + id_receta);
    }

    @NonNull
    public String getId_mascota() { return id_mascota; }

    public int getId_receta() { return id_receta; }
}