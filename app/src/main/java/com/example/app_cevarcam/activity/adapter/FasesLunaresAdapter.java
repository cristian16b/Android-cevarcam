package com.example.app_cevarcam.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_cevarcam.R;

import java.util.ArrayList;

public class FasesLunaresAdapter extends BaseAdapter {

    // arrays dinamicos
    private ArrayList<String> fasesTitulos;
    private ArrayList<String> fasesDescripcion;
    private ArrayList<String> fasesTitulosCopia  = new ArrayList<>();;
    private ArrayList<String> fasesDescripcionCopia  = new ArrayList<>();;
    private Context contexto;
    private static LayoutInflater inflater= null;


    public FasesLunaresAdapter(Context context, ArrayList<String> titulos, ArrayList<String> descripciones) {
        fasesTitulos = titulos;
        contexto = context;
        fasesDescripcion = descripciones;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
        fasesTitulosCopia.addAll(fasesTitulos);
        fasesDescripcionCopia.addAll(fasesDescripcion);
    }

    @Override
    public int getCount(){
        return fasesTitulos.size();
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
        ImageView img;
    }
    @Override
    public View getView(final int posicion, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        FasesLunaresAdapter.Holder holder= new FasesLunaresAdapter.Holder();
        View fila;
        fila = inflater.inflate(R.layout.item_list_fase_lunar, null);
        holder.titulo=(TextView) fila.findViewById(R.id.textViewTitulo);
        holder.descripcion=(TextView) fila.findViewById(R.id.textViewDescripcion);
        holder.titulo.setText(fasesTitulos.get(posicion));
        holder.descripcion.setText(fasesDescripcion.get(posicion));

        String fase = fasesDescripcion.get(posicion);
        holder.img = (ImageView) fila.findViewById(R.id.imageViewFase);
        if(fase.equals("CRECIENTE")){
            holder.img.setImageResource(R.drawable.luna_creciente);
        } else if(fase.equals("LLENA")){
            holder.img.setImageResource(R.drawable.luna_llena);
        } else if(fase.equals("MENGUANTE")){
            holder.img.setImageResource(R.drawable.luna_menguante);
        } else {
            holder.img.setImageResource(R.drawable.luna_nueva);
        }
        return fila;
    }

    public void filtrarPorMes(int pos){
        //Log.i("mes",mesSeleccionado + " " + fasesTitulos.size());

        if(pos == 0){
            fasesTitulos.addAll(fasesTitulosCopia);
            fasesDescripcion.addAll(fasesDescripcionCopia);
        }
        else {
            fasesTitulos.clear();
            fasesDescripcion.clear();

            String mes;
            String mesSeleccionado;
            if(pos < 10){
                mesSeleccionado = "0" + pos;
            }else {
                mesSeleccionado = ""  + pos;
            }

            //Log.i("itemListado",mesSeleccionado);

            int index = 0;
            for (String item: fasesTitulosCopia) {

                mes = item.substring(3,5);
                //Log.i("itemListado",mes);

                if (mes.equals(mesSeleccionado))
                {
                    fasesTitulos.add(item);
                    fasesDescripcion.add(fasesDescripcionCopia.get(index));
                    Log.i("itemListado",item);
                }
                index = index + 1;
            }
        }
        notifyDataSetChanged();
    }
}
