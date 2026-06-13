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

//Esta clase inicializa la base de datos
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
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Executors.newSingleThreadExecutor().execute(() -> {
                NutriPetDao dao = INSTANCE.nutriPetDao();

                // =========================================================================
                // INICIALIZACIÓN DE PATOLOGÍAS (ID autogenerados de SQLite: 1, 2, 3, 4)
                // =========================================================================
                List<Patologia> patologiasFijas = new ArrayList<>();
                patologiasFijas.add(new Patologia("Obesidad", 25.0f, 8.0f, 0.8f));               // ID: 1
                patologiasFijas.add(new Patologia("Insuficiencia Renal", 12.0f, 15.0f, 0.3f));   // ID: 2
                patologiasFijas.add(new Patologia("Diabetes", 20.0f, 10.0f, 0.5f));              // ID: 3
                patologiasFijas.add(new Patologia("Sin Patologías", 99.0f, 99.0f, 99.0f));       // ID: 4

                dao.precargarPatologias(patologiasFijas);

                // =========================================================================
                // INICIALIZACIÓN DE INGREDIENTES (ID autogenerados de SQLite: 1 al 20)
                // =========================================================================
                List<Ingrediente> ingredientesFijos = new ArrayList<>();

                // --- PROTEÍNAS (ID: 1 al 7) ---
                ingredientesFijos.add(new Ingrediente("Pechuga de Pollo", 165.0f, 31.0f, 3.6f, 0.23f));          // ID: 1
                ingredientesFijos.add(new Ingrediente("Muslo de Pollo (sin piel)", 120.0f, 20.0f, 4.0f, 0.18f));  // ID: 2
                ingredientesFijos.add(new Ingrediente("Carne de Ternera magra", 250.0f, 26.0f, 15.0f, 0.22f));     // ID: 3
                ingredientesFijos.add(new Ingrediente("Lomo de Cerdo magro", 143.0f, 26.0f, 3.5f, 0.25f));         // ID: 4
                ingredientesFijos.add(new Ingrediente("Salmón Fresco", 208.0f, 20.0f, 13.0f, 0.24f));              // ID: 5
                ingredientesFijos.add(new Ingrediente("Merluza (Pescado Blanco)", 89.0f, 12.0f, 0.9f, 0.19f));     // ID: 6
                ingredientesFijos.add(new Ingrediente("Hígado de Pollo", 119.0f, 16.9f, 4.8f, 0.30f));             // ID: 7

                // --- CARBOHIDRATOS (ID: 8 al 12) ---
                ingredientesFijos.add(new Ingrediente("Arroz Blanco", 130.0f, 2.7f, 0.3f, 0.03f));                 // ID: 8
                ingredientesFijos.add(new Ingrediente("Arroz Integral", 111.0f, 2.6f, 0.9f, 0.08f));               // ID: 9
                ingredientesFijos.add(new Ingrediente("Patata Cocida (sin piel)", 86.0f, 1.6f, 0.1f, 0.06f));       // ID: 10
                ingredientesFijos.add(new Ingrediente("Boniato / Camote", 86.0f, 1.6f, 0.1f, 0.05f));              // ID: 11
                ingredientesFijos.add(new Ingrediente("Copos de Avena (cocidos)", 71.0f, 2.5f, 1.4f, 0.08f));      // ID: 12

                // --- VERDURAS Y FRUTAS (ID: 13 al 17) ---
                ingredientesFijos.add(new Ingrediente("Zanahoria", 41.0f, 0.9f, 0.2f, 0.04f));                     // ID: 13
                ingredientesFijos.add(new Ingrediente("Calabaza", 26.0f, 1.0f, 0.1f, 0.02f));                      // ID: 14
                ingredientesFijos.add(new Ingrediente("Calabacín", 17.0f, 1.2f, 0.3f, 0.03f));                     // ID: 15
                ingredientesFijos.add(new Ingrediente("Judías Verdes (Ejotes)", 31.0f, 1.8f, 0.2f, 0.04f));        // ID: 16
                ingredientesFijos.add(new Ingrediente("Manzana (sin semillas)", 52.0f, 0.3f, 0.2f, 0.01f));         // ID: 17

                // --- GRASAS Y COMPLEMENTOS (ID: 18 al 20) ---
                ingredientesFijos.add(new Ingrediente("Aceite de Salmón", 900.0f, 0.0f, 100.0f, 0.0f));            // ID: 18
                ingredientesFijos.add(new Ingrediente("Aceite de Oliva Virgen Extra", 884.0f, 0.0f, 100.0f, 0.0f)); // ID: 19
                ingredientesFijos.add(new Ingrediente("Huevo Entero (cocido)", 155.0f, 13.0f, 11.0f, 0.18f));      // ID: 20

                dao.precargarIngredientes(ingredientesFijos);

                // =========================================================================
                // ENLACE DE INGREDIENTES PROHIBIDOS POR ENFERMEDAD (Tabla: Ingre_Prohibido)
                // =========================================================================
                db.execSQL("INSERT INTO Ingre_Prohibido (id_ingrediente, id_patologia) VALUES (7, 1)");
                db.execSQL("INSERT INTO Ingre_Prohibido (id_ingrediente, id_patologia) VALUES (4, 2)");
                db.execSQL("INSERT INTO Ingre_Prohibido (id_ingrediente, id_patologia) VALUES (7, 2)");
                db.execSQL("INSERT INTO Ingre_Prohibido (id_ingrediente, id_patologia) VALUES (8, 3)");

                // =========================================================================
                // PRECARGA DE RECETAS BASE (Tabla: Receta)
                // =========================================================================
                // Recetas Originales (IDs: 1, 2, 3)
                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(1, 'Dieta Blanda de Pollo y Arroz', 'Hervir la pechuga de pollo y el arroz blanco sin sal ni aceites. Mezclar bien antes de servir.', 20, 'url_pollo_arroz')");

                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(2, 'Estofado Ligero de Ternera', 'Cocinar la carne de ternera magra troceada junto con dados de calabaza y zanahoria al vapor hasta ablandar.', 30, 'url_estofado_ternera')");

                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(3, 'Plato Renal de Merluza', 'Cocer el lomo de merluza junto a la patata y el calabacín (bajos en fósforo). Escurrir completamente el agua al finalizar.', 15, 'url_merluza_renal')");

                // --- NUEVAS RECETAS PARA OBESIDAD (Saciantes y bajas en calorías) ---
                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(4, 'Pollo Fitness con Calabacín', 'Hervir el muslo de pollo sin piel. Cocer al vapor abundante calabacín y judías verdes para aportar volumen y fibra saciante sin subir calorías.', 25, 'url_pollo_fitness')");

                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(5, 'Merluza Ligera con Calabaza', 'Hervir la merluza junto con la calabaza troceada. Añadir unas gotas de aceite de oliva al final para los ácidos grasos esenciales.', 15, 'url_merluza_calabaza')");

                // --- NUEVAS RECETAS PARA INSUFICIENCIA RENAL (Muy bajas en fósforo y sodio) ---
                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(6, 'Delicia de Pollo e Hidratos Renales', 'Cocer la pechuga de pollo y la patata (ambas hervidas con doble cambio de agua para reducir minerales). Mezclar con manzana rallada.', 25, 'url_pollo_renal')");

                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(7, 'Arroz con Huevo y Aceite de Salmón', 'Hervir el arroz blanco. Añadir el huevo cocido picado como proteína de alto valor biológico controlada y el aceite de salmón.', 20, 'url_arroz_huevo_renal')");

                // --- NUEVAS RECETAS PARA DIABETES (Índice glucémico lento y fibra) ---
                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(8, 'Ternera con Boniato y Judías', 'Cocinar la ternera magra. El boniato cocido y las judías verdes mantendrán la glucosa estable gracias a su absorción lenta.', 35, 'url_ternera_diabetes')");

                db.execSQL("INSERT INTO Receta (id_receta, nombre_receta, instrucciones, tiempo_preparacion, imagen_url) VALUES " +
                        "(9, 'Salmón con Avena y Zanahoria', 'Cocinar el salmón fresco a la plancha sin aceite. Mezclar con copos de avena previamente cocidos en agua y zanahoria al vapor.', 20, 'url_salmon_diabetes')");


                // =========================================================================
                // COMPOSICIÓN NUTRICIONAL DE CADA RECETA (Tabla puente: Receta_Ingredi)
                // =========================================================================
                // Receta 1 (Pollo y Arroz)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (1, 1, 150.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (1, 8, 100.0)");

                // Receta 2 (Estofado Ternera)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (2, 3, 120.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (2, 13, 40.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (2, 14, 50.0)");

                // Receta 3 (Plato Renal de Merluza)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (3, 6, 100.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (3, 10, 80.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (3, 15, 60.0)");

                // Receta 4 (Pollo Fitness - Obesidad): Muslo pollo (ID 2 -> 100g), Calabacín (ID 15 -> 120g), Judías (ID 16 -> 60g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (4, 2, 100.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (4, 15, 120.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (4, 16, 60.0)");

                // Receta 5 (Merluza Ligera - Obesidad): Merluza (ID 6 -> 120g), Calabaza (ID 14 -> 100g), Aceite Oliva (ID 19 -> 5g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (5, 6, 120.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (5, 14, 100.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (5, 19, 5.0)");

                // Receta 6 (Pollo Renal - Renal): Pechuga pollo (ID 1 -> 80g), Patata (ID 10 -> 120g), Manzana (ID 17 -> 50g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (6, 1, 80.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (6, 10, 120.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (6, 17, 50.0)");

                // Receta 7 (Arroz con Huevo - Renal): Arroz Blanco (ID 8 -> 130g), Huevo (ID 20 -> 50g), Aceite Salmón (ID 18 -> 8g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (7, 8, 130.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (7, 20, 50.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (7, 18, 8.0)");

                // Receta 8 (Ternera Diabetes - Diabetes): Ternera magra (ID 3 -> 120g), Boniato (ID 11 -> 80g), Judías (ID 16 -> 80g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (8, 3, 120.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (8, 11, 80.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (8, 16, 80.0)");

                // Receta 9 (Salmón Diabetes - Diabetes): Salmón (ID 5 -> 100g), Avena (ID 12 -> 80g), Zanahoria (ID 13 -> 60g)
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (9, 5, 100.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (9, 12, 80.0)");
                db.execSQL("INSERT INTO Receta_Ingredi (id_receta, id_ingrediente, cantidad_gramos) VALUES (9, 13, 60.0)");
            });
        }
    };
}