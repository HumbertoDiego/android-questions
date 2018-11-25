package com.humberto.concursoengine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 Esse BD fica gravado internamente na memmória do app, dá pra resgatar o sqlite dos emuladores:
 Abrir um emulador: Tools > Android > Android Device Monitor:
 Shell: adb root shell > "restarting adbd as root":
 Ir para /data/data/"seu package"/databases/xxx.db: fazer o pull de "xxx.db"
 **/
public class InternalDatabaseHelper extends SQLiteOpenHelper {


    private static int DB_VERSION = 1;

    public InternalDatabaseHelper(Context context, String DB_NAME) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public ArrayList getTablesNames(){
        SQLiteDatabase myDataBase = this.getReadableDatabase();
        ArrayList<String> arrTabelas = new ArrayList<String>();
        String query = "SELECT name FROM sqlite_master WHERE type='table'";
        Cursor c = myDataBase.rawQuery(query, null);
        String aux;
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                aux = c.getString(0);
                System.out.println(aux);
                // pegar somente as tabelas q possuem OAB no nome , as outras são tabelas de sistema
                if (aux.toString().matches("(.*)sql(.*)") || aux.toString().matches("(.*)entry(.*)") ||
                        aux.toString().matches("(.*)meta(.*)") || aux.toString().matches("(.*)quest(.*)")){
                }else{
                    arrTabelas.add(aux);
                }
                c.moveToNext();
            }
        }
        c.close();

        return arrTabelas;
    }


    public ArrayList<String> getQuestionFromTable(String tableName, int index){
    SQLiteDatabase dbr = this.getReadableDatabase();
    ArrayList<String> arrDados = new ArrayList<>();
    Cursor c = dbr.rawQuery("SELECT * FROM "+ tableName+ " ORDER BY intervaloNew", null);
        if (c.moveToPosition(index)) {
                arrDados.add(c.getString(0)); // id
                arrDados.add(c.getString(1)); // pergunta
                arrDados.add(c.getString(2)); // resposta
                arrDados.add(c.getString(3)); // intervaloNew
                arrDados.add(c.getString(4)); // curtidas
                arrDados.add(c.getString(5)); // tag
                arrDados.add(c.getString(6)); // date
        }
    c.close();

    return arrDados;
    }

    public int getCountOfNewQuestions(String tableName, int limitBy){
        int count = 0;
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor c = dbr.rawQuery("SELECT * FROM "+tableName+" WHERE date IS NULL AND intervalo=0"+" LIMIT "+String.valueOf(limitBy), null);
        count = c.getCount();
        c.close();
        return count;
    }

    public ArrayList<String[]> getArrayListOfNewQuestions(String tableName, int limitBy){
        SQLiteDatabase dbr = this.getReadableDatabase();
        ArrayList<String[]> arrDados = new ArrayList<>();
        Cursor c = dbr.rawQuery("SELECT * FROM "+tableName+" WHERE date IS NULL AND intervalo=0"+" LIMIT "+String.valueOf(limitBy), null);
        String[] aux;
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                aux = new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6)};
                arrDados.add(aux);
                c.moveToNext();
            }
        }
        c.close();
        return arrDados;
    }

    public int getCountOfRevQuestions(String tableName){
        int count = 0;
        SQLiteDatabase dbr = this.getReadableDatabase();

        // TODO: função q retorna a String today
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(GregorianCalendar.getInstance().getTime()); // Seta a data de hoje no calendário
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //Date today = sdf.parse("23-06-2017");
        Date today = new Date(calendar.getTimeInMillis()); //
        String todayString = sdf.format(today);

        String query = "SELECT * FROM "+tableName+" WHERE date<='"+todayString+"' ORDER BY intervalo";
        Cursor c = dbr.rawQuery(query, null);
        count = c.getCount();
        c.close();
        return count;
    }

    public ArrayList<String[]> getArrayListOfRevQuestions(String tableName){
        SQLiteDatabase dbr = this.getReadableDatabase();
        ArrayList<String[]> arrDados = new ArrayList<>();

//        // TODO: Get String Today function
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(GregorianCalendar.getInstance().getTime()); // Seta a data de hoje no calendário
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //Date today = sdf.parse("23-06-2017");
        Date today = new Date(calendar.getTimeInMillis()); //
        String todayString = sdf.format(today);
        System.out.println("today="+todayString);
        String query = "SELECT * FROM "+tableName+" WHERE date<='"+todayString+"' ORDER BY intervalo";
        System.out.println("query="+query);
        Cursor c = dbr.rawQuery(query, null);
        String[] aux;
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                                    // id         pergunta       resposta       intervalo      curtidas         assunto         date
                aux = new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6)};
                arrDados.add(aux);

                c.moveToNext();
            }
        }
        c.close();

        return arrDados;
    }

    public String[] getColnames(String tableName){
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor c = dbr.rawQuery("SELECT * FROM "+ tableName, null);
        String[] arrDados = c.getColumnNames();
        return arrDados;
    }


    public int updateQuestionInterval(String tableName, String questionId, int intervaloAtual, String fatorMultiplicativo){
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(intervaloAtual==0){
            switch (fatorMultiplicativo) {
                case "facil":
                    intervaloAtual=4;
                    break;
                case "medio":
                    intervaloAtual=1;
                    break;
                case "dificil":
                    intervaloAtual=0;
                    break;
            }
        }else{
            switch (fatorMultiplicativo) {
                case "facil":
                    intervaloAtual= (int) Math.round(intervaloAtual*2.5);
                    break;
                case "medio":
                    intervaloAtual= (int) Math.round(intervaloAtual*1.5);
                    break;
                case "dificil":
                    intervaloAtual=0;
                    break;
            }
        }
        values.put("intervalo",intervaloAtual);
        int i = dbw.update(tableName, values, "_id=" + questionId, null);
        dbw.close();
        return intervaloAtual;
    }



    public boolean updateQuestionDate(String tableName, String questionId, String newDate){
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date",newDate);
        System.out.println("DateInputed= "+newDate);
        int i = dbw.update(tableName, values, "_id=" + questionId, null);
        System.out.println("DateInputedSuccess? "+i);
        dbw.close();
        return i>0;
    }

//
//    public ArrayList<String[]> getTableData(String tableName, int index){
//        SQLiteDatabase dbr = this.getReadableDatabase();
//        ArrayList<String[]> arrDados = new ArrayList<>();
//        String[] aux;
//        Cursor c = dbr.rawQuery("SELECT * FROM "+ tableName, null);
//            if (c.moveToFirst()) {
//                while ( !c.isAfterLast() ) {
//                    aux = new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4)};
//                    arrDados.add(aux);
//                    c.moveToNext();
//                }
//            }
//        c.close();
//
//        return arrDados;
//    }


//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CriarTB1);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
////        if (newVersion > oldVersion) {
////            db.execSQL("ALTER TABLE foo ADD COLUMN new_column INTEGER DEFAULT 0");
////        }
//        db.execSQL(DropTB1);
//        onCreate(db);
//    }
//
//    public String getCOL_EMAIL() {
//        return COL_EMAIL;
//    }
//
//    public String getCOL_NOME() {
//        return COL_NOME;
//    }
//
//    public String getCOL_SENHA() {
//        return COL_SENHA;
//    }
//
//    public String getTABLE1_NAME() {
//        return TABLE1_NAME;
//    }
//

//
//    public boolean insertUserData(String nome, String email, String senha){
//        SQLiteDatabase dbw = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_NOME,nome);
//        values.put(COL_EMAIL,email);
//        values.put(COL_SENHA,senha);
//        long id = dbw.insert(TABLE1_NAME,null,values);
//        if (id == -1)
//            return false;
//        else
//            return true;
//    }
}
