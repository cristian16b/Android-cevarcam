package com.example.app_cevarcam.activity.linksInteres;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.adapter.LinksInteresAdapter;

public class LinksInteresFragment extends Fragment {

    private String[] linksTitulos = {
            "Monitoreo y pronósticos Hidroclimáticos",
            "FICH",
            "UNL",
            "SMN",
            "Meteored",
            "Accuweather",
    };

    private String[] linksDescripcion = {
            "http://fich.unl.edu.ar/cevarcam-mph/login",
            "http://fich.unl.edu.ar/",
            "https://www.unl.edu.ar/",
            "https://www.smn.gob.ar/",
            "https://www.meteored.com.ar/",
            "https://www.accuweather.com/es/ar/santa-fé",
    };

    private ListView listaLinksInteres = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_links_interes, container, false);

        listaLinksInteres =(ListView) root.findViewById(R.id.list_links_interes);
        listaLinksInteres.setAdapter(new LinksInteresAdapter(getActivity(),linksTitulos,linksDescripcion));

        listaLinksInteres.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                // ref: https://codea.app/android/lanzar-un-link
                // todo permitir que se abra en la misma aplicacion
                Uri uri = Uri.parse(linksDescripcion[i]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        return root;
    }
}