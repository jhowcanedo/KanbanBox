package br.edu.utfpr.jhonatans.kanbanbox.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;

@Dao
public interface ProjetoDao {

    @Insert
    long insert(Projeto projeto);

    @Delete
    void delete(Projeto projeto);

    @Update
    void update(Projeto projeto);

    @Query("SELECT * FROM projeto WHERE id = :id")
    Projeto queryForId(long id);

    @Query("SELECT * FROM projeto ORDER BY nome ASC")
    List<Projeto> queryAll();
}
