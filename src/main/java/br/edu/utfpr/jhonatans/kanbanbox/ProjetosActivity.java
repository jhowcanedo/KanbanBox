package br.edu.utfpr.jhonatans.kanbanbox;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;
import br.edu.utfpr.jhonatans.kanbanbox.persistencia.ProjetosDatabase;
import br.edu.utfpr.jhonatans.kanbanbox.utils.UtilsGUI;

public class ProjetosActivity extends AppCompatActivity {

    private static final int REQUEST_NOVO_PROJETO    = 1;
    private static final int REQUEST_ALTERAR_PROJETO = 2;
    private static final int REQUEST_VISUALIZAR_CARD = 3;
    public static final String ID = "ID";

    private ListView listViewProjetos;
    private ArrayAdapter<Projeto> listAdapter;
    private List<Projeto> listaProjetos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);

        listViewProjetos = findViewById(R.id.listViewProjetos);



        listViewProjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Projeto projeto = (Projeto) parent.getItemAtPosition(position);
               ToDoActivity.visualizar(ProjetosActivity.this, REQUEST_VISUALIZAR_CARD, projeto);
           }
       });

        popularLista();
        registerForContextMenu(listViewProjetos);
    }

    //CRIA MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.novo_projeto, menu);
        return true;
    }

    //É CHAMADO QUANDO UMA OPÇÃO DO MENUITEM É CLICADO
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuItemNovo:
                ProjetoActivity.novo(this, REQUEST_NOVO_PROJETO);
                return true;

            case R.id.menuItemAbout:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;

            case R.id.menuItemNoturno:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //POPULA LISTVIEW
    private void popularLista(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ProjetosDatabase database = ProjetosDatabase.getDatabase(ProjetosActivity.this);

                listaProjetos = database.projetoDao().queryAll();

                ProjetosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter = new ArrayAdapter<>(ProjetosActivity.this,
                                                            android.R.layout.simple_list_item_1, listaProjetos);

                        listViewProjetos.setAdapter(listAdapter);
                    }
                });
            }
        });
    }

    //EXCLUIR PROJETO SELECIONADO
    private void excluirProjeto(final Projeto projeto){
        String mensagem = "Deseja realmente apagar?" + "\n" + projeto.getNome();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                ProjetosDatabase database = ProjetosDatabase.getDatabase(ProjetosActivity.this);

                                database.projetoDao().delete(projeto);

                                ProjetosActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listAdapter.remove(projeto);
                                    }
                                });

                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };
        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    //CRIA MENU DE CONTEXTO FLUTUANTE
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.principal_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Projeto projeto =  (Projeto) listViewProjetos.getItemAtPosition(info.position);

        switch (item.getItemId()){
            case R.id.menuItemAlterar:
                ProjetoActivity.alterar(this, REQUEST_ALTERAR_PROJETO, projeto);
                return true;

            case R.id.menuItemExcluir:
                excluirProjeto(projeto);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == REQUEST_NOVO_PROJETO || requestCode == REQUEST_ALTERAR_PROJETO)
                && resultCode == Activity.RESULT_OK){

            popularLista();
        }
    }



}
