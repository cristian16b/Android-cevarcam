package com.example.app_cevarcam.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app_cevarcam.R;

// referencia
// https://www.androfast.com/2017/03/como-personalizar-listas-listview-en.html

public class ProductoAdapter extends BaseAdapter {

    private String[] productosTitulo;
    private String[] productosDescripcion;
    private Context contexto;
    private static LayoutInflater inflater= null;

    public ProductoAdapter(Context context, String[]productosTitulos, String[]productosDescripciones) {

        contexto = context;
        productosTitulo = productosTitulos;
        productosDescripcion = productosDescripciones;

        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return productosTitulo.length;
    }

    @Override
    public  Object getItem(int posicion) {
        return posicion;
    }

    @Override
    public  long  getItemId(int posicion) {
        return posicion;
    }


    @Override
    public View getView(final int posicion, View convertView, ViewGroup parent) {

        View fila;
        fila = inflater.inflate(R.layout.item_list_producto, null);
        TextView tvTitulo = fila.findViewById(R.id.textViewTitulo);
        tvTitulo.setText(productosTitulo[posicion]);

        TextView tvDescripcion = fila.findViewById(R.id.textViewDescripcion);
        tvDescripcion.setText(productosDescripcion[posicion]);

        return fila;
    }

}
