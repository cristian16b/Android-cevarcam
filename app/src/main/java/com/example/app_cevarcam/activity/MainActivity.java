package com.example.app_cevarcam.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.app_cevarcam.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.app_cevarcam.activity.acercaDe.AcercaDeFragment;
import com.example.app_cevarcam.activity.bd.SQLiteDB;
import com.example.app_cevarcam.activity.linksInteres.LinksInteresFragment;
import com.example.app_cevarcam.activity.productos.ProductosHandler;
import com.example.app_cevarcam.activity.productos.ListadoProductosFragment;
import com.example.app_cevarcam.activity.productos.ProductosFragment;
import com.example.app_cevarcam.activity.salir.SalirFragment;
import com.example.app_cevarcam.activity.service.DownloaderService;
import com.example.app_cevarcam.activity.simulacionProducto.SimulacionProductosFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.app_cevarcam.activity.fasesLunares.FasesLunaresFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FragmentManager fm = getSupportFragmentManager();

    // creo instancias nulas de los fragments del menu
    private FasesLunaresFragment fase = null;
    private ProductosFragment productos = null;
    private ListadoProductosFragment listadoProductos = null;
    private AcercaDeFragment acercaDe = null;
    private SalirFragment salir = null;
    private SimulacionProductosFragment simulacion = null;
    private LinksInteresFragment linksInteres = null;
    private SQLiteDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_productos, R.id.nav_ajustes, R.id.nav_fases_lunares,
                R.id.nav_links_interes, R.id.nav_acerca_de, R.id.nav_salir)
                .setDrawerLayout(drawer)
                .build();

         */
        //comente nav_ajustes porque no esta definido
        /*
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_productos, R.id.nav_fases_lunares,
                R.id.nav_links_interes, R.id.nav_acerca_de, R.id.nav_salir)
                .setDrawerLayout(drawer)
                .build();

        // lo siguiente es el fragment con el listado de productos (pronosticos/perspectivas)
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


         */

        //this.deleteDatabase(AppConfig.DB_NAME);
        db = new SQLiteDB(this);
        db.initColsIfEmpty();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // muestro el primer fragment de productos o home de la app
        productos = new ProductosFragment();
        fm.beginTransaction().
                replace(R.id.nav_host_fragment,productos).
                addToBackStack(null).
                commit();


        // Inicializar el servicio de descarga
        if(false == db.productosActualizados()){
            iniciarServicioDescarga();
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            fm.popBackStack();
        }

         */

        // por esta instancia de la app, se deja por defecto que el boton volver,
        // siempre retorna al fragment de productos
        if (
                !(fm.findFragmentById(R.id.nav_host_fragment) instanceof ProductosFragment)
                        ||
                !(fm.findFragmentById(R.id.nav_host_fragment) instanceof SimulacionProductosFragment)
        ) {
            productos = new ProductosFragment();
            fm.beginTransaction().
                    replace(R.id.nav_host_fragment,productos).
                    addToBackStack(null).
                    commit();
        }
    }



    public void iniciarServicioDescarga(){

        Intent i = new Intent(this, DownloaderService.class);

//        Bundle data = new Bundle();
//        data.putSerializable(DownloaderServiceWithOpt.ARG_USUARIO, this.usuario);
//        data.putBoolean(DownloaderServiceWithOpt.ARG_APLICAR_OPT, optimizacionActivada());
        //data.put(DownloaderServiceWithOpt.ARG_WS_IMAGENES, getWsImagenesClient());
       // i.putExtras(data);
        startService(i);
    }

    public SQLiteDB getDb(){
        return db;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //Log.i("id-seleccionado",""+id);

        switch (id){
            case R.id.nav_productos:
                if (!(fm.findFragmentById(R.id.nav_host_fragment) instanceof ProductosFragment)) {
                    productos = new ProductosFragment();
                    fm.beginTransaction().
                            replace(R.id.nav_host_fragment,productos).
                            addToBackStack(null).
                            commit();
                }
                break;
            case R.id.nav_ajustes:

                break;
            case R.id.nav_fases_lunares:
                if (!(fm.findFragmentById(R.id.nav_host_fragment) instanceof FasesLunaresFragment)) {
                    fase = new FasesLunaresFragment();
                    fm.beginTransaction().
                            replace(R.id.nav_host_fragment,fase).
                            addToBackStack(null).
                            commit();
                }

                break;
            case R.id.nav_links_interes:
                if (!(fm.findFragmentById(R.id.nav_host_fragment) instanceof LinksInteresFragment)) {
                    linksInteres = new LinksInteresFragment();
                    fm.beginTransaction().
                            replace(R.id.nav_host_fragment,linksInteres).
                            addToBackStack(null).
                            commit();
                }
                break;
            case R.id.nav_acerca_de:
                if (!(fm.findFragmentById(R.id.nav_host_fragment) instanceof AcercaDeFragment)) {
                    acercaDe = new AcercaDeFragment();
                    fm.beginTransaction().
                            replace(R.id.nav_host_fragment,acercaDe).
                            addToBackStack(null).
                            commit();
                }
                break;
            case R.id.nav_salir:
                    salir = new SalirFragment();
                    fm.beginTransaction().
                            replace(R.id.nav_host_fragment,salir).
                            addToBackStack(null).
                            commit();
                break;
             default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void showLongToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public String getProdNom(int cod_prod){

        switch (cod_prod){
            case ProductosHandler.PROD_TEMPERATURA:
                return getString(R.string.prod_temperatura);
            case ProductosHandler.PROD_PRECIPITACION:
                return getString(R.string.prod_precipitacion);
            case ProductosHandler.PROD_VIENTOS:
                return getString(R.string.prod_viento);
            case ProductosHandler.PROD_EVAPOT:
                return getString(R.string.prod_evapotranspiracion);
            case ProductosHandler.PROD_HS:
                return getString(R.string.prod_humedad_suelo);
                default:
                    return "Sin titulo";
        }
    }

    public String getProdDesc(int cod_prod){

        switch (cod_prod){
            case ProductosHandler.PROD_TEMPERATURA:
                return getString(R.string.prod_temperatura_desc);
            case ProductosHandler.PROD_PRECIPITACION:
                return getString(R.string.prod_precipitacion_desc);
            case ProductosHandler.PROD_VIENTOS:
                return getString(R.string.prod_viento_desc);
            case ProductosHandler.PROD_EVAPOT:
                return getString(R.string.prod_evapotranspiracion_desc);
            case ProductosHandler.PROD_HS:
                return getString(R.string.prod_humedad_suelo_desc);
            default:
                return "-";
        }
    }
}