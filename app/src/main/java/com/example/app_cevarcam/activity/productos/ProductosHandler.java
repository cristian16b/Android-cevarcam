package com.example.app_cevarcam.activity.productos;

import android.util.Log;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.config.AppConfig;

import java.io.File;

/**
 * Clase que contiene constantes y metodos para trabajar con los productos.
 */
public class ProductosHandler {

    public static final String TAG = "ProductosHandler";

    // Productos
    // Definir una constante unica por producto
    public static final int PROD_PRECIPITACION = 1;
    public static final int PROD_TEMPERATURA = 2;
    public static final int PROD_VIENTOS = 3;
    public static final int PROD_EVAPOT = 4;
    public static final int PROD_HS = 5;


    public static final int IMGS_IDX_START = 0;
    public static final int IMGS_IDX_END = 54;


    public static final int IMGS_SEQ_START = 3;
    public static final int IMGS_SEQ_END = 165;
    public static final int IMGS_SEQ_STEP = 3;


    /**
     * Devuelve los nombres de los archivos de las imagenes
     *
     * @return
     */
    public static String[] getNombresArchivos() {

        String[] files = new String[IMGS_IDX_END + 1];
        String fname = "";
        int last = 0;

        for (int i = IMGS_SEQ_START; i <= IMGS_SEQ_END; i += IMGS_SEQ_STEP) {

            fname = "";

            if (i < 10) {
                fname = "00";
            } else if (i < 100) {
                fname = "0";
            }

            fname += (new Integer(i)).toString() + AppConfig.IMG_FILE_EXTENSION;
            files[last++] = fname;
        }

        return files;
    }

    public static String getDirectorioImgsLocal(int cod_prod){

        switch (cod_prod){
            case ProductosHandler.PROD_TEMPERATURA:
                return AppConfig.DIRECTORIO_TEMP;
            case ProductosHandler.PROD_PRECIPITACION:
                return AppConfig.DIRECTORIO_PRECIP;
            case ProductosHandler.PROD_VIENTOS:
                return AppConfig.DIRECTORIO_VIENTO;
            case ProductosHandler.PROD_EVAPOT:
                return AppConfig.DIRECTORIO_EVAPOT;
            case ProductosHandler.PROD_HS:
                return AppConfig.DIRECTORIO_HUM_SUELO;
            default:
                return null;
        }

    }

    public static int getNext(int pos_actual) {
        if (pos_actual >= IMGS_IDX_END || pos_actual < IMGS_IDX_START) {
            return IMGS_IDX_START;
        } else {
            return pos_actual + 1;
        }
    }

    public static int getPrev(int pos_actual) {
        if (pos_actual <= IMGS_IDX_START || pos_actual > IMGS_IDX_END + 1) {
            return IMGS_IDX_END;
        } else {
            return pos_actual - 1;
        }
    }

    public static void testNext() {
        String[] archivos = getNombresArchivos();

        for (int i = -5; i < 68; i++) {
            int next = getNext(i);
            Log.i(TAG, "El NEXT de " + i + " es " + next);
            Log.i(TAG, "la imagen SIGUIENTE es: " + archivos[next]);
        }
    }

    public static void testPrev() {
        String[] archivos = getNombresArchivos();

        for (int i = -5; i < 68; i++) {
            int prev = getPrev(i);
            Log.i(TAG, "El PREV de " + i + " es " + prev);
            Log.i(TAG, "la imagen ANTERIOR es: " + archivos[prev]);
        }
    }
}
