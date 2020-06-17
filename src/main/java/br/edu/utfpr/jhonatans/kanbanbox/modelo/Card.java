package br.edu.utfpr.jhonatans.kanbanbox.modelo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "card",
        foreignKeys = @ForeignKey(entity = Projeto.class,
                parentColumns = "id",
                childColumns = "projetoId",
                onDelete = CASCADE))
public class Card {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;
    private String descricao;

    @ColumnInfo(index = true)
    private int projetoId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Card(@NonNull String nome) {
        this.nome = nome;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    public void setNome(@NonNull String nome) {
        this.nome = nome;
    }

    @Nullable
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(@Nullable String descricao) {
        this.descricao = descricao;
    }

    public int getProjetoId() {
        return projetoId;
    }

    public void setProjetoId(int projetoId) {
        this.projetoId = projetoId;
    }

    @Override
    public String toString() {return nome;}
}