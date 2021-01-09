package com.example.app_cevarcam.activity.productos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.MainActivity;
import com.example.app_cevarcam.activity.adapter.ProductoAdapter;
import com.example.app_cevarcam.activity.simulacionProducto.SimulacionProductosFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListadoProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListadoProductosFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private OnFragmentInteractionListener mListener;
    private MainActivity mainActivity;


    private String[] productosTitulos;

    private String[] productosDescripcion;

    private int[] productosCodigo;

    private ListView listaPronosticos = null;

    private ListadoProductosFragment listadoProductos;

    public ListadoProductosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListadoProductosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListadoProductosFragment newInstance(String param1, String param2) {
        ListadoProductosFragment fragment = new ListadoProductosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        productosTitulos = new String[]{
                mainActivity.getProdNom(ProductosHandler.PROD_TEMPERATURA),
                mainActivity.getProdNom(ProductosHandler.PROD_PRECIPITACION),
                mainActivity.getProdNom(ProductosHandler.PROD_VIENTOS),
                mainActivity.getProdNom(ProductosHandler.PROD_EVAPOT),
                mainActivity.getProdNom(ProductosHandler.PROD_HS),
        };

        productosDescripcion = new String[]{
                mainActivity.getProdDesc(ProductosHandler.PROD_TEMPERATURA),
                mainActivity.getProdDesc(ProductosHandler.PROD_PRECIPITACION),
                mainActivity.getProdDesc(ProductosHandler.PROD_VIENTOS),
                mainActivity.getProdDesc(ProductosHandler.PROD_EVAPOT),
                mainActivity.getProdDesc(ProductosHandler.PROD_HS),
        };

        productosCodigo = new int[]{
                ProductosHandler.PROD_TEMPERATURA,
                ProductosHandler.PROD_PRECIPITACION,
                ProductosHandler.PROD_VIENTOS,
                ProductosHandler.PROD_EVAPOT,
                ProductosHandler.PROD_HS,
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_listado_productos, container, false);

        listaPronosticos =(ListView) root.findViewById(R.id.list_pronosticos);
        listaPronosticos.setAdapter(new ProductoAdapter(getActivity(),productosTitulos,productosDescripcion));

        listaPronosticos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                cambiarFragmentoSimulacion(productosCodigo[i]);
            }
        });
        return root;
    }


    private void cambiarFragmentoSimulacion(int cod_prod){
        //Paso 1: Obtener la instancia del administrador de fragmentos
        FragmentManager fragmentManager = getFragmentManager();
        //seteo los parametros
        Bundle arguments = new Bundle();
        arguments.putInt( SimulacionProductosFragment.ARG_PROD_COD , cod_prod);

        SimulacionProductosFragment simulacion = new SimulacionProductosFragment();
        simulacion.setArguments(arguments);
        //Paso 2: Crear una nueva transacción
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //Paso 3: Crear un nuevo fragmento y añadirlo
        transaction.replace(R.id.fragment_container,simulacion);
        //Paso 4: Confirmar el cambio
        transaction.commit();
    }



}
