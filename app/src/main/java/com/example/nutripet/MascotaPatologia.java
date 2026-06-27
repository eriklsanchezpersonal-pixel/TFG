package com.example.nutripet;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

// Entidad de unión (n:m) que relaciona una mascota con sus patologías diagnosticadas
@Entity(
        tableName = "Mascota_Patologia",
        primaryKeys = {"id_mascota", "id_patologia"},
        foreignKeys = {
                @ForeignKey(
                        entity = Mascota.class,
                        parentColumns = "microchip",
                        childColumns = "id_mascota",
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
                @Index(value = {"id_mascota"}),
                @Index(value = {"id_patologia"})
        }
)
public class MascotaPatologia {
    private static final String TAG = "DEBUG_MASCOTA_PATOLOGIA";

    @NonNull
    private String id_mascota;
    private int id_patologia;

    // Constructor que registra la relación, con log para rastrear la asociación creada
    public MascotaPatologia(@NonNull String id_mascota, int id_patologia) {
        this.id_mascota = id_mascota;
        this.id_patologia = id_patologia;
        Log.d(TAG, "Asociación creada: Mascota (Microchip: " + id_mascota +
                ") ahora vinculada a Patología ID: " + id_patologia);
    }

    @NonNull
    public String getId_mascota() { return id_mascota; }
    public void setId_mascota(@NonNull String id_mascota) { this.id_mascota = id_mascota; }

    public int getId_patologia() { return id_patologia; }
    public void setId_patologia(int id_patologia) { this.id_patologia = id_patologia; }
}