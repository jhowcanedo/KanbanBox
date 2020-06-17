package br.edu.utfpr.jhonatans.kanbanbox.persistencia;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Card;

@Dao
public interface CardDao {

    @Insert
    long insert(Card card);

    @Delete
    void delete(Card card);

    @Update
    void update(Card card);

    @Query("SELECT * FROM card WHERE id = :id")
    Card queryForId(long id);

    @Query("SELECT * FROM card WHERE projetoId = :projetoId")
    List<Card> queryAll(int projetoId);
}
