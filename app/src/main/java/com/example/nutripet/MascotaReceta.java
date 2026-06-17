package com.example.nutripet;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

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
    @NonNull
    private String id_mascota;
    private int id_receta;

    public MascotaReceta(@NonNull String id_mascota, int id_receta) {
        this.id_mascota = id_mascota;
        this.id_receta = id_receta;
    }

    @NonNull
    public String getId_mascota() { return id_mascota; }
    public int getId_receta() { return id_receta; }
}