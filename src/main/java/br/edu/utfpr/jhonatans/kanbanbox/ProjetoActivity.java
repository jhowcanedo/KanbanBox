package br.edu.utfpr.jhonatans.kanbanbox;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Collections;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;
import br.edu.utfpr.jhonatans.kanbanbox.persistencia.ProjetosDatabase;
import br.edu.utfpr.jhonatans.kanbanbox.utils.UtilsGUI;

public class ProjetoActivity extends AppCompatActivity {
    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText editTextNome;

    private int    modo;
    private Projeto projeto;

    public static void novo(Activity activity, int requestCode){

        Intent intent = new Intent(activity, ProjetoActivity.class);
        intent.putExtra(MODO, NOVO);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Projeto projeto){

        Intent intent = new Intent(activity, ProjetoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, projeto.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projeto);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNome   = findViewById(R.id.editTextNome);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, NOVO);

        if (modo == ALTERAR){

            setTitle(R.string.editar);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    ProjetosDatabase database = ProjetosDatabase.getDatabase(ProjetoActivity.this);

                    projeto = database.projetoDao().queryForId(id);

                    ProjetoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextNome.setText(projeto.getNome());
                        }
                    });
                }
            });

        }else{

            setTitle(R.string.novo_projeto);

            projeto = new Projeto("");
        }
    }

    private void salvar(){

        String nome  = UtilsGUI.validaCampoTexto(this,
                editTextNome,
                R.string.nome_vazio);
        if (nome == null){
            return;
        }

        projeto.setNome(nome);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ProjetosDatabase database = ProjetosDatabase.getDatabase(ProjetoActivity.this);

                if (modo == NOVO) {

                    int novoId = (int) database.projetoDao().insert(projeto);
                    projeto.setId(novoId);

                }else{
                    database.projetoDao().update(projeto);
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
