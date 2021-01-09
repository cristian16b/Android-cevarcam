package com.example.app_cevarcam.activity.productos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_cevarcam.R;

// referencia sobre el listview
// https://www.androfast.com/2017/03/como-personalizar-listas-listview-en.html

public class ProductosFragment extends Fragment {



    public ProductosFragment(){
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_productos, container, false);

        // ref. https://developer.android.com/training/basics/fragments/fragment-ui
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (root.findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return root;
            }

            // Create a new Fragment to be placed in the activity layout
            ListadoProductosFragment firstFragment = new ListadoProductosFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            // firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout

            //Paso 1: Obtener la instancia del administrador de fragmentos
            FragmentManager fragmentManager = getFragmentManager();
            //Paso 2: Crear una nueva transacción
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //Paso 3: Crear un nuevo fragmento y añadirlo
            transaction.add(R.id.fragment_container,firstFragment);
            transaction.addToBackStack(null);
            //Paso 4: Confirmar el cambio
            transaction.commit();
        }
        return root;
    }
}