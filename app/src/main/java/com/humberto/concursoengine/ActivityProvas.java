package com.humberto.concursoengine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityProvas extends AppCompatActivity implements RVAdapterBaralhos.ClickListener{

    public List<Utils.Baralho> baralhos;
    ArrayList <String> arrtabelas;
    String dbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeBaralhosData();

         /* Começo da aplicação do recycler para expor os baralhos*/
        RecyclerView recList = (RecyclerView) findViewById(R.id.rv);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        RVAdapterBaralhos adapter = new RVAdapterBaralhos(baralhos,this);
        adapter.setOnItemClickListener(this);
        recList.setAdapter(adapter);
        /* Começo da aplicação do recycler*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initializeBaralhosData()  {

        baralhos = new ArrayList<>();

        // Capturar os dados da Activity anterior
        Bundle extras = getIntent().getExtras();
        dbName = extras.getString("dbName");
        System.out.println(dbName);
        
        Bitmap fb_circ_img = BitmapFactory.decodeResource(this.getResources(),R.drawable.student );
        Bitmap down64 = BitmapFactory.decodeResource(this.getResources(),R.drawable.downloads64x64 );

        InternalDatabaseHelper in = new InternalDatabaseHelper(getApplicationContext(), dbName);
        


        arrtabelas = in.getTablesNames();
        System.out.println(arrtabelas);
        for (int i=0;i<arrtabelas.size();i++){
            System.out.println(arrtabelas.get(i));
            baralhos.add(new Utils.Baralho(arrtabelas.get(i),
                    "2016/01",
                    fb_circ_img ,
                    "+"+String.valueOf(in.getCountOfRevQuestions(arrtabelas.get(i))),
                    "+"+String.valueOf(in.getCountOfNewQuestions(arrtabelas.get(i),10)))
            );
        }
    }


    @Override
    public void itemClicked(View v, int position){

        String tableName = arrtabelas.get(position);
        InternalDatabaseHelper in2 = new InternalDatabaseHelper(this, dbName);
        Intent intent = new Intent(this, ActivityQuestao.class);
        ArrayList<String[]> novas = in2.getArrayListOfNewQuestions(tableName, 10);
        ArrayList<String[]> revs = in2.getArrayListOfRevQuestions(tableName);

        if ( novas.size()==0 && revs.size()==0){
            Toast.makeText(this,"Este Baralho está vazio",Toast.LENGTH_LONG).show();
        }
        else {

            intent.putExtra("dbName", dbName);
            intent.putExtra("tableName", tableName);
            intent.putExtra("questionNovPos", 0);
            intent.putExtra("questionRevPos", 0);
            intent.putExtra("limit", 10);
            intent.putExtra("baralhopos", position);
            intent.putExtra("novas", novas);
            intent.putExtra("revisao", revs);

            startActivity(intent);
        }
    }
}
