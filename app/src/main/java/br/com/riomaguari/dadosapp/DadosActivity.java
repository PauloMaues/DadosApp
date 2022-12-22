package br.com.riomaguari.dadosapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.ArrayList;

import br.com.riomaguari.dadosapp.R;

public class DadosActivity extends AppCompatActivity {

    private ArrayList<Dados> dadosList;

    ImageView ivChegou;
    ListView lvDados;

    String myLike;

    DadosAdapter dadosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados);

        lvDados = findViewById(R.id.lvDados);

        ivChegou = findViewById(R.id.ivIncluir);
        ivChegou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in;
                in = new Intent(DadosActivity.this, InputActivity.class);
                startActivity(in);
            }
        });

        mostrarDados();

        lvDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Dados selectedDado=dadosList.get(position);

                Intent in;
                in=new Intent(DadosActivity.this, InputActivity.class);
                in.putExtra("dados", selectedDado);
                startActivity(in);

            }
        });

    }

    private void mostrarDados() {

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(DadosActivity.this, new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                if (s.contains("Sem registros")) {
                    Toast.makeText(DadosActivity.this, "Não encontrei registros !", Toast.LENGTH_LONG).show();

                    lvDados = findViewById(R.id.lvDados);
                    lvDados.setAdapter(null);

                } else {
                    dadosList = new JsonConverter<Dados>().toArrayList(s, Dados.class);

                    if (dadosList != null) {
                        dadosAdapter = new DadosAdapter(DadosActivity.this, R.layout.layout_dados, dadosList );
                        lvDados = findViewById(R.id.lvDados);
                        lvDados.setAdapter(dadosAdapter);

                    } else {
                        Toast.makeText(DadosActivity.this, "Erro no php!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        myLike = "";

        if (Common.isLocal(this)) {
            taskRead.execute("http://192.168.1.244/ftperm/apps/CA/dados_consultar.php?like="+myLike);
        }
        else {
            if (Common.isOnline(this)){
                taskRead.execute("http://riomaguari.dyndns.org:8082/ftperm/apps/CA/dados_consultar.php?like="+myLike);
            }
            else {
                Toast.makeText(DadosActivity.this, "Sem conexão com a Internet!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cp, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                dadosAdapter.getFilter().filter(s);
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);

        return true;
    }

}
