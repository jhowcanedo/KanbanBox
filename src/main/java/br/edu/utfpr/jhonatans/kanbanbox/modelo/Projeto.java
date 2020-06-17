package br.edu.utfpr.jhonatans.kanbanbox.modelo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "projeto")
public class Projeto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;


    public Projeto(String nome) {
        setNome(nome);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
