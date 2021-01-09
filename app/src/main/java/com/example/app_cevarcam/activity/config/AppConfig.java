package com.example.app_cevarcam.activity.config;

public class AppConfig {

    public static final String APP_PKG = "com.example.app_cevarcam";
    public static final String APP_IMGS_BASE_DIR = "/data/data/" + APP_PKG + "/imagenes";
    public static final String DIRECTORIO_TEMP = APP_IMGS_BASE_DIR + "/temperatura";
    public static final String DIRECTORIO_PRECIP = APP_IMGS_BASE_DIR + "/precipitacion";
    public static final String DIRECTORIO_VIENTO = APP_IMGS_BASE_DIR + "/viento";
    public static final String DIRECTORIO_EVAPOT = APP_IMGS_BASE_DIR + "/evapotranspiracion";
    public static final String DIRECTORIO_HUM_SUELO = APP_IMGS_BASE_DIR + "/hsuelo";

    private static final String IMGS_SOURCE = "http://fich.unl.edu.ar/cevarcam-mph/uploads/imagenes/productos/pr";
    public static final String IMGS_SOURCE_TEMPERATURA = IMGS_SOURCE + "/temperatura/";
    public static final String IMGS_SOURCE_PRECIPITACION = IMGS_SOURCE + "/precipitacion/";
    public static final String IMGS_SOURCE_VIENTOS = IMGS_SOURCE + "/vientos/";
    public static final String IMGS_SOURCE_EVAPOT = IMGS_SOURCE + "/evapotranspiracion/";
    public static final String IMGS_SOURCE_HUM_SUELO = IMGS_SOURCE + "/hsuelo/";


    public static final String IMG_FILE_EXTENSION = ".gif";



    public static final String DB_NAME = "APP_CEVARCAM_DB";
    public static final int DB_VERSION = 1;
    public static final String DATE_FORMAT_DEFAULT = "d/M/yyyy";
    public static final String DATE_FORMAT_FECHA_ACT = "yyyyMMdd";

}
