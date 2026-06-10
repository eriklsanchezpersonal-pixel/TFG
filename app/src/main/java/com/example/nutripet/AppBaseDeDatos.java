package com.example.nutripet;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {
        Duenio.class,
        Mascota.class,
        Patologia.class,
        Ingrediente.class,
        Receta.class,
        MascotaPatologia.class,
        IngreProhibido.class,
        RecetaIngrediente.class
}, version = 1, exportSchema = false)
public abstract class AppBaseDeDatos extends RoomDatabase {

    private static AppBaseDeDatos INSTANCE;

    public abstract NutriPetDao nutriPetDao();

    public static AppBaseDeDatos getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppBaseDeDatos.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppBaseDeDatos.class,
                                    "nutripet_db"
                            )
                            .allowMainThreadQueries()
                            //  ESTA LÍNEA LE DICE A ROOM QUE META LOS DATOS AL CREAR LA BD
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Código interno que se ejecuta SOLO la primera vez que se instala la App
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Ejecutamos la inserción en un hilo secundario para que no bloquee la app al arrancar
            Executors.newSingleThreadExecutor().execute(() -> {
                NutriPetDao dao = INSTANCE.nutriPetDao();

                // 1. Preparamos las Patologías fijas de la App
                List<Patologia> patologiasFijas = new ArrayList<>();
                patologiasFijas.add(new Patologia("Insuficiencia Renal", 12.0f, 15.0f, 0.3f));
                patologiasFijas.add(new Patologia("Obesidad", 25.0f, 8.0f, 0.8f));
                patologiasFijas.add(new Patologia("Sin Patologías", 99.0f, 99.0f, 99.0f)); // Caso base

                dao.precargarPatologias(patologiasFijas);

                // 2. Preparamos los Ingredientes fijos de la App (Nutrientes por cada 100g)
                // campos: Nombre, Kcal, Proteína, Grasa, Fósforo
                List<Ingrediente> ingredientesFijos = new ArrayList<>();
                ingredientesFijos.add(new Ingrediente("Pechuga de Pollo", 165.0f, 31.0f, 3.6f, 0.2f));
                ingredientesFijos.add(new Ingrediente("Carne de Ternera", 250.0f, 26.0f, 15.0f, 0.2f));
                ingredientesFijos.add(new Ingrediente("Arroz Blanco", 130.0f, 2.7f, 0.3f, 0.03f));
                ingredientesFijos.add(new Ingrediente("Zanahoria", 41.0f, 0.9f, 0.2f, 0.04f));
                ingredientesFijos.add(new Ingrediente("Aceite de Salmón", 900.0f, 0.0f, 100.0f, 0.0f));

                dao.precargarIngredientes(ingredientesFijos);
            });
        }
    };
}