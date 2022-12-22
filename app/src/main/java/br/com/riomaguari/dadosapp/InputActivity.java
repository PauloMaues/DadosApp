package br.com.riomaguari.dadosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.ArrayList;

import br.com.riomaguari.dadosapp.R;

public class InputActivity extends AppCompatActivity {

    private ArrayList<Dados> dadosList;
    private ArrayList<Dados> dadosEditar;

    TextView etId,etEndereco,etComplemento,etBairro,etCidade,etUf,etQtde,etHorario,etCaracateristicas;
    Button btIncluir,btGravar,btExcluir;

    ListView lvDados;
    DadosAdapter dadosAdapter;
    String myId,myEndereco,myComplemento,myBairro,myCidade,myUf,myQtde,myHorario,myCaracteristicas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input);

        setTitle("Informações - Meu próximo");

        etId = findViewById(R.id.etId);
        etEndereco = findViewById(R.id.etEndereco);
        etComplemento = findViewById(R.id.etComplemento);
        etBairro = findViewById(R.id.etBairro);
        etCidade = findViewById(R.id.etCidade);
        etUf = findViewById(R.id.etUf);
        etQtde = findViewById(R.id.etQtde);
        etHorario = findViewById(R.id.etHorario);
        etCaracateristicas = findViewById(R.id.etCaracteristicas);

        btIncluir = findViewById(R.id.btIncluir);
        btIncluir.setVisibility(View.GONE);

        btGravar = findViewById(R.id.btGravar);

        btExcluir = findViewById(R.id.btExcluir);
        btExcluir.setVisibility(View.GONE);

        etEndereco.requestFocus();

        //veio do listview
        if (getIntent().hasExtra("dados")) {

            final Dados dados=(Dados) getIntent().getSerializableExtra("dados");

            if (dados != null) {
                etId.setText(dados.id);
                myId=dados.id;

                etEndereco.setText(dados.endereco);
                etComplemento.setText(dados.complemento);
                etBairro.setText(dados.bairro);
                etCidade.setText(dados.cidade);
                etUf.setText(dados.uf);
                etQtde.setText(dados.qtde);
                etHorario.setText(dados.horario);
                etCaracateristicas.setText(dados.caracteristicas);

                btExcluir.setVisibility(View.VISIBLE);

            }
        }

        btIncluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limparDados();
            }
        });

        btGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gravarDados();
            }
        });

        btExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirDados();
            }
        });

    }

    //gravar
    private void limparDados() {
        etId.setText("");
        etEndereco.setText("");
        etComplemento.setText("");
        etBairro.setText("");
        etCidade.setText("");
        etUf.setText("");
        etQtde.setText("");
        etHorario.setText("");
        etCaracateristicas.setText("");

        btExcluir.setVisibility(View.INVISIBLE);

    }

    //gravar
    private void gravarDados() {

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(InputActivity.this, new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                if(s.contains("sucesso")){
                    Toast.makeText(getApplicationContext(), "Dados gravados com sucesso!", Toast.LENGTH_SHORT).show();

                    String[] myId = s.split("_");
                    etId.setText( myId[0] );

                    btExcluir.setVisibility(View.VISIBLE);
                    btIncluir.setVisibility(View.VISIBLE);

                }
                else if (s.contains("Erro")){
                    Toast.makeText(getApplicationContext(), "Erro ao gravar os dados no servidor!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Erro no PHP!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        myId = etId.getText().toString();

        myEndereco = etEndereco.getText().toString().replace(" ","_");
        if (TextUtils.isEmpty(myEndereco)) {
            etEndereco.setError("Favor informar o endereço!");
            return;
        }

        myComplemento = etComplemento.getText().toString().replace(" ","_");

        myBairro = etBairro.getText().toString().replace(" ","_");
        if (TextUtils.isEmpty(myBairro)) {
            etBairro.setError("Favor informar o bairro!");
            return;
        }

        myCidade = etCidade.getText().toString().replace(" ","_");
        if (TextUtils.isEmpty(myCidade)) {
            etCidade.setError("Favor informar a cidade!");
            return;
        }

        myUf = etUf.getText().toString();
        if (TextUtils.isEmpty(myUf)) {
            etUf.setError("Favor informar o Estado!");
            return;
        }

        myQtde = etQtde.getText().toString();
        if (TextUtils.isEmpty(myQtde)) {
            etQtde.setError("Favor informar a qtde de pessoas!");
            return;
        }

        myHorario = etHorario.getText().toString();
        if (TextUtils.isEmpty(myHorario)) {
            myHorario = "";
        }

        myCaracteristicas = etCaracateristicas.getText().toString().replace(" ","_");
        if (TextUtils.isEmpty(myCaracteristicas)) {
            myCaracteristicas = "";
        }

        if (myId.equals("")) {
            myId="0";
        }

        if (Common.isLocal(this)) {
            taskRead.execute("http://192.168.1.244/ftperm/apps/CA/dados_gravar.php?"
                    +"id="+myId
                    +"&endereco="+myEndereco
                    +"&complemento="+myComplemento
                    +"&bairro="+myBairro
                    +"&cidade="+myCidade
                    +"&uf="+myUf
                    +"&qtde="+myQtde
                    +"&horario="+myHorario
                    +"&caracteristicas="+myCaracteristicas
            );
        }
        else {
            if (Common.isOnline(this)){
                taskRead.execute("http://riomaguari.dyndns.org:8082/ftperm/apps/CA/dados_gravar.php?"
                        +"id="+myId
                        +"&endereco="+myEndereco
                        +"&complemento="+myComplemento
                        +"&bairro="+myBairro
                        +"&cidade="+myCidade
                        +"&uf="+myUf
                        +"&qtde="+myQtde
                        +"&horario="+myHorario
                        +"&caracteristicas="+myCaracteristicas);
            }
            else {
                Toast.makeText(InputActivity.this, "Sem conexão com a Internet!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //gravar
    private void excluirDados() {

        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(InputActivity.this, new AsyncResponse() {
            @Override
            public void processFinish(String s) {

                if(s.contains("sucesso")){
                    Toast.makeText(getApplicationContext(), "Dados excluídos com sucesso!", Toast.LENGTH_SHORT).show();
                    limparDados();
                }
                else if (s.contains("Erro")){
                    Toast.makeText(getApplicationContext(), "Erro ao excluir os dados no servidor!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Erro no PHP!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        myId = etId.getText().toString();

        if (myId.equals("")) {
            return;
        }

        if (Common.isLocal(this)) {
            taskRead.execute("http://192.168.1.244/ftperm/apps/CA/dados_excluir.php?id="+myId);
        }
        else {
            if (Common.isOnline(this)){
                taskRead.execute("http://riomaguari.dyndns.org:8082/ftperm/apps/CA/dados_excluir.php?id="+myId);
            }
            else {
                Toast.makeText(InputActivity.this, "Sem conexão com a Internet!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //volto para subareaactivity
        Intent in = new Intent(InputActivity.this, DadosActivity.class);
        startActivity(in);
    }

}

