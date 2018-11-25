package com.humberto.concursoengine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ActivityRegister extends AppCompatActivity {

    //UI Elements
    EditText etnome ;
    EditText etemail ;
    EditText etidade ;
    EditText etsenha ;
    ImageView imgView;
    Spinner spinner;
    private View mRegisterFormView;
    private View mProgressView;
    private RegisterTask mReg = null;
    private static final int PICK_IMAGE = 100;

    // Data elements
    String nome;
    String email;
    String idade ;
    String senha;
    String estado;
    Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        // Antigo teste para registrar users numa DB interna
//        mydbhelper = new InternalDatabaseHelper(this);
//        dbw = mydbhelper.getWritableDatabase();
//        values = new ContentValues();

        session = new Session(this);
        if (session.getloggedin()){
            Toast.makeText(ActivityRegister.this, "Logado", Toast.LENGTH_LONG).show();
        }
        etnome = (EditText)findViewById(R.id.editText);
        etemail = (EditText)findViewById(R.id.editText2);
        etsenha = (EditText)findViewById(R.id.editText3);
        etidade = (EditText) findViewById(R.id.editText8);
        mRegisterFormView = findViewById(R.id.regsiter_form);
        mProgressView = findViewById(R.id.progressBar1);
        imgView = (ImageView) findViewById(R.id.myImageView);
        /////// Coloca a lista de UF dos estados no Spinner
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last card_row_baralho. It is used as hint.
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(new String[]{"AP","AC","RR","AM","PA","RO","MT","MS","GO","DF","RS","PR","SC","SP","RJ","ES","MG","BA","AL","SE","PE","PB","RN","CE","PI","MA"});
        adapter.add("Estado");
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount()); //display hint
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                estado = spinner.getSelectedItem().toString();
                Log.w("Estado",estado);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }


    /////////////////////////// Registro pelo pythonanywhere /////////////////////////////////////
    // metodo onClick no botao "Registrar" dentro da arquivo activity_register.xml Dá início ao registro
    public void signin(View v){
        nome = etnome.getText().toString();
        email = etemail.getText().toString();
        senha = etsenha.getText().toString();
        idade =  etidade.getText().toString();
        Bitmap bit = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        if (mReg != null) {
            return;
        }
        RegisterCheck(nome,email,senha,idade,estado, bit);
    }

    // Barra entradas de email e senha sem coerencia e repassa para o método assincrono se OK
    private void RegisterCheck(String nome, String email, String password, String idade, String estado, Bitmap bitmap) {
        // Reset errors.
        etemail.setError(null);
        etsenha.setError(null);

        if (mReg != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etsenha.setError(getString(R.string.error_invalid_password));
            focusView = etsenha;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etemail.setError(getString(R.string.error_field_required));
            focusView = etemail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etemail.setError(getString(R.string.error_invalid_email));
            focusView = etemail;
            cancel = true;
        }
        // Se o user ñ forneceu os dados, atribuir algum valor para ñ dar o erro "...//..." na URL
        if (nome.isEmpty()){
            nome="Estudante";
        }
        if (estado.isEmpty()){
            estado="NI";
        }

        if (idade.isEmpty()){
            idade="0";
        }
        String sobrenome = "Audaz"; // Diferencia os registados no Face dos registrados no pythonanywhre
        if (cancel) {
            // There was an error; don't attempt login and focus the error field.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mProgressView.setVisibility(View.VISIBLE);
            mRegisterFormView.setVisibility(View.GONE);

            String[] params = new String[] {email,password,nome,sobrenome,idade,estado};

            File f = Utils.bitmaptoFile(bitmap);

            mReg = new RegisterTask(params, f, bitmap);
            mReg.execute((Void) null);


        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    // Entra em ctt com uma Restfull API de URL definida e recebe os resultados
    // AsyncTask<Params(String), Progress(Void), Result(String)> => recebe params do tipo String no execute(),
    // passa params[](String) para doInBackground
    // passa nada(void) para onProgressUpdate
    // Saída do doInBackgrounde e Entrada de onPostExecute = result, é String
    // Entra em ctt com uma Restfull API de URL definida e recebe os resultados
    // AsyncTask<Params(Void), Progress(int), Result(String)> => recebe nenhum(void) params no execute(),
    // passa nada(void) para doInBackground
    // passa inteiro para onProgressUpdate
    // Saída do doInBackgrounde e Entrada de onPostExecute = result, é String
    private class RegisterTask extends AsyncTask<Void, Void, String>{

        String[] parametros;
        File file;
        Bitmap fb_circ_img;

        public RegisterTask(String[] dados, File f ,Bitmap bit) {
            this.parametros = dados; //{email,password,nome,sobrenome,idade,estado};
            this.file = f;
            this.fb_circ_img = bit;
        }

        @Override
        protected String doInBackground(Void... params) {
            String charset = "UTF-8";
            String requestURL = "http://humbertoalves.pythonanywhere.com/api";

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

        protected void onPostExecute(String result) {
            mProgressView.setVisibility(View.GONE);
            mRegisterFormView.setVisibility(View.VISIBLE);
            mReg = null;

            try {
                JSONObject meuObjJson = new JSONObject(result);
                System.out.println(meuObjJson);
                if (meuObjJson.isNull("id")){
                    Toast.makeText(ActivityRegister.this, "Erro no registro: "+meuObjJson.getString("erro"), Toast.LENGTH_LONG).show();

                }else{
                    session.setLoggedin(true);
                    session.setEmail(email);
                    session.setNome(nome);
                    session.setNivel(1);
                    session.setXP(0);

                    // Use following method to convert bitmap to byte array:
                    byte[] b = Utils.bitmap2ByteArray(fb_circ_img);
                    // Convert byte array to base64 String
                    String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                    // Guardar na shared prefs
                    session.setThumbnailBase64String(encoded);

                    Toast.makeText(ActivityRegister.this, session.getNome()+" "+session.getEmail(), Toast.LENGTH_LONG).show();
                    mainScreen();

                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void mainScreen (){
        startActivity(new Intent(this, ActivityMain.class));
    }

    ///////////////////// Métodos chamado do xml, ao clicar escolher foto ////////////////////////
    public void escolher_foto(View v){
        // checks if the app has permission at runtime(needed only once for write in app),
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Requests the permission if necessary:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  0);
            chooseFoto();
        }else{
            chooseFoto();
        }
    }

    // Abre o seletor filtrado para fotos
    public void chooseFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    // Convert the data chosen to Bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            Uri imageUri = data.getData();
            imgView.setRotation(Utils.getRotation(getApplicationContext(),imageUri));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                putFoto(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //  Gera o thumbnail, corta em circulo e aplica ao ImageView
    public void putFoto(Bitmap bitmap){
//        File dir =Environment.getExternalStorageDirectory();
//        File file = new File( dir , "/Download/fbimg.jpg");
//        System.out.println(file.getAbsolutePath());
//        if (file.exists()) {
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            int dimension = 124;
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
            imgView.setImageBitmap(Utils.getCircleBitmap(bitmap));

//        }
    }



/*
Colunas de metadados de uma foto tirada no galaxy S7
I/System.out: _id
I/System.out: _data
I/System.out: _size
I/System.out: _display_name
I/System.out: mime_type
I/System.out: title
I/System.out: date_added
I/System.out: date_modified
I/System.out: description
I/System.out: picasa_id
I/System.out: isprivate
I/System.out: latitude
I/System.out: longitude
I/System.out: datetaken
I/System.out: orientation
I/System.out: mini_thumb_magic
I/System.out: bucket_id
I/System.out: bucket_display_name
I/System.out: width
I/System.out: height
I/System.out: group_id
I/System.out: spherical_mosaic
I/System.out: addr
I/System.out: langagecode
I/System.out: is_secretbox
I/System.out: weather_ID
I/System.out: sef_file_type
I/System.out: reusable
I/System.out: is_drm
I/System.out: is_favorite
I/System.out: sef_file_sub_type
I/System.out: smartcrop_rect
*/
}
