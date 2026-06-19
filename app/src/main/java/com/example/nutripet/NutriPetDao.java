package com.example.nutripet;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NutriPetDao {

    //OPERACIONES PARA EL DUEÑO (LOGIN Y REGISTRO)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long registrarDueno(Duenio dueno);

    @Query("SELECT * FROM Duenio WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    Duenio login(String email, String contrasena);

    //OPERACIONES PARA LAS MASCOTAS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarMascota(Mascota mascota);

    @Delete
    void eliminarMascota(Mascota mascota);

    @Query("SELECT * FROM Mascota WHERE id_dueno = :idDueno")
    List<Mascota> obtenerMascotasDeDueno(int idDueno);

    // --- CONSULTAS DEL CATÁLOGO FIJO (Para mostrarlos en la App) ---
    @Query("SELECT * FROM Patologia")
    List<Patologia> obtenerTodasLasPatologias();

    @Query("SELECT * FROM Ingrediente")
    List<Ingrediente> obtenerTodosLosIngredientes();
    @Query("SELECT * FROM Receta")
    List<Receta> obtenerTodasLasRecetas();
    @Query("SELECT COUNT(*) FROM Ingre_Prohibido " +
            "INNER JOIN Ingrediente ON Ingre_Prohibido.id_ingrediente = Ingrediente.id_ingrediente " +
            "INNER JOIN Patologia ON Ingre_Prohibido.id_patologia = Patologia.id_patologia " +
            "WHERE LOWER(Ingrediente.nombre) = LOWER(:nombreIngrediente) " +
            "AND LOWER(:patologiaMascota) LIKE '%' || LOWER(Patologia.nombre_patologia) || '%'")
    int esIngredienteProhibido(String nombreIngrediente, String patologiaMascota);

    @Query("SELECT nombre FROM Ingrediente INNER JOIN Receta_Ingrediente ON Ingrediente.id_ingrediente = Receta_Ingrediente.id_ingrediente WHERE Receta_Ingrediente.id_receta = :idReceta")
    List<String> obtenerIngredientesDeReceta(int idReceta);

    // --- MÉTODOS INTERNOS (Para que la App precargue los datos al instalarse) ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void precargarPatologias(List<Patologia> patologias);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void precargarIngredientes(List<Ingrediente> ingredientes);

    @Query("SELECT * FROM Mascota WHERE id_dueno = :idDuenio")
    List<Mascota> obtenerMascotasPorDuenio(int idDuenio);

    // INSERTAR MASCOTA
    @Insert
    long registrarMascota(Mascota mascota);

    // INSERTAR RELACIÓN MASCOTA-PATOLOGÍA
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarMascotaPatologia(MascotaPatologia mascotaPatologia);

    //Obtener informacion de un dueño
    @Query("SELECT * FROM Duenio WHERE id_dueno = :id")
    Duenio obtenerDuenioPorId(int id);

    //Sentencias para borrado de prueba
    @Query("DELETE FROM Duenio")
    void borrarTodosLosDuenios();

    @Query("DELETE FROM Mascota")
    void borrarTodasLasMascotas();

    //Obtener los datos básicos de una mascota usando su microchip
    @Query("SELECT * FROM Mascota WHERE microchip = :microchip")
    Mascota obtenerMascotaPorMicrochip(String microchip);

    //Obtener el nombre de la patología que tiene asignada una mascota
    @Query("SELECT P.nombre_patologia FROM Patologia P " +
            "INNER JOIN Mascota_Patologia MP ON P.id_patologia = MP.id_patologia " +
            "WHERE MP.id_mascota = :microchip LIMIT 1")
    String obtenerPatologiaDeMascota(String microchip);
    @Query("SELECT R.* FROM Receta R " +
            "INNER JOIN Mascota_Receta MR ON R.id_receta = MR.id_receta " +
            "WHERE MR.id_mascota = :microchip LIMIT 1")
    Receta obtenerRecetaDeMascota(String microchip);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void asignarRecetaAMascota(MascotaReceta mascotaReceta);

    @Query("SELECT R.* FROM Receta R " +
            "INNER JOIN Mascota_Receta MR ON R.id_receta = MR.id_receta " +
            "WHERE MR.id_mascota = :microchip")
    List<Receta> obtenerRecetasAsignadasAMascota(String microchip);

    @Query("SELECT * FROM Receta WHERE id_receta = :id")
    Receta obtenerRecetaPorId(int id);

}