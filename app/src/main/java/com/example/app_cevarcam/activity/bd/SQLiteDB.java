package com.example.app_cevarcam.activity.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.app_cevarcam.activity.config.AppConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteDB extends SQLiteOpenHelper {


    public static final String TABLE_NAME = "APP";
    public static final String COL_FECHA_ULT_ACT = "FECHA_ULTIMA_ACT";
    public static final String EMPTY = "-";
    public static final String COL_DESCARGA_EN_PROGRESO = "DESCARGA_EN_PROGRESO";
    public static final String COL_DESCARGA_EN_PROGRESO_SI = "true";
    public static final String COL_DESCARGA_EN_PROGRESO_NO = "false";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, CLAVE TEXT UNIQUE, VALOR TEXT)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public SQLiteDB(Context context) {
        super(context, AppConfig.DB_NAME, null, AppConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Se crea una tabla con la siguiente estructura:
        // ID    |   CLAVE                   |VALOR
        // 1     |   FECHA_ULTIMA_ACT        |20200229
        // 2     |   DESCARGA_EN_PROGRESO    |false
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    public void initColsIfEmpty(){

        if(!rowExists(COL_FECHA_ULT_ACT)){
            crearRowFechaUltAct();
        }

        if(!rowExists(COL_DESCARGA_EN_PROGRESO)){
            crearRowDescargaEnProgreso();
        }


    }

    private void crearRowFechaUltAct() {
        SQLiteDatabase writableDB = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put( "CLAVE", COL_FECHA_ULT_ACT );
            values.put( "VALOR", EMPTY );
            writableDB.insertOrThrow(TABLE_NAME, null, values);
            writableDB.close();
    }

    private void crearRowDescargaEnProgreso() {
        SQLiteDatabase writableDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( "CLAVE", COL_DESCARGA_EN_PROGRESO );
        values.put( "VALOR", COL_DESCARGA_EN_PROGRESO_NO);
        writableDB.insertOrThrow(TABLE_NAME, null, values);
        writableDB.close();
    }

    private boolean rowExists(String clave) {

        SQLiteDatabase readableDB = this.getReadableDatabase();
        String[] campos = new String[] {"VALOR"};
        String[] filtro = new String[]{clave};
        Cursor cursor = readableDB.query(TABLE_NAME, campos, "CLAVE=?", filtro, null, null, null);

        boolean res = false;
        if(cursor.moveToFirst()){
            res = true;
        }

        readableDB.close();
        return res;
    }


    /**
     * Seter del parametro "Descarga en progreso"
     *
     * @param valor
     * @return
     */
    public boolean setDescargaEnProgreso(Boolean valor){

        String val = COL_DESCARGA_EN_PROGRESO_NO;
        if(valor == true){
            val = COL_DESCARGA_EN_PROGRESO_SI;
        }

        try{

            SQLiteDatabase writableDB = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put( "VALOR", val );

            long res = writableDB.update(TABLE_NAME, values, "CLAVE = ?", new String[]{COL_DESCARGA_EN_PROGRESO});

            writableDB.close();

            return res != -1;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Devuelve true si la descarga de productos esta en progreso o false en caso contrario.
     *
     * @return
     */
    public Boolean isDescargaEnProgreso(){


        SQLiteDatabase readableDB = this.getReadableDatabase();
        String[] campos = new String[] {"VALOR"};
        String[] filtro = new String[]{COL_DESCARGA_EN_PROGRESO};
        Cursor cursor = readableDB.query(TABLE_NAME, campos, "CLAVE=?", filtro, null, null, null);

        if(cursor.moveToFirst()){
            // existe al menos un resultado

            String valor = cursor.getString(0);
            readableDB.close();

            return valor.equals(COL_DESCARGA_EN_PROGRESO_SI);
        }

        readableDB.close();
        return false;
    }

    /**
     * Devuelve la fecha de ultima actualizacion.
     *
     * @return
     */
    public Date getFechaActualizacion(){

        SQLiteDatabase readableDB = this.getReadableDatabase();
        String[] campos = new String[] {"VALOR"};
        String[] filtro = new String[]{COL_FECHA_ULT_ACT};
        Cursor cursor = readableDB.query(TABLE_NAME, campos, "CLAVE=?", filtro, null, null, null);

        Date fechaAct = null;
        if(cursor.moveToFirst()){
            // existe al menos un resultado

            String fecha = cursor.getString(0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConfig.DATE_FORMAT_FECHA_ACT);
            try {
                fechaAct = simpleDateFormat.parse(fecha);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        readableDB.close();
        return fechaAct;
    }


    /**
     * Setea el campo de fecha de actualizacion.
     *
     * @param val
     * @return
     */
    public Boolean setFechaActualizacion(String val){

        if(val == null){
            val = new SimpleDateFormat(AppConfig.DATE_FORMAT_FECHA_ACT).format(new Date());
        }

        try{

            SQLiteDatabase writableDB = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put( "VALOR", val );

            long res = writableDB.update(TABLE_NAME, values, "CLAVE = ?" , new String[]{COL_FECHA_ULT_ACT});

            writableDB.close();
            return res != -1;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Devuelve true si los productos han sido descargados en el dia actual.
     * No revisa que los archivos existan, solo verifica que la fecha almacenada en la
     * base de datos se igual al dia actual.
     *
     * @return
     */
    public boolean productosActualizados(){

        SQLiteDatabase readableDB = this.getReadableDatabase();
        String[] campos = new String[] {"VALOR"};
        String[] filtro = new String[]{COL_FECHA_ULT_ACT};
        Cursor cursor = readableDB.query(TABLE_NAME, campos, "CLAVE=?", filtro, null, null, null);

        if(cursor.moveToFirst()){

            String valor = cursor.getString(0);
            readableDB.close();
            String strFechaHoy = new SimpleDateFormat(AppConfig.DATE_FORMAT_FECHA_ACT).format(new Date());
            return valor.equals(strFechaHoy);
        }

        readableDB.close();
        return false;
    }

    /**
     * Permite vaciar el contenido de la tabla APP
     */
    public void truncarTabla(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL( "DELETE FROM " + TABLE_NAME );
        db.close();

    }

}
