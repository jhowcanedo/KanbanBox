package br.edu.utfpr.jhonatans.kanbanbox;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

import br.edu.utfpr.jhonatans.kanbanbox.modelo.Card;
import br.edu.utfpr.jhonatans.kanbanbox.modelo.Projeto;
import br.edu.utfpr.jhonatans.kanbanbox.persistencia.ProjetosDatabase;
import br.edu.utfpr.jhonatans.kanbanbox.utils.UtilsGUI;

public class ToDoActivity extends AppCompatActivity {

    private static final int REQUEST_NOVO_CARD = 1;
    private static final int REQUEST_ALTERAR_CARD = 2;
    private static final int VISUALIZAR = 3;
    private static final String ID = "ID";
    private static final String MODO = "MODO";

    private static final int NOVO = 1;
    private static final int ALTERAR = 2;

    private Projeto projeto;

    private int modo;

    private ListView listViewToDo;
    private ArrayAdapter<Card> listaAdapter;
    private List<Card> listaCard;

    public static void visualizar(Activity activity, int requestCode, Projeto projeto){
        Intent intent = new Intent(activity, ToDoActivity.class);

        intent.putExtra(MODO, VISUALIZAR);
        intent.putExtra(ID, projeto.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewToDo = findViewById(R.id.listViewToDo);

        listViewToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Card card = (Card) parent.getItemAtPosition(position);
                CardActivity.alterar(ToDoActivity.this, REQUEST_ALTERAR_CARD, card);
            }
        });

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO, VISUALIZAR);
        registerForContextMenu(listViewToDo);

        if(modo == VISUALIZAR){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    int id = bundle.getInt(ID);

                    ProjetosDatabase database = ProjetosDatabase.getDatabase(ToDoActivity.this);

                    projeto = database.projetoDao().queryForId(id);

                    setTitle(projeto.getNome());

                    listaCard = database.cardDao().queryAll(projeto.getId());
                    ToDoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listaAdapter = new ArrayAdapter<>(ToDoActivity.this,
                                    android.R.layout.simple_list_item_1, listaCard);

                            listViewToDo.setAdapter(listaAdapter);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Card card =  (Card) listViewToDo.getItemAtPosition(info.position);

        switch (item.getItemId()){
            case R.id.menuItemAlterar:
                CardActivity.alterar(this, REQUEST_ALTERAR_CARD, card);
                return true;

            case R.id.menuItemExcluir:
                excluirCard(card);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.novo_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemNovoCard:
                CardActivity.novo(this, REQUEST_NOVO_CARD);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();

            int idCard = bundle.getInt(CardActivity.ID_CARD);
            String nomeCard = bundle.getString(CardActivity.NOME);
            String descricaoCard = bundle.getString(CardActivity.DESCRICAO);

            final Card card;

            if(requestCode == REQUEST_NOVO_CARD){
                card = new Card("");

                listaCard.add(card);
            }else{
                card = getCard(idCard);
            }

            card.setNome(nomeCard);
            card.setDescricao(descricaoCard);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    ProjetosDatabase database = ProjetosDatabase.getDatabase(ToDoActivity.this);

                    if (modo == NOVO) {
                        int novoid =  (int) database.cardDao().insert(card);

                        card.setId(novoid);
                    }else{
                        database.cardDao().update(card);
                    }

                    if(card.getProjetoId() != projeto.getId()){
                        card.setProjetoId(projeto.getId());
                    }

                    if(card.getId() == 0){
                        database.cardDao().insert(card);
                    }else{
                        database.cardDao().update(card);
                    }
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.principal_context_menu, menu);
    }

    private void excluirCard(final Card card){
        String mensagem = "Deseja realmente apagar?" + "\n" + card.getNome();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                ProjetosDatabase database = ProjetosDatabase.getDatabase(ToDoActivity.this);

                                database.cardDao().delete(card);

                                ToDoActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listaAdapter.remove(card);
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

    private Card getCard(int idProjeto){

        for (Card c : listaCard){

            if (c.getId() == idProjeto){
                return c;
            }
        }

        return null;
    }
}