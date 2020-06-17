package br.edu.utfpr.jhonatans.kanbanbox.persistencia;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Card;
import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;

@Database(entities = {Projeto.class, Card.class}, version = 1)
public abstract class ProjetosDatabase extends RoomDatabase {

    public abstract ProjetoDao projetoDao();
    public abstract CardDao cardDao();

    private static ProjetosDatabase instance;

    public static ProjetosDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (ProjetosDatabase.class) {
                if (instance == null) {
                    Builder builder =  Room.databaseBuilder(context,
                            ProjetosDatabase.class,
                            "kanbanbox.db");

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    });

                    instance = (ProjetosDatabase) builder.build();
                }
            }
        }
        return instance;
    }
}