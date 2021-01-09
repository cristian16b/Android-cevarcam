package com.example.app_cevarcam.activity.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by emi88 on 11/28/17.
 */
public class FileManager {

    private String taq = "FileManager";
    private boolean logMuted = false;

    private void log(String msg){
        if(!logMuted){
            Log.i(taq, msg);
        }
    }

    /**
     * Permite descargar un archivo de un servidor.
     *
     * @param urlTarget
     * @param filename
     * @param destino
     * @return
     */
    public Integer descargar(URL urlTarget, String filename, File destino ) {

        if(!destino.exists()){
            log("El directorio " + destino.getAbsolutePath() + " no existe.");
            mkdir(destino);
        }

        log("Descargando: " + urlTarget + " en: " + destino.toString() + " con nombre: " + filename);

        int c = 0;

        try {

            URLConnection con = urlTarget.openConnection();
            con.connect();

            // download the file
            InputStream input = new BufferedInputStream(urlTarget.openStream(), 8192);

            File nuevo = new File(destino, filename);
            Log.d("FileManager", "Nuevo archivo: " + nuevo.toString());
            Log.d("FileManager", "Existe?: " + (nuevo.exists() ? "Si" : "No"));
            FileOutputStream output = new FileOutputStream(nuevo);

            byte data[] = new byte[1024];
            long total = 0;

            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            c++;

            Log.d("FileManager", "Se descargaron: " + total + " bytes");

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return c;
    }



    public static File mkdir(String absolutePath){
        File dir = new File( absolutePath );
        if(!dir.exists()){
            if(!dir.getParentFile().exists()){
                dir.getParentFile().mkdir();
            }
            dir.mkdir();
        }
        return dir;
    }

    public static boolean mkdir(File dir){
        if(!dir.exists()){
            if(!dir.getParentFile().exists()){
                dir.getParentFile().mkdir();
            }
            dir.mkdir();
            return  true;
        }

        return false;
    }


}
