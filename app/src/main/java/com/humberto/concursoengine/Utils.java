package com.humberto.concursoengine;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public final class Utils {

    private Utils() {
    }

    public static class DownloadableDb {

        String ord;
        String nome;
        String desc;
        int viewType;

        DownloadableDb(String ord, String nome, String desc, int viewType){
            this.ord = ord;
            this.nome = nome;
            this.desc = desc;
            this.viewType = viewType;
        }
    }



    public static class Person {

        String ord;
        String nome;
        Bitmap photoId;
        String nivExp;
        String fotoFilename = null;
        int viewType; //2 para o user card, 1 para title e 0 para normal

        Person(String name, String age, String Exp, int viewType , Bitmap photoId, String fotoFilename) {
            this.nome = name;
            this.ord = age;
            this.nivExp = Exp;
            this.photoId = photoId;
            this.viewType = viewType;
            this.fotoFilename = fotoFilename; // "foto": "auth_user.foto.b8ebd6660807b42b.70726f66696c657069632e706e67.png"
        }

        Person (String name ,int viewType){
            this.nome = name;
            this.viewType = viewType;
        }

        Person (String viewType){
            this.viewType = Integer.getInteger(viewType);
        }

        public int getType_row() {
            return viewType;
        }

        public String getFotoFilename() { return fotoFilename; }

        public String getNome() { return nome; }

        public void setPhotoId(Bitmap photoId) {
            this.photoId = photoId;
        }
    }

    public static class Coment {

        Bitmap userFoto;
        Bitmap comentFoto;
        String userFotoFilename;
        String comentFotoFilename;
        String ord;
        String nome;
        String currentUserEmail;
        String comentario;
        String date;
        String dbName;
        String tableName;
        String questionId;
        String comentId;
        int viewType; //1 para o usuário e 0 para os demais usuários
        int curtidas= 0;
        boolean liked;


        Coment(String name, String ord, String comentario, int viewType, Bitmap userFoto, String userFotoFilename,
               int curtidas, String date, String comentFotoFilename, String dbName, String tableName,
               String questionId, boolean liked, String email, String id) {

            this.nome = name;
            this.ord = ord;
            this.comentario = comentario;
            this.userFoto = userFoto;
            this.userFotoFilename = userFotoFilename;
            this.curtidas = curtidas;
            this.date = date;
            this.comentFotoFilename = comentFotoFilename;

            // Identificador do usuário q está visualizando o comentario, futuro identificador do like
            this.currentUserEmail = email;

            // Identificador de q o Usuario atual é um cuttidor do comentario
            this.liked = liked;

            // Identificadores do comentario
            this.viewType = viewType;
            this.comentId = id;
            this.dbName = dbName;
            this.tableName = tableName;
            this. questionId = questionId;
        }


        public int getType_row() { return viewType; }

        public String getUserFotoFilename() { return userFotoFilename; }

        public String getComentFotoFilename(){ return comentFotoFilename; }

        public void setUserFoto(Bitmap userFoto) {
            this.userFoto = userFoto;
        }

        public void setComentFoto(Bitmap comentFoto) {
            this.comentFoto = comentFoto;
        }
    }

    // not static inner classes requires an instance of the outer class.
    public static class Baralho {
        String name;
        String age;
        String downloadName = "";
        Bitmap photoId;
        String badgeNew = "+10";
        String badgeRev = "+0";

        Baralho(String name, String age, Bitmap photoId, String badgeNew, String badgeRev ) {
            this.name = name;
            this.age = age;
            this.photoId = photoId;
            this.badgeNew = badgeNew;
            this.badgeRev = badgeRev;
        }

    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    // Transforma uma imagem quadrada em img circular
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


    public static File bitmaptoFile(Bitmap bitmap){
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;

        // Caminho de gravação
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        File f = new File( file_path, "profilepic.png");

// TODO: colocar try cacth em cada um aqui em baixo
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    public static byte[] bitmap2ByteArray (Bitmap bitmap){
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();

        return bitmapdata;
    }



    public static Bitmap httpsUrl2CircleBitmap(String path){
        HttpsURLConnection conn1 = null;
        try {
            conn1 = (HttpsURLConnection) new URL(path.toString()).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setFollowRedirects(true);
        conn1.setInstanceFollowRedirects(true);
        Bitmap fb_circ_img = null;
        try {
            fb_circ_img = getCircleBitmap(BitmapFactory.decodeStream(conn1.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fb_circ_img;
    }

    public static int getRotation(Context ctx, Uri imageUri){
        String orientation = "0";
        Cursor cursor;
        cursor = ctx.getContentResolver().query(imageUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            orientation = cursor.getString(0);
            if (orientation==null){
                orientation="0";
            }
            cursor.close();
        }
//        System.out.println(orientation);
        return Integer.parseInt(orientation);
    }

    public static Bitmap girarBitmapEm(Bitmap bitmap,int rotation){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

}
