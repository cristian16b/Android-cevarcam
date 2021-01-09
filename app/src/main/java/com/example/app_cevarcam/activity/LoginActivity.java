package com.example.app_cevarcam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_cevarcam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// fuente sobre okhttp
// https://guides.codepath.com/android/Using-OkHttp#overview

public class LoginActivity extends AppCompatActivity {

    private EditText nombreUsuario;
    private EditText constraseniaUsuario;
    private ProgressBar barraProgreso;
    private Button botonIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicialiso los edittext
        nombreUsuario=(EditText)findViewById(R.id.et_usuario);
        constraseniaUsuario=(EditText)findViewById(R.id.et_pwd);

        //inicializo la barra de progreso
        barraProgreso = (ProgressBar) findViewById(R.id.progressBar);

        //inicializo el boton ingresar
        botonIngresar = (Button) findViewById(R.id.btn_loguear);
    }

    // funcion que recibe el usuario/pass ingresada y valida contra la api si es un usuario registrado
    public void loguearUsuario(View view) throws IOException {
        String nombreUsuario=this.nombreUsuario.getText().toString();
        String constraseniaUsuario=this.constraseniaUsuario.getText().toString();

        if(nombreUsuario.length() == 0 || constraseniaUsuario.length() == 0 ){
            Toast.makeText(this,R.string.msg_usuario_y_pwd_no_puede_no_vacio,Toast.LENGTH_LONG).show();
            return;
        }

        if(this.nombreUsuario.getError() != null || this.constraseniaUsuario.getError() != null){
            Toast.makeText(this, R.string.msg_errores_en_form_login,Toast.LENGTH_LONG ).show();
            return;
        }

        // desabilito los botones
        mostrarOcultarBotones(false);

        // consulto contra la api
        postHttpResponse(nombreUsuario,constraseniaUsuario);
    }

    // funcion que resetea los campos de usuario/pass en la actividad del login
    public void resetearUsuario(View view) {
        nombreUsuario.setText("");
        constraseniaUsuario.setText("");
    }

    public void postHttpResponse(String nombreUsuario,String contraseniaUsuario) throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("username", nombreUsuario)
                .add("password", contraseniaUsuario).build();
        /*
        RequestBody formBody = new FormBody.Builder()
                .add("username", "cbudzicz")
                .add("password", "=H;Fa3Qg<cy?e+Pk").build();

         */

        Request request = new Request.Builder()
                .url(getString(R.string.api_login))
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            // tener en cuenta
            // https://stackoverflow.com/questions/36786132/okhttp-response-status-code-in-onfailure-method
            // https://github.com/emiliano-sangoi/app-turnos/blob/master/app/src/main/java/com/example/emiliano/appturnos/backend/APITurnosManager.java

            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.i("ONFAILURE","");
                mostrarErrores(getString(R.string.msg_error_conexion));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try
                {
                    // Si la api retorna un 200
                    if(response.isSuccessful()){
                        String responseData = response.body().string();
                        Log.i("ONRESPONSE",responseData);

                        // abro el main activity con el listado de productos
                        abrirMainActivity();
                    }
                    else
                    {
                        switch(response.code()){
                            case 401: //unauthorized
                                mostrarErrores(getString(R.string.msg_errores_login_invalido));
                                break;
                            case 403: //unauthorized
                                mostrarErrores(getString(R.string.msg_errores_login_invalido));
                                break;
                            default:
                                mostrarErrores(getString(R.string.msg_errores_login_default));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // si el usuario/contraseña corresponde a un usuario registrado
    // se abre el mainActivity
    private void abrirMainActivity()
    {
        //1
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
    }

    // muestro un mensaje de error que se le pasa como parametro.
    private void mostrarErrores(final String errorMensaje)
    {
        // Run view-related code back on the main thread
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,errorMensaje,
                        Toast.LENGTH_LONG).show();

                // muestro los botones
                mostrarOcultarBotones(true);
            }
        });
    }

    // funcion para ocultar botones/mostrarlos
    private void mostrarOcultarBotones(Boolean bandera){
        Button loguear = (Button) findViewById(R.id.btn_loguear);
        // Log.i("bandera", String.valueOf(bandera));
        if(bandera == false){
            // oculto la barra de progreso
            this.barraProgreso.setVisibility(ProgressBar.VISIBLE);
            botonIngresar.setVisibility(View.INVISIBLE);
        }
        else {
            // oculto la barra de progreso
            this.barraProgreso.setVisibility(ProgressBar.INVISIBLE);
            botonIngresar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //todo si esta logueado no deberia poder volver al login
        // salvo que precione el item "salir" del menu lateral
    }
}
