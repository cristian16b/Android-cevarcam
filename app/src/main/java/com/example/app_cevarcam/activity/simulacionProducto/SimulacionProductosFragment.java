package com.example.app_cevarcam.activity.simulacionProducto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.MainActivity;
import com.example.app_cevarcam.activity.config.AppConfig;
import com.example.app_cevarcam.activity.productos.ProductosHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SimulacionProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimulacionProductosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PROD_COD = "cod_producto";
    private static final String LOG_TAG = "SIMULACION";

    /**
     * Codigo del producto seleccionado
     */
    private Integer codProducto;
    private String pathImagenes;


    // previsualizacion de los productos
    private ImageView ivPrevisualizacionProducto;

    private String[] archivos;
    private volatile int archivos_pos_actual;
    private volatile boolean img_mostrada = false;


    private volatile boolean paused = false;
    private volatile int delay;

    // botones anterior,play,siguiente
    private Button buttonReproducir;
    private Button buttonAnterior;
    private Button buttonSiguiente;
    private Button buttonPausa;
    private Button buttonStop;

    // fecha/hora de ultima actualizacion
    private TextView fechaHoraUltimaActualizacion;

    // fecha/hora imagen visualizada
    private TextView fechaHoraVisualizacion;

    private TextView tvTitulo;
    private TextView tvSubtitulo;

    //private OnFragmentInteractionListener mListener;

    private MainActivity mainActivity;

    private AnimRunnable runnableAnim;


    public SimulacionProductosFragment() {
        //posicionActual = primerNumero;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SimulacionProductosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SimulacionProductosFragment newInstance(String param1) {
        SimulacionProductosFragment fragment = new SimulacionProductosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROD_COD, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            codProducto = getArguments().getInt(ARG_PROD_COD);
        }

        if(codProducto == 0){
            throw new IllegalArgumentException("El codigo de producto no corresponde a algun producto existente.");
        }

        pathImagenes = ProductosHandler.getDirectorioImgsLocal(codProducto);
        this.mainActivity = (MainActivity) this.getActivity();

        this.archivos = ProductosHandler.getNombresArchivos();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simulacion_productos, container, false);

        ivPrevisualizacionProducto = view.findViewById(R.id.img_previsualizacion);

        //Titulo
        this.tvTitulo = view.findViewById(R.id.titulo);
        tvTitulo.setText( mainActivity.getProdNom(codProducto) );

        //Subtitulo
        this.tvSubtitulo = view.findViewById(R.id.subtitulo);
        actualizarSubtitulo();

        //fechaHoraUltimaActualizacion = (TextView) view.findViewById(R.id.text_fecha_hora_ultima);
        fechaHoraVisualizacion = (TextView) view.findViewById(R.id.text_fecha_hora);

        // provisorio
        //fechaHoraVisualizacion.setText(imageUrl);
        //Date hoy = new Date();
        //fechaHoraUltimaActualizacion.setText(new SimpleDateFormat("dd/MM/yyyy").format(hoy));
        fechaHoraVisualizacion.setText("3 Hs AM");

        //Buscar y setear botones:
        setBotones(view);


        this.archivos_pos_actual = 0;
        if(false == isDescargaEnProgreso()){
            updateView();
        }

        return view;
    }

    /**
     * Busca los botones en la interfaz y les setea sus respectivos eventos
     *
     * @param view
     */
    private void setBotones(View view){
        // Boton reproducir
        buttonReproducir = (Button) view.findViewById(R.id.boton_play);
        buttonReproducir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                play();
            }
        });

        // Boton pausa:
        buttonPausa = (Button) view.findViewById(R.id.boton_pause);
        buttonPausa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pause();
            }
        });

        // Boton pausa:
        buttonStop = (Button) view.findViewById(R.id.boton_stop);
        buttonStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stop();
            }
        });


        // Boton anterior o imagen previa:
        buttonAnterior = (Button) view.findViewById(R.id.boton_anterior);
        buttonAnterior.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prev();
            }
        });

        // Boton siguiente
        buttonSiguiente = (Button) view.findViewById(R.id.boton_siguiente);
        buttonSiguiente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                next();
            }
        });
    }

    public void actualizarSubtitulo(){
        if(tvSubtitulo == null){
            return;
        }

        Date fechaUltAct = mainActivity.getDb().getFechaActualizacion();
        if(fechaUltAct != null){
            String strFechaUltAct = new SimpleDateFormat(AppConfig.DATE_FORMAT_DEFAULT).format( fechaUltAct );
            tvSubtitulo.setText( "Actualizado al " +  strFechaUltAct);
        }else{
            tvSubtitulo.setText( "Descargando imágenes..." );
        }
    }



    public boolean isDescargaEnProgreso(){
        if(!mainActivity.getDb().isDescargaEnProgreso()){
            //por las dudas se actualiza la fecha de actualizacion del producto
            actualizarSubtitulo();
            return false;
        }

        mainActivity.showLongToast("Las imágenes estan siendo descargadas. No es posible reproducir la animación hasta que la descarga haya finalizado. Intente nuevamente en unos minutos.");
        return true;
    }

    /**
     * Actualiza la vista para la imagen actual mostrada.
     *
     */
    public void updateView(){

        String pathImgActual = pathImagenes + "/" + archivos[archivos_pos_actual];
        File img = new File( pathImgActual );
        //Log.i(LOG_TAG, "Voy a mostrar: " + img.getAbsolutePath());
        if(img.exists()){
            Log.i(LOG_TAG, "Voy a mostrar: " + img.getAbsolutePath());

            Bitmap myBitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            ivPrevisualizacionProducto.setImageBitmap(myBitmap);
        }else{
            Log.i(LOG_TAG, "No existe la imagen: " + img.getAbsolutePath());
        }

    }




    // ANIMACION ...........................................................................................................
    // =====================================================================================================================

    private class AnimRunnable implements Runnable {


        private Handler handler;

        public AnimRunnable(Handler handler) {
            //this.stopped = false;
            this.handler = handler;
        }

        public void run() {

            //this.stopped = false;
            delay = 800;


            //String img_next = archivos[ ProductosHandler.getNext(archivos_pos_actual) ];


            if (!paused) {

                String img_actual = archivos[archivos_pos_actual];
                //mainActivity.showToast( img_actual );

                // Si la imagen actual no fue mostrada, entonces no muestro la siguiente todavia:
                archivos_pos_actual = ProductosHandler.getNext(archivos_pos_actual);
                updateView();

                //activity.imgActual = iterator.next();

                //log("Cargando:" + activity.imgActual);
                //setOverlay(activity.imgActual);
                //currentImgTxt.setText(activity.imgActual);

                // Mostrar label:
                //currentImgTxt.setVisibility(View.VISIBLE);
                //Calendar c = activity.convertirAFecha(activity.imgActual);
               // currentImgTxt.setText(dateFormatter2.format(c.getTime()));

                //Date validTime = ProductosHandler.getFechaYHoraImg(cod_prod, currentImg);
                //String msg = simpleDateFormat.format(validTime.getTime());

                //nom img:
                //txt1.setText(currentImg);
                //valid time:
                //txt3.setText(simpleDateFormat.format(ProductosHandler.getTiempoValidez(cod_prod, currentImg).getTime()));
                //time zone:
                //txt4.setText( simpleDateFormat.getTimeZone().getDisplayName() );

                //int p = 5;
                //tvCurrentImg.setText( msg );
                fechaHoraVisualizacion.setText(img_actual);
                //tvCurrentImg.setText("Img actual: " + currentImg);
                //Log.d("Actual:", activity.imgActual);

                //if (archivos_pos_actual < 56) {
                    handler.postDelayed(this, delay);
                    //hideOverlay(activity.imgActual);
               // }else {
                //    archivos_pos_actual = 0;
               //     fabPausa.setVisibility(View.INVISIBLE);
               //     fabPlay.setVisibility(View.VISIBLE);
               //     stopped = true;
               // }

            }

        }

        /*public boolean isStopped() {
            return stopped;
        }

        public void setStop(boolean stopped) {
            this.stopped = stopped;
        }*/
    };


    public void play(){
        if(false == isDescargaEnProgreso()) {
            paused = false;

            buttonReproducir.setVisibility(View.GONE);
            buttonPausa.setVisibility(View.VISIBLE);

            final Handler handler = new Handler();

            runnableAnim = new AnimRunnable(handler);
            // disparar por primera vez:
            handler.post(runnableAnim);
        }
    }

    /**
     * Detiene la animacion
     */
    public void stop(){
        if(false == isDescargaEnProgreso()) {

            archivos_pos_actual = 0;
            paused = true;

            // Actualizar vista:
            buttonPausa.setVisibility(View.GONE);
            buttonReproducir.setVisibility(View.VISIBLE);
            fechaHoraVisualizacion.setText(archivos[archivos_pos_actual]);
        }
    }

    public void pause(){
        if(false == isDescargaEnProgreso()) {
            paused = !paused;

            if (paused) {
                buttonPausa.setVisibility(View.GONE);
                buttonReproducir.setVisibility(View.VISIBLE);
            }
        }
    }

    public void prev(){
        if(false == isDescargaEnProgreso()) {
            int img_prev = ProductosHandler.getPrev(archivos_pos_actual);
            archivos_pos_actual = img_prev;
            fechaHoraVisualizacion.setText( archivos[archivos_pos_actual] );
            updateView();
        }
    }

    public void next(){
        if(false == isDescargaEnProgreso()) {
            int img_next = ProductosHandler.getNext(archivos_pos_actual);
            archivos_pos_actual = img_next;
            fechaHoraVisualizacion.setText( archivos[archivos_pos_actual] );
            updateView();
        }
    }



}
