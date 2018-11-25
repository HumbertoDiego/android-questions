package com.humberto.concursoengine;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class ActivityQuestao extends AppCompatActivity {

    // UI Elements
    InternalDatabaseHelper internaldbs;
    TextView contadorNov;
    TextView contadorRev;
    TextView questionTitle;
    TextView questionInterval;
    TextView questionNrComents;
    TextView question;
    TextView answer;
    Button dificil;
    Button medio;
    Button facil;
    Button showAnswer;
    Button cameraButton;
    Button sendButton;
    ProgressBar progressBar;
    RecyclerView rvComents;
    RVAdapterComents adapterComents;
    EditText comentario;
    View linlayout;
    ImageView comentFotoView;

    // Data elements
    Session session;
    File comentImageFile;
    boolean flagButtonCamera;
    int positionNov;
    int positionRev;
    int limit;
    int intervaloNew;
    String questionId;
    String pergunta;
    String resposta;
    String intervaloOld;
    String curtidas;
    String assunto;
    String date;
    String tableName;
    String dbName;
    SimpleDateFormat sdfsqlite = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfshow = new SimpleDateFormat("dd-MM-yyyy");
    Calendar c = Calendar.getInstance();
    ArrayList<String[]> novasQuestoes;
    ArrayList<String[]> revQuestoes;
    String requestComentsBaseURL = "http://humbertoalves.pythonanywhere.com/coments/";
    String downloadComentUserFotoBaseUrl = "http://humbertoalves.pythonanywhere.com/home4/download/";
    String downloadComentFotoBaseUrl = "http://humbertoalves.pythonanywhere.com/home4/downloaddb2/";
    String getALLcomentsKey = "all";
    String getONEcomentsKey = "one";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            dbName = extras.getString("dbName");
            tableName = extras.getString("tableName");
            positionNov = extras.getInt("questionNovPos");
            positionRev = extras.getInt("questionRevPos"); //The key argument here must match that used in the other activity
            limit = extras.getInt("limit");

            novasQuestoes = (ArrayList<String[]>) extras.getSerializable("novas");
            revQuestoes = (ArrayList<String[]>) extras.getSerializable("revisao");

        }

        internaldbs = new InternalDatabaseHelper(this, dbName);
        session =  new Session(this);

        linlayout =  findViewById(R.id.bottom_send_coment);
        linlayout.setFocusable(true);
        linlayout.setFocusableInTouchMode(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /* RecylerView  */
        rvComents = (RecyclerView) findViewById(R.id.rvcoments);
        rvComents.setNestedScrollingEnabled(false);
//        rvComents.setHasFixedSize(true); // Isso buga a rolagem ao adicionar novos itens "on the fly"
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvComents.setLayoutManager(llm);


        ///////////////////// Animação: ñ dá muito certo qd o conteúdo está escondido...
        adapterComents = new RVAdapterComents();
//        // Iniciando o animador do RcyclerView Coments (modo básico)
//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setAddDuration(1000);
//
        // Iniciando o animador do RcyclerView Coments (modo avançado) ver https://github.com/wasabeef/recyclerview-animators
        SlideInRightAnimator animator1 = new SlideInRightAnimator();
        animator1.setAddDuration(1000);

        rvComents.setItemAnimator(animator1);

        rvComents.setAdapter(adapterComents);

        contadorNov = (TextView) findViewById(R.id.contador_nov);
        contadorRev = (TextView) findViewById(R.id.contador_rev);
        questionTitle = (TextView) findViewById(R.id.question_title);
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);
        questionInterval = (TextView) findViewById(R.id.question_intervalo);
        questionNrComents = (TextView) findViewById(R.id.question_nrComents);

        contadorRev.setText(String.valueOf(positionRev) + "/" + revQuestoes.size());
        contadorNov.setText(String.valueOf(positionNov) + "/" + novasQuestoes.size());

        // Fazer as questoes da revisão
        if (positionNov > novasQuestoes.size()-1){
            questionId = revQuestoes.get(positionRev)[0]; // id
            pergunta = revQuestoes.get(positionRev)[1]; // pergunta
            resposta = revQuestoes.get(positionRev)[2]; // resposta
            intervaloOld = revQuestoes.get(positionRev)[3]; // intervalo
            curtidas = revQuestoes.get(positionRev)[4]; // curtidas
            assunto = revQuestoes.get(positionRev)[5]; // assunto

            try {
                if (revQuestoes.get(positionRev)[6]!=null) {
                    date = sdfshow.format(sdfsqlite.parse(revQuestoes.get(positionRev)[6])); // date vindo do sqlite yyyy-MM-dd
                }
            } catch (ParseException e) {
                date = revQuestoes.get(positionNov)[6];
                e.printStackTrace();
            }



            contadorRev.setText(String.valueOf(positionRev) + "/" + revQuestoes.size());
            contadorRev.setTypeface(null, Typeface.BOLD_ITALIC);
            questionTitle.setText(tableName+" - "+questionId);
            question.setText(pergunta);
            answer.setText(resposta);
            questionInterval.setText("Intervalo: " + intervaloOld+"d");
            questionNrComents.setText(date);

        }
        else { // Fazer as novas questoes
            questionId = novasQuestoes.get(positionNov)[0];// id
            pergunta = novasQuestoes.get(positionNov)[1]; // pergunta
            resposta = novasQuestoes.get(positionNov)[2]; // resposta
            intervaloOld = novasQuestoes.get(positionNov)[3]; // intervalo
            curtidas = novasQuestoes.get(positionNov)[4]; // curtidas
            assunto = novasQuestoes.get(positionNov)[5]; // assunto

            try {
                if (novasQuestoes.get(positionRev)[6]!=null) {
                    date = sdfshow.format(sdfsqlite.parse(novasQuestoes.get(positionRev)[6])); // date vindo do sqlite yyyy-MM-dd
                }
            } catch (ParseException e) {
                date = novasQuestoes.get(positionNov)[6];
                e.printStackTrace();
            }

            contadorNov.setText(String.valueOf(positionNov+1) + "/" + novasQuestoes.size());
            contadorNov.setTypeface(null, Typeface.BOLD_ITALIC);
            questionTitle.setText(tableName+" - "+questionId);
            question.setText(pergunta);
            answer.setText(resposta);
            questionInterval.setText("Intervalo: " + intervaloOld + "d");
            questionNrComents.setText(date);
        }

            showAnswer = (Button) findViewById(R.id.show_answer);
            showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer.setVisibility(Button.GONE);
                facil.setVisibility(Button.VISIBLE);
                medio.setVisibility(Button.VISIBLE);
                dificil.setVisibility(Button.VISIBLE);
                answer.setVisibility(TextView.VISIBLE);
            }
        });


        c.setTime(GregorianCalendar.getInstance().getTime()); // Seta a data de hoje no calendário
        dificil = (Button) findViewById(R.id.dificil);
        medio = (Button) findViewById(R.id.medio);
        facil = (Button) findViewById(R.id.facil);

        if(Integer.parseInt(intervaloOld)!=0){
            dificil.setText("DIFICIL (RESET)");
            medio.setText("MEDIO (x1.5)");
            facil.setText("FÁCIL (x2.5)");
        }

        dificil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervaloNew = internaldbs.updateQuestionInterval(tableName, questionId, Integer.parseInt(intervaloOld),"dificil");
                revQuestoes.add(new String[]{questionId,pergunta,resposta,String.valueOf(intervaloNew),curtidas,assunto,date});
                next();
            }
        });

        medio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervaloNew = internaldbs.updateQuestionInterval(tableName, questionId, Integer.parseInt(intervaloOld),"medio");
                next();
            }
        });

        facil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervaloNew = internaldbs.updateQuestionInterval(tableName, questionId, Integer.parseInt(intervaloOld),"facil");
                next();
            }
        });

        flagButtonCamera = true;

        comentFotoView = (ImageView) findViewById(R.id.send_coment_foto);

        cameraButton = (Button) findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flagButtonCamera) {
                    escolher_foto();
                }
                else{
//                    comentario.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                    comentFotoView.setImageResource(0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraButton.setForeground(null);
                    }
                    flagButtonCamera = true;
                }
            }
        });

        comentario = (EditText)findViewById(R.id.editText_comentario);
        sendButton = (Button) findViewById(R.id.button_send_comentario);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date comentDate = new Date(c.getTimeInMillis());
                String dateString = sdfshow.format(comentDate);

                String[] params = new String[]{session.getEmail(),dbName, tableName ,questionId, comentario.getText().toString(),dateString};

                SendComentTask mSend = new SendComentTask(params,comentImageFile);
                mSend.execute();

            }
        });

        // download dos comentários:
        GetComentTask mgetComent = new GetComentTask(this, "especific", dbName, tableName, questionId);
        mgetComent.execute();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ActivityMain.class));
    }


    public void next(){

        c.add(GregorianCalendar.DATE, intervaloNew); // soma o novo intervaloNew na data de hoje
        Date resultdate = new Date(c.getTimeInMillis()); //
        String resultdateString = sdfsqlite.format(resultdate); //
        internaldbs.updateQuestionDate(tableName, questionId, resultdateString );


        if ((positionNov+positionRev) >= (novasQuestoes.size()+revQuestoes.size()-1)){// acabar
            startActivity(new Intent(getApplicationContext(), ActivityMain.class));
            finish(); // destroi esta view para não mais ser acessada dando back
        }
        else  if (positionNov > novasQuestoes.size()-1){ // para de fazer novas questões e parte p revisoes
            positionRev++;
            Intent intent = new Intent(getApplicationContext(),  ActivityQuestao.class);
            intent.putExtra("dbName",dbName);
            intent.putExtra("tableName",tableName);
            intent.putExtra("questionNovPos", positionNov);
            intent.putExtra("questionRevPos", positionRev);
            intent.putExtra("limit", limit);
            intent.putExtra("novas",novasQuestoes);
            intent.putExtra("revisao", revQuestoes );
            startActivity(intent);
            finish();
            if ((positionNov+positionRev) > (novasQuestoes.size()+revQuestoes.size()-1)){
                startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                finish(); // destroi esta view para não mais ser acessada dando back
            }
        }else {// fazer novas questoes
            positionNov++;
            Intent intent = new Intent(getApplicationContext(),  ActivityQuestao.class);
            intent.putExtra("dbName",dbName);
            intent.putExtra("tableName",tableName);
            intent.putExtra("questionNovPos", positionNov);
            intent.putExtra("questionRevPos", positionRev);
            intent.putExtra("limit", limit);
            intent.putExtra("novas",novasQuestoes);
            intent.putExtra("revisao", revQuestoes );
            startActivity(intent);
            finish();
        }
    }



    ///////////////////// Métodos chamado do xml, ao clicar escolher foto ////////////////////////
    public void escolher_foto(){
        // checks if the app has permission at runtime(needed only once for write in app),
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Requests the permission if necessary:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        chooseFoto();
    }

    // Abre o seletor filtrado para fotos
    public void chooseFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    // Convert the data chosen to Bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {

            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // existem cameras q guardam a orientação da img em um metadado, deve-se capturar esse metadado se houver
                // e girar a img ainda como bitmap
                bitmap = Utils.girarBitmapEm(bitmap,Utils.getRotation(ActivityQuestao.this.getApplicationContext(),imageUri));

                // Dá uma diminuida na foto para evitar fotos muito largas
                int dimension = 610;
                if(bitmap.getWidth()>dimension){ // Reescalonar a img mantendo o aspect ratio
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension*bitmap.getHeight()/bitmap.getWidth());
                    comentImageFile = Utils.bitmaptoFile(bitmap);
                }
                putFoto(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //  Gera o thumbnail, corta em circulo e aplica ao ImageView
    public void putFoto(Bitmap bitmap){
        int dimension = 128;
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
        comentFotoView.setImageBitmap(bitmap);

        // Método para colocar drawable dentro dum editTExtView
//        Drawable d = new BitmapDrawable(getResources(), bitmap);
//        comentario.setCompoundDrawablesWithIntrinsicBounds(null,d,null,null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraButton.setForeground(getResources().getDrawable(android.R.drawable.btn_dialog,null));
        }
        flagButtonCamera = false;
    }


////////////////////////////  Async Tasks ////////////////////////////////////////////////////////////////
    // Entra em ctt com uma Restfull API de URL definida e recebe os resultados
    // AsyncTask<Params(Void), Progress(Void), Result(String)> =>NÃO recebe params do tipo String no execute(),
    // passa nada(void) para doInBackground
    // passa nada(void) para onProgressUpdate
    // Saída do doInBackground e Entrada de onPostExecute = result, é String
    private class SendComentTask extends AsyncTask<Void, Void, String> {

        String[] parametros;
        File comentImageFile2;

        public SendComentTask(String[] dados, File f) {
            this.parametros = dados; //{"user_email","database_name","table_name","question_id","comentario"};
            this.comentImageFile2 = f; // {"humberto-xingu@live.com","oab", "OABXXII" ,"2" ,"comentario"};

        }

    @Override
    protected void onPreExecute() {
//        comentario.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
//        comentImageFile.delete();

        comentario.setText("");
        comentImageFile = null;
        comentFotoView.setImageResource(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraButton.setForeground(null);
            flagButtonCamera = true;
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comentario.getWindowToken(), 0);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        rvComents.setVisibility(RecyclerView.GONE);
    }

    @Override
        protected String doInBackground(Void... params) {
            String charset = "UTF-8";
            MultipartUtility multipart = null;
            try {
                multipart = new MultipartUtility(requestComentsBaseURL, charset);
                multipart.addHeaderField("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
                multipart.addHeaderField("cache-control", "no-cache");
                multipart.addFormField("user_email", parametros[0]);
                multipart.addFormField("db_name", parametros[1]);
                multipart.addFormField("table_name", parametros[2]);
                multipart.addFormField("question_id", parametros[3]);
                multipart.addFormField("comentario", parametros[4]);
                multipart.addFormField("calendario", parametros[5]);
                multipart.addFormField("curtidas", "0");
                if(comentImageFile2!=null){
                    System.out.println("Sending comentImageFile");
                    multipart.addFilePart("file_insert", comentImageFile2);
                }

                String response = multipart.finish(); // response from server.

                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        protected void onPostExecute(String result) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }



// Recebe os comentários async Task
// AsyncTask<Params(Void), Progress(Void), Result(String)> =>NÃO recebe params do tipo String no execute(),
    // passa nada(void) para doInBackground
    // passa nada(void) para onProgressUpdate
    // Saída do doInBackground e Entrada de onPostExecute = result, é String

    private class GetComentTask extends AsyncTask<Void, Void, String> {

        private final List<Utils.Coment> coments2;
        private final Context ctx2;
        private final String url;

        public GetComentTask(Context ctx, String key, String dbName, String tableName, String questionId) {
            this.ctx2 = ctx;
            this.url = requestComentsBaseURL+dbName+"/"+tableName+"/"+questionId+"/"+key;
            this.coments2 =  new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            Bitmap student = BitmapFactory.decodeResource( ctx2.getResources(), R.drawable.student);
            JSONArray mjsonTop10Array = null;
            try {
                mjsonTop10Array = new JSONArray(getContent(url));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }

            if (mjsonTop10Array != null) {
                for (int i = 0; i < mjsonTop10Array.length(); i++) {
                    try {

                        boolean likedByThisUser = false;
                        JSONArray mjsonobj = new JSONArray(mjsonTop10Array.getJSONObject(i).getString("users_that_likeit"));
                        if (mjsonobj.length()!=0){

                            for (int k = 0; k < mjsonobj.length(); k++){
                                if(mjsonobj.getString(k).equals(session.getEmail())){
                                    likedByThisUser = true;
                                }
                            }
                        }
                        // Utils.Coment(String name, String ord, String comentario, int viewType , Bitmap userFoto,
                        // String fotoFilename, int curtidas,String date,String comentFotoFilename ,
                        // String dbName, String tableName,String questionId)
                        coments2.add(
                                new Utils.Coment(mjsonTop10Array.getJSONObject(i).getString("user_name"),
                                String.valueOf(i+1),
                                mjsonTop10Array.getJSONObject(i).getString("comentario"),
                                mjsonTop10Array.getJSONObject(i).getString("user_email").equals(session.getEmail()) ? 1 : 0,
                                student,
                                mjsonTop10Array.getJSONObject(i).getString("user_foto"),// None python objects retornam "null" e não null
                                Integer.parseInt(mjsonTop10Array.getJSONObject(i).getString("curtidas")),
                                mjsonTop10Array.getJSONObject(i).getString("calendario"),
                                mjsonTop10Array.getJSONObject(i).getString("imagefile"), // None python objects retornam "null" e não null
                                mjsonTop10Array.getJSONObject(i).getString("db_name"),
                                mjsonTop10Array.getJSONObject(i).getString("table_name"),
                                mjsonTop10Array.getJSONObject(i).getString("question_id"),
                                likedByThisUser,
                                session.getEmail(),
                                mjsonTop10Array.getJSONObject(i).getString("id"))
                        );
                    } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return "hello";
        }

        protected void onPostExecute(String result) {

            rvComents.setVisibility(RecyclerView.VISIBLE);
            progressBar.setVisibility(ProgressBar.GONE);

            // Começa fazer os downloads das fotos das pessoas no rank
            for(int i=0;i<coments2.size();i++){
                if (coments2.get(i).getUserFotoFilename() != "null"){
                    getUserComentFotoAsyncTask mgetuserfotosasync = new getUserComentFotoAsyncTask(coments2, i);
                    mgetuserfotosasync.execute((Void)null);
                }
            }

        }

        // Entra no conexão http em uma REst API para receber dados
        private String getContent(String myurl) throws IOException {
            InputStream is = null;
            int length = 500000; // tamanho do texto q se espera receber

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                int response = conn.getResponseCode();
                Log.d("TA", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = convertInputStreamToString(is, length);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Convert the InputStream into a string
        public String convertInputStreamToString(InputStream stream, int length) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return new String(buffer);
        }


    }


///////////////////////// Executado várias vezes, atualiza as fotos dos usuarios nos comentarios /////////////////////////////
    public class getUserComentFotoAsyncTask extends AsyncTask<Void, Void, String> {

        Bitmap bitmap;
        private final List<Utils.Coment> coments3;
        int position;

        // Usar o construtor é uma forma dos parametros entrarem nessa subclasse
        getUserComentFotoAsyncTask( List<Utils.Coment> coments, int pos) {
            // keys que esta api reotrna: "nivel", "foto", "first_name","xp", "estado", "RJ", "email"
            this.coments3 = coments;
            this.position = pos;
        }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    // Começa o envio da solicitação das fotos, as recebe e envia para onPostExecute
        @Override
        protected String doInBackground(Void... params) {

            InputStream in = null;

            try {
                //URL().openStream() = URL().openConnection().getInputStream()
                in = new URL(downloadComentUserFotoBaseUrl +coments3.get(position).getUserFotoFilename()).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(in);

            coments3.get(position).setUserFoto(bitmap);

            return "hello";
        }

        protected void onPostExecute(String jsonArrayUsers) {
            if (coments3.get(position).getComentFotoFilename() != "null"){
                getComentFotoAsyncTask mgetcomentfotoasync = new getComentFotoAsyncTask(coments3,position);
                mgetcomentfotoasync.execute();
            }else{
                adapterComents.addItem(coments3.get(position));
                progressBar.setVisibility(ProgressBar.GONE);
            }
//            adapter.updateList(coments, position);
        }
    }


    /////////////////// Executado várias vezes, atualiza as fotos dos comentarios /////////////////////////////
    public class getComentFotoAsyncTask extends AsyncTask<Void, Void, String> {

        Bitmap bitmapcoment;
        private final List<Utils.Coment> coments;
        int position;

        // Usar o construtor é uma forma dos parametros entrarem nessa subclasse
        getComentFotoAsyncTask( List<Utils.Coment> coments, int pos) {
            // keys que esta api retorna: "nivel", "foto", "first_name","xp", "estado", "RJ", "email"
            this.coments = coments;
            this.position = pos;
        }


        // Começa o envio da solicitação das fotos, as recebe e envia para onPostExecute
        @Override
        protected String doInBackground(Void... params) {

            //////////////////////// getComentFotoAsyncTask ///////////////////////////////////////
            InputStream in = null;
            try {
                //URL().openStream() = URL().openConnection().getInputStream()
                in = new URL(downloadComentFotoBaseUrl +coments.get(position).getComentFotoFilename()).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmapcoment = BitmapFactory.decodeStream(in);

            coments.get(position).setComentFoto(bitmapcoment);

            return "hello";

        }

        protected void onPostExecute(String jsonArrayUsers) {
            adapterComents.addItem(coments.get(position));
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

}
