package com.example.app_cevarcam.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.app_cevarcam.R;

public class LinksInteresAdapter extends BaseAdapter {

    private String[] linksTitulo;
    private String[] linksDescripcion;
    private Context contexto;
    private static LayoutInflater inflater= null;


    public LinksInteresAdapter(Context context, String[]linksInteresTitulo, String[]linksInteresDescripcion) {
        linksTitulo = linksInteresTitulo;
        contexto = context;
        linksDescripcion = linksInteresDescripcion;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return linksTitulo.length;
    }
    @Override
    public  Object getItem(int posicion) {
        return posicion;
    }
    @Override
    public  long  getItemId(int posicion) {
        return posicion;
    }

    public class Holder
    {
        TextView titulo;
        TextView descripcion;
    }
    @Override
    public View getView(final int posicion, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LinksInteresAdapter.Holder holder= new LinksInteresAdapter.Holder();
        View fila;
        fila = inflater.inflate(R.layout.item_list_producto, null);
        holder.titulo=(TextView) fila.findViewById(R.id.textViewTitulo);
        holder.descripcion=(TextView) fila.findViewById(R.id.textViewDescripcion);
        holder.titulo.setText(linksTitulo[posicion]);
        holder.descripcion.setText(linksDescripcion[posicion]);
        return fila;
    }
}
