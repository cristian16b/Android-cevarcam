package com.example.app_cevarcam.activity.fasesLunares;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.adapter.FasesLunaresAdapter;
import com.example.app_cevarcam.activity.productos.ProductosFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FasesLunaresFragment extends Fragment {

    // arrays dinamicos
    private ArrayList<String> fasesTitulos;
    private ArrayList<String> fasesDescripcion;

    //arrays de meses
    private String[] listadoMeses = {
            "--- Ver todas ---",
            "Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto",
            "Septiembre","Octubre","Noviembre","Diciembre"
    };

    //componetes del fragment
    private ListView listaFasesLunares = null;
    private ProgressBar barraProgreso;
    private FasesLunaresAdapter adapter;
    private Spinner spinnerMeses;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fases_lunares, container, false);

        listaFasesLunares =(ListView) view.findViewById(R.id.list_fases_lunares);

        //inicializo la barra de progreso
        barraProgreso = (ProgressBar) view.findViewById(R.id.progressBar);

        //inicializo los array
        fasesTitulos = new ArrayList<String>();
        fasesDescripcion = new ArrayList<String>();

        //inicilizo el spinner de meses
        spinnerMeses = (Spinner) view.findViewById(R.id.spinner_meses);
        spinnerMeses.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.item_spinner,listadoMeses));

        //inicializo el adapter
        // ref: https://androidcource.blogspot.com/2017/09/okhttp-with-listview-json-example-in.html?m=1&fbclid=IwAR3A2Lpc0BzbIhtxdjqc7tlHsYojw2qrT03LjHap6ibV8uaKUZxUvSBbgik
        adapter = new FasesLunaresAdapter(getActivity(),fasesTitulos,fasesDescripcion);
        listaFasesLunares.setAdapter(adapter);

        //hago la peticion a la api
        try
        {
            getHttpResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //eventos del spinner
        spinnerMeses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //Toast.makeText(getActivity(),spinnerMeses.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                adapter.filtrarPorMes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        return view;
    }

    public void getHttpResponse() throws IOException {

        String url = getString(R.string.api_fases_lunares) +  Calendar.getInstance().get(Calendar.YEAR);
        Log.i("url",url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            // tener en cuenta
            // https://stackoverflow.com/questions/36786132/okhttp-response-status-code-in-onfailure-method
            // https://github.com/emiliano-sangoi/app-turnos/blob/master/app/src/main/java/com/example/emiliano/appturnos/backend/APITurnosManager.java

            @Override
            public void onFailure(Call call, IOException e) {
                //String mMessage = e.getMessage().toString();
                //Log.i("ONFAILURE","");
                barraProgreso.setVisibility(View.INVISIBLE);
                //cambio fragment
                volverMainActivity();
                mostrarErrores(getString(R.string.msg_error_conexion));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try
                {
                    //Si la api retorna un 200
                    if(response.isSuccessful()){
                        //REF: https://howtodoinjava.com/gson/gson-parse-json-array/
                        Gson gson = new Gson();

                        String responseData = Objects.requireNonNull(response.body()).string();
                        //JsonResponse[] listaJson = gson.fromJson(responseData,JsonResponse[].class);

                        //ref: https://stackoverflow.com/questions/18575757/how-to-sort-gson-array-based-on-a-key
                        Type listType = new TypeToken<List<JsonResponse>>() {}.getType();
                        List<JsonResponse> listaJson = new Gson().fromJson(responseData, listType);

                        //llamo al comparador y orderno el listado segun la fecha y hora
                        Collections.sort(listaJson, new MyComparator());

                        for(JsonResponse elemento : listaJson) {
                            // convierto la fecha y hora
                            Log.i("fase",elemento.getNombre());
                            Long valor = Long.parseLong(elemento.getFechaHora());
                            Long timestamp = (long) valor * 1000;
                            Date time = new Date( timestamp );
                            String timeFormateado = new SimpleDateFormat("dd/MM/yyyy").format(time);
                            // guardo en los arrays
                            fasesTitulos.add(timeFormateado);
                            fasesDescripcion.add(elemento.getNombre());
                        }

                        //ref: https://stackoverflow.com/questions/44318549/how-to-post-back-the-data-from-an-okhttp-async-thread-to-the-arrayadapter-in-mai
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //oculto la barra y muestro el spinner
                                barraProgreso.setVisibility(View.INVISIBLE);
                                spinnerMeses.setVisibility(View.VISIBLE);
                                //seteo el adapter y muestro
                                adapter = new FasesLunaresAdapter(getActivity(),fasesTitulos,fasesDescripcion);
                                listaFasesLunares.setAdapter(adapter);
                            }
                        });

                        return;
                    }
                    else
                    {
                        mostrarErrores(getString(R.string.msg_errores_login_default));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class JsonResponse{
        private String fechaHora;
        private String nombre;

        public JsonResponse(String fhora,String nomb){
            fechaHora = fhora;
            nombre = nomb;
        }

        public String getFechaHora(){ return fechaHora;}
        public String getNombre(){return  nombre;}
    }

    public class MyComparator implements Comparator<JsonResponse> {
        @Override
        public int compare(JsonResponse o1, JsonResponse o2) {
            return o1.getFechaHora().compareTo(o2.getFechaHora());
        }
    }

    // muestro un mensaje de error que se le pasa como parametro.
    private void mostrarErrores(final String errorMensaje)
    {
        // Run view-related code back on the main thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //oculto la barra y muestro el spinner
                barraProgreso.setVisibility(View.INVISIBLE);
                //cambio fragment
                volverMainActivity();
                //muestro error
                Toast.makeText(getActivity(),errorMensaje,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void volverMainActivity(){
        //Paso 1: Obtener la instancia del administrador de fragmentos
        FragmentManager fragmentManager = getFragmentManager();
        //Paso 2: Crear una nueva transacción
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //Paso 3: Crear un nuevo fragmento y añadirlo
        transaction.add(R.id.nav_host_fragment,new ProductosFragment());
        transaction.addToBackStack(null);
        //Paso 4: Confirmar el cambio
        transaction.commit();
    }
}