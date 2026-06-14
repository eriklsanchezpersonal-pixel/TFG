package com.example.nutripet;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NutriPetDao {

    // --- OPERACIONES PARA EL DUEÑO (LOGIN Y REGISTRO) ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long registrarDueno(Duenio dueno);

    @Query("SELECT * FROM Duenio WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    Duenio login(String email, String contrasena);

    // --- OPERACIONES PARA LAS MASCOTAS ---
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

    // --- MÉTODOS INTERNOS (Para que la App precargue los datos al instalarse) ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void precargarPatologias(List<Patologia> patologias);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void precargarIngredientes(List<Ingrediente> ingredientes);

    @Query("SELECT * FROM Mascota WHERE id_dueno = :idDuenio")
    List<Mascota> obtenerMascotasPorDuenio(int idDuenio);

    //--- INSERTAR MASCOTA ---
    @Insert
    long registrarMascota(Mascota mascota);

    //--- INSERTAR RELACIÓN MASCOTA-PATOLOGÍA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarMascotaPatologia(MascotaPatologia mascotaPatologia);
}