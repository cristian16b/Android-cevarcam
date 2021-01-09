package com.example.app_cevarcam.activity.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.app_cevarcam.activity.bd.SQLiteDB;
import com.example.app_cevarcam.activity.config.AppConfig;
import com.example.app_cevarcam.activity.util.FileManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


//        import android.app.IntentService;
//        import android.app.Notification;
//        import android.app.NotificationManager;
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.support.annotation.Nullable;
//        import android.util.ArrayMap;
//        import android.util.Log;
//        import android.widget.Toast;
//
//        import com.google.android.gms.maps.model.LatLng;
//
//        import java.io.File;
//        import java.io.FileNotFoundException;
//        import java.net.MalformedURLException;
//        import java.net.URL;
//        import java.text.ParseException;
//        import java.text.SimpleDateFormat;
//        import java.util.ArrayList;
//        import java.util.Date;
//        import java.util.Map;
//        import java.util.Scanner;
//
//        import apps.sangoi.emiliano.cevarcam.R;
//        import apps.sangoi.emiliano.cevarcam.config.AppConfig;
//        import apps.sangoi.emiliano.cevarcam.config.ProductosConfig;
//        import apps.sangoi.emiliano.cevarcam.database.CircularCentradaConfig;
//        import apps.sangoi.emiliano.cevarcam.database.ConfigDBHandler;
//        import apps.sangoi.emiliano.cevarcam.database.DBManager;
//        import apps.sangoi.emiliano.cevarcam.database.ItemDescarga;
//        import apps.sangoi.emiliano.cevarcam.database.ItemsDescargaDBHandler;
//        import apps.sangoi.emiliano.cevarcam.database.OptimizacionConfig;
//        import apps.sangoi.emiliano.cevarcam.database.Parametro;
//        import apps.sangoi.emiliano.cevarcam.database.ProductoTipoOpt;
//        import apps.sangoi.emiliano.cevarcam.database.ProductoTipoOptDBHandler;
//        import apps.sangoi.emiliano.cevarcam.database.TiposOptimizacionDBHandler;
//        import apps.sangoi.emiliano.cevarcam.meteo.ProductoHandler;
//        import apps.sangoi.emiliano.cevarcam.meteo.ProductosHandler;
//        import apps.sangoi.emiliano.cevarcam.spm.Producto;
//        import apps.sangoi.emiliano.cevarcam.spm.Usuario;
//        import apps.sangoi.emiliano.cevarcam.util.Util;

/**
 * Este servicio se encarga de la descarga en segundo plano de las imagenes.
 * <p>
 * Links de interes:
 * - https://proandroiddev.com/intentservice-and-resultreceiver-70de71e5e40a
 */
public class DownloaderService extends IntentService {

    public final static String ARG_USUARIO = "u";
    public final static String ARG_APLICAR_OPT = "o";
    public final static String ARG_WS_IMAGENES = "i";

    protected Notification notification;
    public final int DESCARGANDO_NOT = 4;
//    private int i;
//    private String[] files;
//    private DBManager dbManager;
//    private ConfigDBHandler configDBHandler;
//    private ItemsDescargaDBHandler itemsDescargaDBHandler;
//    private SimpleDateFormat simpleDateFormat;
      private Boolean descargaFinalizada;
//    private NotificationManager notificationManager;
//    private final int FREC_CHECKEO_NUEVA_VERS = 12;
//    private Float progreso;
//    private Float deltaProgreso;
//    private Boolean aplicarOpt;
//    private Notification.Builder notificationBuilder;
//    private ProductoTipoOptDBHandler productoTipoOptDBHandler;
//    private ArrayMap<Integer, Producto> repoProductos;
//    private ArrayMap<Integer, OptimizacionConfig> repoProdConfig;
//    private ArrayMap<Integer, Integer[]> repoBoundaries;
//    private ArrayMap<Integer, ProductoTipoOpt> repoProductoTipoOpt;
    //private Usuario usuario;
    private boolean muted;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private FileManager fileManager;
    private ArrayList<String> colaDescarga;
    private File dirDestinoImgsTemp;
    private File dirDestinoImgsPrecip;
    private File dirDestinoImgsVientos;
    private File dirDestinoImgsEvapot;
    private File dirDestinoImgsHumSuelo;
    private SQLiteDB dbLink;

    public static final String LOG_TAG = "DownloaderService";

    public DownloaderService() {

        super(LOG_TAG);

        this.fileManager = new FileManager();

        //Crear directorio de imagenes si no existen
        this.dirDestinoImgsTemp = new File(AppConfig.DIRECTORIO_TEMP);
        this.dirDestinoImgsTemp.mkdir();

        this.dirDestinoImgsPrecip = new File(AppConfig.DIRECTORIO_PRECIP);
        this.dirDestinoImgsPrecip.mkdir();

        this.dirDestinoImgsVientos = new File(AppConfig.DIRECTORIO_VIENTO);
        this.dirDestinoImgsVientos.mkdir();

        this.dirDestinoImgsEvapot = new File(AppConfig.DIRECTORIO_EVAPOT);
        this.dirDestinoImgsEvapot.mkdir();

        this.dirDestinoImgsHumSuelo = new File(AppConfig.DIRECTORIO_HUM_SUELO);
        this.dirDestinoImgsHumSuelo.mkdir();


        this.colaDescarga = new ArrayList<String>();


        dbLink = new SQLiteDB(this);

    }


    private void log(String msg){
        if(!muted){
            Log.i(LOG_TAG, msg);
        }
    }


    /**
     * Descarga los archivos teniendo en cuenta las configuraciones de optimizacion.
     *
     * @param intent
     */
    public void iniciarDescarga(Intent intent) {

        //empezar descarga ...

        //notificacion:
        notificationBuilder =
                new Notification.Builder(getApplicationContext())
                        .setContentTitle("Descargando imagenes")
                        .setContentText("Descargando ...")
                      //  .setSmallIcon(R.drawable.ic_baseline_done_24px)
                        .setAutoCancel(true)
                        .setProgress(100, 0, false)
                        // .setContentIntent(pendingIntent)
                        .setTicker("Iniciando descarga...");
        notification = notificationBuilder.build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        startForeground(DESCARGANDO_NOT, notification);

        //Log.i("DOWNLOAD SERVICE", "SOY EL SERVICIO!!!!");

        this.descargaFinalizada = false;


        this.descargarImagenes();

    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Toast.makeText(getApplicationContext(), "Descargando ...", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Descargando ...");
        iniciarDescarga(intent);


        Handler handler = new Handler(getMainLooper());
        //final boolean e = error;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (descargaFinalizada) {
                    Toast.makeText(getApplicationContext(), "Imagenes descargadas !!!", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Error al descargar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(DESCARGANDO_NOT);
        super.onDestroy();
    }

    String path = "";


    /**
     * Funcion que genera el listado de imagenes a descargar
     *
     * @return
     */
    public void descargarImagenes(){

        //Vaciar la fecha de actualizacion:
        dbLink.setFechaActualizacion(SQLiteDB.EMPTY);

        //Indicar que hay descarga en progreso:
        dbLink.setDescargaEnProgreso(true);

        int c = 0;

        for(int i = 3; i <= 165 ; i+=3){

            String fname = "";

            if(i<10){
                fname = "00";
            }else if(i<100){
                fname = "0";
            }

            URL url = null;
            fname += (new Integer(i)).toString() + AppConfig.IMG_FILE_EXTENSION;

            // ===================================================================================================
            // Temperatura
            path = AppConfig.IMGS_SOURCE_TEMPERATURA + fname;

            try {
                url = new URL( path );
                this.fileManager.descargar(url, fname, this.dirDestinoImgsTemp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // ===================================================================================================
            // Precipitacion
            path = AppConfig.IMGS_SOURCE_PRECIPITACION + fname;
            try {
                url = new URL( path );
                this.fileManager.descargar(url, fname, this.dirDestinoImgsPrecip);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // ===================================================================================================
            // Vientos
            path = AppConfig.IMGS_SOURCE_VIENTOS + fname;
            try {
                url = new URL( path );
                this.fileManager.descargar(url, fname, this.dirDestinoImgsVientos);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // ===================================================================================================
            // Evapotranspiracion
            path = AppConfig.IMGS_SOURCE_EVAPOT + fname;
            try {
                url = new URL( path );
                this.fileManager.descargar(url, fname, this.dirDestinoImgsEvapot);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // ===================================================================================================
            // Humedad de suelo
            path = AppConfig.IMGS_SOURCE_HUM_SUELO + fname;
            try {
                url = new URL( path );
                this.fileManager.descargar(url, fname, this.dirDestinoImgsHumSuelo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            c++;
            //files[last++] = fname;
        }

        //indicar que no hay descarga en progreso:
        dbLink.setDescargaEnProgreso(false);

        //Actualizar la fecha de descarga al dia actual:
        dbLink.setFechaActualizacion(null);

        Log.i(LOG_TAG, "Se descargaron: " + c + " imagenes por producto");

    }





}