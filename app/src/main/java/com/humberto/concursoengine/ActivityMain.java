package com.humberto.concursoengine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.BassBoost;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Data elements
    Session session;

    //UI elements que persistem nas subclasses
    ImageView thumb;
    TextView nome;
    TextView option;
    ImageView imageSignoutButton;
    View header;

    // Adapters e host pagers
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public List<Database> databases;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d("", "In the onCreat() event");

        // Menu superior
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Manejo da política de uso de várias threads
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* Começo da aplicação das Tabs */
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity. Conforme a Subclasse lá embaixo.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Tablayout é o menu das Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(mViewPager);
        /* Fim da aplicação das Tabs */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Manejo do usuário que está logado, captura de dados, preferências e permissões
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);

    }

    public void onStart()
    {
        super.onStart();
//        Log.d("", "In the onStart() event");
        // Recupera das shared preferencies os dados do usuário
        session = new Session(this);
        initializeDbsData();

        if (session.getloggedin()){
            nome = (TextView) header.findViewById(R.id.first_name) ;
            nome.setText(session.getNome());
            option =(TextView) header.findViewById(R.id.option) ;
            option.setText(session.getEmail());
            imageSignoutButton = (ImageView) header.findViewById(R.id.imagesignout);
            imageSignoutButton.setVisibility(ImageView.VISIBLE);
            thumb = (ImageView) header.findViewById(R.id.thumbnail);
            byte[] imageAsBytes = Base64.decode(session.getThumbnailBase64String(this).getBytes(),Base64.DEFAULT);
            thumb.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

            if (AccessToken.getCurrentAccessToken() != null) {
                // TODO: Concetado pelo Facebook alguma diferenciação? escrever aqui
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //Após logado,  Impede de voltar pra main screen com a setagem de usuario convidado
        moveTaskToBack(true);
    }

    // Reticências vertical no menu = Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    // Reticências vertical no menu = Options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar card_row_baralho clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.out.println("clicado num card_row_baralho da reticencias vertical?");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view card_row_baralho clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mais_provas) {
            startActivity(new Intent(this, ItemListActivity.class));
        } else if (id == R.id.nav_gallery) {
            this.startActivity(new Intent(this, ActivityLogin.class));
        }  else if (id == R.id.nav_prefs_geral) {
            DownloadExternalDBTask dbtask = new DownloadExternalDBTask(getApplicationContext(),"oab");
            dbtask.execute();
        } else if (id == R.id.nav_prefs_perfil) {
            DownloadExternalDBTask dbtask = new DownloadExternalDBTask(getApplicationContext(),"enem");
            dbtask.execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    // Método chamado do xml, ao clicar no signout button
    public void logout(View v){
        LoginManager.getInstance().logOut();
        session.resetAll();
        finish(); // chamar o finish para impedir q o back retorne para esta tela
        startActivity(new Intent(this, ActivityMain.class));
    }

    // Método chamado do xml, ao clicar no usuário convidado/nome
    public void login_profilescreen(View v){
         if(session.getloggedin()){
             // Se já estiver logado ñ faz nada
        }else {
             // CC, começar o Login
             this.startActivity(new Intent(this, ActivityLogin.class));
             finish();
         }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static List<Database> databases2 = null;

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber,
                                                      List<Database> databases) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            databases2 = databases;
            return fragment;
        }

                @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState)  {

            // Para pegar o Context/activity:
            // getActivity() funciona em views inseridas na activity
            // this funcina na main thread, main Class
            // Activiyt.this funciona em subclasses da main thread
            final Context ctx = getActivity();
            final Session session2 = new Session(ctx);

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                // LayoutInflater gera um cópia do xml na view correspondente
            /* Começo da aplicação do recycler para expor os baralhos*/
                View recView = inflater.inflate(R.layout.tab_baralhos, container, false);
                RecyclerView recList = (RecyclerView) recView.findViewById(R.id.rv);
                recList.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(ctx);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recList.setLayoutManager(llm);

                RVAdapterDatabases adapter = new RVAdapterDatabases(databases2,ctx);

                // Um dos método para setar o click lister num item do recycler view
                // Diz q existe um click listener no adapter e declara ele aqui na Activity
                adapter.setOnItemClickListener(new RVAdapterBaralhos.ClickListener() {
                    @Override
                    public void itemClicked(View v, int position) {

                        System.out.println("DBToOpen: "+databases2.get(position).title);

                        Intent intent = new Intent(ctx, ActivityProvas.class);
                        intent.putExtra("dbName", databases2.get(position).title);
                        startActivity(intent);
                    }
                });

                recList.setAdapter(adapter);
            /* Começo da aplicação do recycler*/

                return recView;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

                View profileView = inflater.inflate(R.layout.tab_points, container, false);

                int xp = session2.getXP();
                int nv = session2.getNivel();
                int max = session2.getMax();
                final int maxant = (int) (99 + (nv - 2) * 20 + Math.pow(nv - 1, 2));
                final ArcProgress arcProgress = (ArcProgress) profileView.findViewById(R.id.arc_progress);
                arcProgress.setSuffixText("/" + String.valueOf(max));
                arcProgress.setMax(max);
                arcProgress.setProgress(xp);
                arcProgress.setBottomText("Nv. " + String.valueOf(nv));


                TextView pname = (TextView) profileView.findViewById(R.id.person_name);
                pname.setText(session2.getNome());

                ImageView pfoto = (ImageView) profileView.findViewById(R.id.person_photo);
                if (session2.getloggedin()) {
                    byte[] imageAsBytes = Base64.decode(session2.getThumbnailBase64String(ctx).getBytes(), Base64.DEFAULT);
                    pfoto.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }


                final TextView plv = (TextView) profileView.findViewById(R.id.person_level);
                plv.setText("Nível " + session2.getNivel());

                final TextView pexp = (TextView) profileView.findViewById(R.id.person_exp);
                pexp.setText(session2.getXPAcum() + " xp");

                Button edit = (Button) profileView.findViewById(R.id.edit_profile_button);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /// Método para aumentar 1 de xp:
                        session2.setXPAcum(session2.getXPAcum() + 1);
                        session2.setXP(session2.getXP() + 1);

                        if (session2.getXP() >= session2.getMax()) {
                            session2.setNivel(session2.getNivel() + 1);
                            session2.setXP(session2.getXP() - session2.getMax());

                            session2.setMax((int) (99 + (session2.getNivel() - 1) * 20 + Math.pow(session2.getNivel(), 2)));
                            arcProgress.setMax(session2.getMax());
                            arcProgress.setSuffixText("/" + arcProgress.getMax());
                        }


                        plv.setText("Nível " + session2.getNivel());
                        pexp.setText(session2.getXPAcum() + " xp");
                        arcProgress.setBottomText("Nv. " + session2.getNivel());
                        arcProgress.setProgress(session2.getXP());
                    }
                });

                return profileView;
            } else {
                // Essa Tab redireciona para um fragment próprio q possui uma progressbar, já que possui
                // uma espera pelos resultados do servidor
                View rankTabView = inflater.inflate(R.layout.tab_rank, container, false);
                ProgressBar rankProgressBar = (ProgressBar) rankTabView.findViewById(R.id.rankProgressBar);

                RecyclerView recList2 = (RecyclerView) rankTabView.findViewById(R.id.rv2);
                recList2.setHasFixedSize(true);
                LinearLayoutManager llm2 = new LinearLayoutManager(ctx);
                llm2.setOrientation(LinearLayoutManager.VERTICAL);
                recList2.setLayoutManager(llm2);


                getRankTask mgetRankTask = new getRankTask(ctx, rankProgressBar, recList2, session2.getEmail());
                mgetRankTask.execute((Void) null);

                return rankTabView;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * keep every loaded fragment in memory
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Esse método transfere a posição e a String para o fragmento
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1, databases);
        }

        // Esse método determina qtas Tabs vão se espremer no topo
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        // Esse métddo informa o título das tabs
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    //estamos na tab Section 1 Dados da sessão:
                    return "Meus Baralhos";
                case 1:
                    return "Meus Pontos";
                case 2:
                    return "Concorrentes";
            }
            return null;
        }

    }

   private void initializeDbsData()  {

        JSONObject mjson;
        JSONArray mjsonarray = null;
        try {
            mjson = new JSONObject(session.getjsonSettings());
            mjsonarray = mjson.getJSONArray("databases");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        databases = new ArrayList<>();

        Bitmap fb_circ_img = BitmapFactory.decodeResource(this.getResources(),R.drawable.abstracttexture );
        String title = "";
        for (int i=0;i<mjsonarray.length();i++){
            try {
                title =mjsonarray.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            databases.add(new Database(title, fb_circ_img,"10 anos anteriores de provas!"));
        }
    }

////////////////////////////  Async Tasks ////////////////////////////////////////////////////////////////
    // Entra em ctt com uma Restfull API de URL definida e recebe os resultados
// AsyncTask<Params(Void), Progress(int), Result(String)> => recebe nenhum(void) params no execute(),
// passa nada(void) para doInBackground
// passa nada(void) para onProgressUpdate
// Saída do doInBackground e Entrada de onPostExecute = result, é Void
    public static class getRankTask extends AsyncTask<Void, Void, String> {

        private final String Rest_URL;
        private final String key;
        private final String email;
        private final Context ctx2;
        private final ProgressBar pgbar2;
        private final List<Utils.Person> personsTop10;
        RecyclerView recList2;

        // Usar o construtor é uma forma dos parametros entrarem nessa subclasse
        getRankTask(Context ctx,  ProgressBar rankProgressBar , RecyclerView recListtop10, String email) {
            // keys que esta api reotrna: "nivel", "foto", "first_name","xp", "estado", "RJ", "email"
            this.Rest_URL = "http://humbertoalves.pythonanywhere.com/getrank/";
            this.key = "/33417401";
            this.email = email;
            this.ctx2 = ctx;
            this.pgbar2 = rankProgressBar;
            this.personsTop10 = new ArrayList<>();
            this.recList2 = recListtop10;
        }

        // Liga a barra de progresso
        @Override
        protected void onPreExecute(){
            pgbar2.setVisibility(ProgressBar.VISIBLE);
        }

        // Começa o envio da solicitação dos dados, os recebe e envia para onPostExecute
        @Override
        protected String doInBackground(Void... params) {
            Bitmap student = BitmapFactory.decodeResource( ctx2.getResources(), R.drawable.student);
            JSONArray mjsonTop10Array = null;
            JSONArray mjsonRedorArray = null;
            int mjsonPos = 0;
            int mjosStartBeforeUserBy;
            int start = 0;
            JSONObject mjsonObj = null;
            if (email=="Clique aqui para entrar"){
                try {
                    mjsonObj = new JSONObject(getContent(Rest_URL+"humberto-xingu@live.com"+key));
                    mjsonTop10Array = mjsonObj.getJSONArray("top10");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                personsTop10.add(new Utils.Person("Top 10" ,1)); // 1º card adicionado:card título escrito "Top 10"

                if (mjsonObj != null) {
                    for (int i = 0; i < mjsonTop10Array.length(); i++) {         //percorre a jsonArray
                        try {
                            personsTop10.add(new Utils.Person(mjsonTop10Array.getJSONObject(i).getString("first_name") + " - " + mjsonTop10Array.getJSONObject(i).get("estado").toString(),
                                    String.valueOf(i + 1),
                                    "Nível " + mjsonTop10Array.getJSONObject(i).get("nivel").toString() + " - " + mjsonTop10Array.getJSONObject(i).get("xp").toString() + " XP",
                                    0,
                                    student,
                                    mjsonTop10Array.getJSONObject(i).getString("foto")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }else{
                try {
    ////////////    mjsonArray = new JSONArray(getContent(Rest_URL));     // A String é alocada num jsonArray
                    mjsonObj = new JSONObject(getContent(Rest_URL+email+key));
                    mjsonTop10Array = mjsonObj.getJSONArray("top10");
                    mjsonRedorArray =  mjsonObj.getJSONArray("redor");
                    mjsonPos = mjsonObj.getInt("position");
                    mjosStartBeforeUserBy = mjsonObj.getInt("startBeforeUserBy");
                    start =  mjsonPos - mjosStartBeforeUserBy;

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                personsTop10.add(new Utils.Person("Top 10" ,1)); // 1º card adicionado:card título escrito "Top 10"

                if (mjsonObj != null) {
                    for (int i = 0; i < mjsonTop10Array.length(); i++) {         //percorre a jsonArray
                        try {
                            personsTop10.add(new Utils.Person(mjsonTop10Array.getJSONObject(i).getString("first_name") + " - " + mjsonTop10Array.getJSONObject(i).get("estado").toString(),
                                    String.valueOf(i + 1),
                                    "Nível " + mjsonTop10Array.getJSONObject(i).get("nivel").toString() + " - " + mjsonTop10Array.getJSONObject(i).get("xp").toString() + " XP",
                                    0,
                                    student,
                                    mjsonTop10Array.getJSONObject(i).getString("foto")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    personsTop10.add(new Utils.Person("... Você é o próximo", 1)); // 11º card adicionado:card título escrito "... Você é o próximo"

                    for (int i = 0; i < mjsonRedorArray.length(); i++) {
                        try {
                            // Esta é a posição do usuário, o item type view recebe um valor = 2 para o adapter poder atribuir um card diferente
                            if (i + start == mjsonPos) {
                                personsTop10.add(new Utils.Person(mjsonRedorArray.getJSONObject(i).get("first_name").toString() + " - " + mjsonRedorArray.getJSONObject(i).get("estado").toString(),
                                        String.valueOf(i + start),
                                        "Nível " + mjsonRedorArray.getJSONObject(i).get("nivel").toString() + " - " + mjsonRedorArray.getJSONObject(i).get("xp").toString() + " XP",
                                        2,
                                        student,
                                        mjsonRedorArray.getJSONObject(i).getString("foto")));
                            } else {
                                personsTop10.add(new Utils.Person(mjsonRedorArray.getJSONObject(i).get("first_name").toString() + " - " + mjsonRedorArray.getJSONObject(i).get("estado").toString(),
                                        String.valueOf(i + start),
                                        "Nível " + mjsonRedorArray.getJSONObject(i).get("nivel").toString() + " - " + mjsonRedorArray.getJSONObject(i).get("xp").toString() + " XP",
                                        0,
                                        student,
                                        mjsonRedorArray.getJSONObject(i).getString("foto")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return "hello";
        }

        protected void onPostExecute(String jsonArrayUsers) {
            pgbar2.setVisibility(ProgressBar.GONE);
            recList2.setVisibility(RecyclerView.VISIBLE);

            RVAdapterRank2 adapterTop10 = new RVAdapterRank2(personsTop10);
            recList2.setAdapter(adapterTop10);

            // Começa fazer os downloads das fotos das pessoas no rank
            for(int i=0;i<personsTop10.size();i++){
                if (personsTop10.get(i).fotoFilename != null){
                    getBaralhoFotoAsyncTask mgetfotosasync = new getBaralhoFotoAsyncTask(ctx2 , recList2, adapterTop10, personsTop10, i);
                    mgetfotosasync.execute((Void)null);
                }
            }
        }

        // Entra no conexão http em uma REst API para receber dados
        private String getContent(String myurl) throws IOException {
            InputStream is = null;
            int length = 5000; // tamanho do texto q se espera receber

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
        public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[length];
            reader.read(buffer);
            return new String(buffer);
        }
    }

    // Entra em ctt com uma Restfull API de URL definida e recebe os resultados
// AsyncTask<Params(Void), Progress(int), Result(String)> => recebe nenhum(void) params no execute(),
// passa nada(void) para doInBackground
// passa nada(void) para onProgressUpdate
// Saída do doInBackground e Entrada de onPostExecute = result, é String
//////////////////// Executado várias vezes, atualiza as fotos no rank /////////////////////////////
    public static class getBaralhoFotoAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context ctx2;
        RecyclerView recList2;
        Bitmap bitmap;
        private final List<Utils.Person> persons;
        RVAdapterRank2 adapterRank;
        int position;

        // Usar o construtor é uma forma dos parametros entrarem nessa subclasse
        getBaralhoFotoAsyncTask(Context ctx,  RecyclerView recListBaralhos, RVAdapterRank2 adapter,
                                List<Utils.Person> personsTop10, int pos) {
            // keys que esta api reotrna: "nivel", "foto", "first_name","xp", "estado", "RJ", "email"
            this.ctx2 = ctx;
            this.recList2 = recListBaralhos;
            this.persons= personsTop10;
            this.adapterRank = adapter;
            this.position = pos;
        }


        // Começa o envio da solicitação das fotos, as recebe e envia para onPostExecute
        @Override
        protected String doInBackground(Void... params) {

            String base_url = "http://humbertoalves.pythonanywhere.com/home4/download/";
            InputStream in = null;

            try {
                //URL().openStream() = URL().openConnection().getInputStream()
                in = new URL(base_url+persons.get(position).getFotoFilename()).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(in);

            persons.get(position).setPhotoId(bitmap);

            return "hello";
        }

        protected void onPostExecute(String jsonArrayUsers) {
            adapterRank.updateList(persons, position);
        }
    }


// Faz o download do Banco de dados que guarda as questoes
// Colunas: _id(int),pergunta(text),resposta(text),intervalos(int),curtidas(int),assunto(text),date(yyyy-MM-dd)
    public static class DownloadExternalDBTask extends AsyncTask<Void , Void, String>{

        String baseUrl;
        String dbName;
        Context ctx;

        DownloadExternalDBTask(Context context, String dbName){
            this.baseUrl = "http://humbertoalves.pythonanywhere.com/uploaddb2/";
            this.dbName  = dbName;
            this.ctx = context;
        }


        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                result = getContent(baseUrl+dbName);// 1ª chamada ao servidor para descobrir o nome dos bancos de dados
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String text){
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(ctx, text, duration);
            toast.show();
        }

        protected void onCancelled() {
            System.out.println("DownloadExternalDBTask cancelled");
        }

        private String getContent(String myurl) throws IOException, JSONException {

//            File dir = new File(Environment.getExternalStorageDirectory()+"/ConcursoEngine/Databases");
            File dir = new File("/data/data/com.humberto.concursoengine/databases/");
            if(!dir.exists()){
                dir.mkdir();
            }
            File file;
            URL url = new URL(myurl);
            file = new File(dir, dbName+".db"); // nome do arquivo do banco de dados a ser gravado
            System.out.println(file.getAbsolutePath());
            long begin = System.currentTimeMillis();

            // Método de GET para fazer download usando a common.io.apache: (SUPERIOR !!)
            FileUtils.copyURLToFile(url, file);
            long end = System.currentTimeMillis();

//             Método de download usando o harcode: (INFERIOR !!)
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("GET");
//            int response = conn.getResponseCode();
//            Log.d("TA", "The response is: " + response);
//            input = conn.getInputStream();
//            try {
//
//
//                OutputStream output = new FileOutputStream(file);
//                try {
//                    try {
//                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                        int read;
//
//                        while ((read = input.read(buffer)) != -1) {
//                            output.write(buffer, 0, read);
//                        }
//                        output.flush();
//                    } finally {
//                        output.close();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace(); // handle exception, define IOException and others
//                }
//            } finally {
//                input.close();
//            }

            return "Tempo: "+String.valueOf((end-begin)/1000.0)+"s"+"\nGravado! Procure em :"+
                    dir+"/"+dbName;
        }
    }


}
