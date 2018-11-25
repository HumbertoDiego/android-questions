package com.humberto.concursoengine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class ActivityLogin extends AppCompatActivity {

    // Método assincrono para entrar em ctt com a Restfull aPI do python anywhere
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Session session;
    String id;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        //////////////////////// Login pelo facebook /////////////////////////
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                // Se por algum motivo já estiver logado, nem tentar logar
                if (session.getloggedin()){
                    return ;
                }
                // Ligar a barra de progresso e retirar o formulário
                mProgressView.setVisibility(ProgressBar.VISIBLE);
                mLoginFormView.setVisibility(View.GONE);

                // Handle de profile infos
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("FaceLoginActivity", response.toString());

                                try {
                                    // Catch facebook profile_image OBJECT contem os dados do Face
                                    id = object.getString("id");
                                    URL profile_pic_url = null;
                                    Bitmap fb_circ_img = null;
                                    String encoded = "";
                                    try {
                                        profile_pic_url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                                        fb_circ_img = Utils.httpsUrl2CircleBitmap(profile_pic_url.toString());
                                        encoded = Base64.encodeToString(Utils.bitmap2ByteArray(fb_circ_img), Base64.DEFAULT);

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                    // Verificar se o usuario com este nome, email e senha=id já está cadastrado no nosso BD
                                    int emailExiste = 0;
                                    int senhaConfere = 0;
                                    String password = id;
                                    JSONObject meuObjJson1 = null;
                                    try {
                                        String result= getContent("http://humbertoalves.pythonanywhere.com/api/"+object.getString("email")+"/"+password+".json");
                                        meuObjJson1 = new JSONObject(result);
                                        Log.i("PythonLoginTask", String.valueOf(meuObjJson1));
                                        // {"nivel":5,"first_name":"Humberto Diego","last_name":"Facebooker","emailExiste":1,"jsonsettings":"{\"config\": {}, \"compras\": [], \"baralhos\": [\"basicos\", \"a\"]}","senhaConfere":1,"xp":549,"email":"humberto-xingu@live.com"}
                                        emailExiste = Integer.parseInt(meuObjJson1.getString("emailExiste"));
                                        senhaConfere = Integer.parseInt(meuObjJson1.getString("senhaConfere"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    // Caso ñ esteja cadastrado (emailExiste==0)=> cadastrar: Senha=id, Estado=NI, Sobrenome=Facebooker, idade =0
                                    if (emailExiste==0){
                                    ///////////////////// Registrar usuario do Face no BD  ////////////////////////////
                                        String nome = object.getString("name");
                                        String sobrenome = "Facebooker";
                                        String idade = "0";
                                        String estado = "NI";
                                        String[] params = new String[]{object.getString("email"),password,nome,sobrenome,idade,estado};

                                        try {
                                            // postContent => se encarrega registrar caso o usuario do face entra pela 1 vez
                                            JSONObject meuObjJson2 = new JSONObject(postContent(params, Utils.bitmaptoFile(fb_circ_img)));
                                            System.out.println(meuObjJson2); //{"chave": "33417401", "erro": "This email already has an account", "id": null}
                                            // Salvar os dados do face na sessão (shared prefs)
                                            session.setThumbnailBase64String(encoded);
                                            session.setNome(object.getString("name"));
                                            session.setEmail(object.getString("email"));
                                            session.setLoggedin(true);
                                            session.setXP(0);
                                            session.setNivel(1);
                                            session.setThumbnailPath(profile_pic_url.toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            // Se algo der errado (EX. Falta de conexao) sair e não gravar nada na sessão
                                            // TODO: colocar um toast avisando o erro aqui
                                            return;
                                        }
                                    }
                                    /////////////////////// Usuario já cadastrado //////////////////////////////////////////
                                    else {
                                        if (senhaConfere==1 & meuObjJson1!=null){
                                            // Usuário já cadastrado
                                            // Salvar os dados do servidor na sessão
                                            int nv = Integer.parseInt(meuObjJson1.getString("nivel"));
                                            int xpacum = Integer.parseInt(meuObjJson1.getString("xp"));
                                            session.setNivel(nv);
                                            session.setXPAcum(xpacum);
                                            session.setMax((int) (99 + (nv- 1) * 20 + Math.pow(nv, 2)));
                                            if (nv==1){
                                                session.setXP(xpacum);
                                            }else {
                                                session.setXP(xpacum - (int)(99+(nv-2)*20+Math.pow(nv-1, 2)));
                                            }
                                            // Salvar os dados do face na sessão
                                            session.setNome(object.getString("name"));
                                            session.setEmail(object.getString("email"));
                                            session.setLoggedin(true);
                                            session.setThumbnailPath(profile_pic_url.toString());
                                            session.setThumbnailBase64String(encoded);
                                        }
                                    }
                                    // Começar o mainActivity novamente com a sessão atualizada
                                    startActivity(new Intent(ActivityLogin.this, ActivityMain.class));
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }


                @Override
                public void onCancel() {
                    mProgressView.setVisibility(ProgressBar.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    Toast.makeText(ActivityLogin.this, "Erro ao logar no Facebook!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    mProgressView.setVisibility(ProgressBar.GONE);
                    mLoginFormView.setVisibility(View.VISIBLE);
                    Toast.makeText(ActivityLogin.this, "Erro ao logar no Facebook!", Toast.LENGTH_LONG).show();
                }
        });

    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

/////////////////////////// Login pelo pythonanywhere /////////////////////////////////////

// Barra entradas de email e senha sem coerencia e repassa para o método assincrono se OK
    private void attemptLogin() {

        // Se por algum motivo já estiver logado, nem tentar
        if (session.getloggedin()){
            return;
        }
        // Se já houver uma tentativa de logar assincrona em andamento, nem tentar
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: A validação de email no web2py é mais sofisticada, pode dar ok aqui e errado no servidor.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


// Entra em ctt com uma Restfull API de URL definida e recebe os resultados
// AsyncTask<Params(Void), Progress(int), Result(String)> => recebe nenhum(void) params no execute(),
// passa nada(void) para doInBackground
// passa inteiro para onProgressUpdate
// Saída do doInBackgrounde e Entrada de onPostExecute = result, é String
    public class UserLoginTask extends AsyncTask<Void, Integer, String> {

        private final String Rest_URL;

        // email e senha devem ser passados para o construtor, é uma forma dos parametros entrarem nessa subclasse
        UserLoginTask(String email, String password) {
            Rest_URL = "http://humbertoalves.pythonanywhere.com/api/"+email+"/"+password;
            System.out.println(Rest_URL);
        }
        // Antes: sumir com o formulário de login e aparecer a barra de progresso
        protected void onPreExecute(){
            mProgressView.setVisibility(ProgressBar.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return getContent(Rest_URL);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        // TODO: verificar se onProgressUpdate pode ser útil
        protected void onProgressUpdate(Integer... progress) {
            System.out.println(progress[0]);
        }

        protected void onPostExecute(String result) {
            JSONObject meuObjJson = null;
            mProgressView.setVisibility(ProgressBar.GONE);
            mLoginFormView.setVisibility(View.VISIBLE);
            mAuthTask = null;
            try {
                meuObjJson = new JSONObject(result);
                System.out.println(meuObjJson);
                // Verificar se o usuario ñ existe emailExiste = 0
                if (Integer.parseInt(meuObjJson.getString("emailExiste"))==0){
                    Toast.makeText(ActivityLogin.this, "Usuario ñ cadastrado", Toast.LENGTH_LONG).show();
                }else if(Integer.parseInt(meuObjJson.getString("senhaConfere"))==0){
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                // Cc email Existe e Senha Confere -> Permitir acesso , o objeto json contem dados extras
                // first_name, last_name, email, xp, nivel,jsonsettings,foto
                }else{
                    int nv = Integer.parseInt(meuObjJson.getString("nivel"));
                    int xpacum = Integer.parseInt(meuObjJson.getString("xp"));
                    String profile_pic_url = "http://humbertoalves.pythonanywhere.com/download/"+meuObjJson.getString("foto");

                    InputStream in = null;
                    try {
                        in = new URL(profile_pic_url).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bit = BitmapFactory.decodeStream(in);
                    String encoded = Base64.encodeToString(Utils.bitmap2ByteArray(bit), Base64.DEFAULT);
                    session.setThumbnailBase64String(encoded);
                    session.setNivel(nv);
                    session.setXPAcum(xpacum);
                    session.setMax((int) (99 + (nv- 1) * 20 + Math.pow(nv, 2)));
                    if (nv==1){
                        session.setXP(xpacum);
                    }else {
                        session.setXP(xpacum - (int)(99+(nv-2)*20+Math.pow(nv-1, 2)));
                    }
                    session.setLoggedin(true);
                    session.setEmail(meuObjJson.getString("email"));
                    session.setNome(meuObjJson.getString("first_name"));

                    Toast.makeText(ActivityLogin.this, session.getNome()+" "+session.getEmail(), Toast.LENGTH_LONG).show();
                    mainScreen();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressView.setVisibility(ProgressBar.GONE);
            mLoginFormView.setVisibility(View.VISIBLE);
        }
    }



// Verifica se a URL definida para usar a REstfull API possui o emai e a senha inserida no BD
// Retorna {"senhaConfere": 1, "first_name": "diego", "last_name": "alves", "email": "humberto-xingu13@live.com", "emailExiste": 1}
// ou {"senhaConfere": 0, "emailExiste": 1}
    private String getContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

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

// Insere os dados da URL definida para usar a REstfull API -
// email/password/nome/sobrenome/idade/estado/33417401.json
// Retorna {"chave": "33417401", "erro": "sem erros", "id": 3}
// ou {"chave": "33417401", "erro": "This email already has an account", "id": null}
    public String postContent(String[] parametros, File file) throws IOException {
        String charset = "UTF-8";
        String requestURL = "http://humbertoalves.pythonanywhere.com/api";

        // métodos POST com formulario não dá p mandar numa HttpURLConnection
        // para isso usar o MultipartUtility que gera um método post com possibilidades de enviar inclusive arquivo
        MultipartUtility multipart = null;
        try {
            multipart = new MultipartUtility(requestURL, charset);
            multipart.addHeaderField("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            multipart.addHeaderField("cache-control", "no-cache");
            multipart.addFormField("email_inserir", parametros[0]);
            multipart.addFormField("senha_inserir", parametros[1]);
            multipart.addFormField("nome_inserir", parametros[2]);
            multipart.addFormField("sobrenome_inserir", parametros[3]);
            multipart.addFormField("idade_inserir", parametros[4]);
            multipart.addFormField("estado_inserir", parametros[5]);
            multipart.addFormField("key", "33417401");
            multipart.addFilePart("foto_inserir", file);
            String response = multipart.finish(); // response from server.
            System.out.println(response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to retrieve data. URL may be invalid.";
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


    // Usando no metodo onClick dentro da arquivo activity_login.xml
    public void registrarsescreen(View v){
        startActivity(new Intent(this, ActivityRegister.class));
    }

    public void mainScreen(){
    startActivity(new Intent(ActivityLogin.this, ActivityMain.class));
}

}

