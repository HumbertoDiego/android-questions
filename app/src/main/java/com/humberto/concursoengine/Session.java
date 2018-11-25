package com.humberto.concursoengine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Session {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;
    String img_padrao = "" ;
    String user_api_url = "http://humbertoalves.pythonanywhere.com/api/";


    public Session(Context ctx){
        this.ctx = ctx;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap guest_user_bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.student);
        guest_user_bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        this.img_padrao = Base64.encodeToString(b, Base64.DEFAULT);

        prefs = ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = prefs.edit();

        editor.apply();

    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public void setNome(String nome){
        editor.putString("Nome",nome);
        editor.commit();
    }

    public void setEmail(String email){
        editor.putString("Email",email);
        editor.commit();
    }

    public void setNivel(int nivel){
        editor.putInt("Nivel",nivel);
        editor.commit();
        String Rest_URL = this.user_api_url+ getEmail()+"/"+String.valueOf(getNivel())+"/"+"nv33417401";
        PutAsyncTask msync = new PutAsyncTask(Rest_URL);
        msync.execute((Void) null);
    }
    public void setXP(int xp){
        editor.putInt("XP",xp);
        editor.commit();
    }
    public void setXPAcum(int xpacum){
        editor.putInt("XPAcum",xpacum);
        editor.commit();
        // Mesmo deixando a aplicação em plano de fundo ela continua a enviar as solicitações
        String Rest_URL = this.user_api_url+ getEmail()+"/"+String.valueOf(getXPAcum())+"/"+"xp33417401";
        PutAsyncTask msync = new PutAsyncTask(Rest_URL);
        msync.execute((Void) null);
    }

    public void setMax(int max){
        editor.putInt("Max",max);
        editor.commit();
    }
    public void setJsonSettings(String wichSetting, String jsonSettings){


        editor.putString("JsonSettings",jsonSettings);
        editor.commit();
    }

    public void setThumbnailPath(String thumbnailPath){
        editor.putString("ThumbnailPath",thumbnailPath);
        editor.commit();
    }

    public void setThumbnailBase64String(String encoded){
        editor.putString("ThumbnailBase64",encoded);
        editor.commit();
    }

    public String getNome(){
        return prefs.getString("Nome", "Usuário Convidado");
    }

    public String getEmail(){
        return prefs.getString("Email", "Clique aqui para entrar");
    }

    public int getNivel(){return prefs.getInt("Nivel",1);}

    public int getXP(){return prefs.getInt("XP",0);}

    public int getXPAcum(){return prefs.getInt("XPAcum",0);}

    public int getMax(){return prefs.getInt("Max",100);}

    public String getjsonSettings(){
        return prefs.getString("JsonSettings", "{'databases':[oab.db, enem.db]}");
    }

    public String getthumbnailPath() { return prefs.getString("ThumbnailPath", "@drawable/student"); }

    public String getThumbnailBase64String(Context ctx) { return prefs.getString("ThumbnailBase64",""); }

    public boolean getloggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }



    public void resetAll(){
        editor.clear();
        editor.commit();
    }



}
