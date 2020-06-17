package br.edu.utfpr.jhonatans.kanbanbox;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Card;
import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;
import br.edu.utfpr.jhonatans.kanbanbox.persistencia.ProjetosDatabase;
import br.edu.utfpr.jhonatans.kanbanbox.utils.UtilsGUI;

public class CardActivity extends AppCompatActivity {
    public static final String MODO    = "MODO";
    public static final String ID_CARD      = "ID_CARD";
    public static final String DESCRICAO = "DESCRICAO";
    public static final String NOME = "NOME";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private int modo;
    private int projeto;
    private int idProjeto;
    private int idCard;

    private EditText editTextNomeCard;
    private EditText editTextDescricao;

    private Card card;

    public static void novo(Activity activity, int requestCode){

        Intent intent = new Intent(activity, CardActivity.class);
        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Card card){

        Intent intent = new Intent(activity, CardActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID_CARD, card.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNomeCard   = findViewById(R.id.editTextCard);
        editTextDescricao = findViewById(R.id.editTextDescricao);


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, NOVO);

        if (modo == ALTERAR){

            setTitle(R.string.editar);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int idCard = bundle.getInt(ID_CARD);

                    ProjetosDatabase database = ProjetosDatabase.getDatabase(CardActivity.this);

                    card = database.cardDao().queryForId(idCard);

                    CardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            editTextNomeCard.setText(card.getNome());
                            editTextDescricao.setText(card.getDescricao());
                        }
                    });
                }
            });

        }else{
            setTitle(R.string.new_card);

            card = new Card("");
        }
    }

    private void salvar(){

        String nome  = UtilsGUI.validaCampoTexto(this,
                editTextNomeCard,
                R.string.nome_vazio);
        if (nome == null){
            return;
        }

        String descricao = UtilsGUI.validaCampoTexto(this, editTextDescricao, R.string.descricao_vazio);
        if(descricao == null){
            return;
        }

        Intent intent= new Intent();

        intent.putExtra(MODO, modo);
        intent.putExtra(ID_CARD, idCard);
        intent.putExtra(NOME, nome);
        intent.putExtra(DESCRICAO, descricao);

        setResult(Activity.RESULT_OK, intent);
        finish();
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
