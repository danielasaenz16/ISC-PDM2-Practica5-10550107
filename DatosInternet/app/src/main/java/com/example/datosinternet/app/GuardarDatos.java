package com.example.datosinternet.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GuardarDatos extends ActionBarActivity {

    public static final String  TAG = GuardarDatos.class.getSimpleName();
    protected TextView nombre;
    protected TextView telefono;
    protected TextView hora;
    //protected TextView activo;
    String NOMBRE ="hola_mundo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_datos);

        nombre = (TextView)findViewById(R.id.txt1);
        telefono = (TextView)findViewById(R.id.txt2);
        hora=(TextView)findViewById(R.id.txt3);
        //activo=(TextView)findViewById(R.id.txt4);

        obtenerTextoInternet();
        try {SharedPreferences sharedPreferences;//falta un apuntador
            sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            //privado o publico
            String uno;
            String dos;
            String tres;

            uno = sharedPreferences.getString("uno", "");
            dos = sharedPreferences.getString("dos", "");
            tres = sharedPreferences.getString("tres", "");
            //uno = sharedPreferences.getString("uno", "");

            nombre.setText(uno);
            telefono.setText(dos);
            hora.setText(tres);
            leerMemoriaInterna();



        }
        catch (Exception e){}
    }public void guardarMemoriaInterna(View v){
        try{
            String texto =nombre.getText().toString();
            String texto2 =telefono.getText().toString();
            String texto3 =hora.getText().toString();

            FileOutputStream fileOutputStream= openFileOutput(NOMBRE,Context.MODE_PRIVATE);
            fileOutputStream.write(texto.getBytes());
            fileOutputStream.write(texto2.getBytes());
            fileOutputStream.write(texto3.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e) {}
    }
    public void leerMemoriaInterna(){
        try{
            String textoMemoria;
            FileInputStream fileInputStream = openFileInput(NOMBRE);
            fileInputStream.read();
            // byte[] buffer = new byte[fileInputStream.read()];
            //fileInputStream.read(buffer);

            BufferedReader bReader = new BufferedReader( new InputStreamReader
                    (fileInputStream,"UTF-8"),8);

            StringBuilder sBuilder = new StringBuilder();
            String line =null;
            while((line = bReader.readLine()) != null){
                sBuilder.append(line);
            }
            textoMemoria = sBuilder.toString();

            nombre.setText(textoMemoria);
            telefono.setText(textoMemoria);
            hora.setText(textoMemoria);
        }
        catch(FileNotFoundException e){}
        catch(IOException e){}
    }

    private void obtenerTextoInternet() {
        if(isNetworkAvailable()){
            GetAPI getAPI = new GetAPI();
            getAPI.execute();
        }else{
            //Toast.makeText(this, "Hola", Toast.LENGTH_LONG).show();
            mostrarAlerta();
        }
    }

    private void mostrarAlerta() {
        nombre.setText("");
        telefono.setText("");
        hora.setText("");
        //activo.setText("");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_error));
        builder.setMessage(getString(R.string.title_error_mensaje));
        builder.setPositiveButton(android.R.string.ok,null);

        AlertDialog alertDialog =builder.create();
        alertDialog.show();
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable = false;

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        else{
            Toast.makeText(this, "Sin Conexión", Toast.LENGTH_LONG);
        }


        return isAvailable;
    }

    private class GetAPI extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            //barra.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            Log.d(TAG, "Response OK");

            int responseCode = -1;
            String resultado = "";
            JSONObject jsonResponse = null;

            try{
                URL apiURL =  new URL(
                        "http://continentalrescueafrica.com/2013/test.php");

                HttpURLConnection httpConnection = (HttpURLConnection)
                        apiURL.openConnection();
                httpConnection.connect();
                responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = httpConnection.getInputStream();
                    BufferedReader bReader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);

                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    resultado = sBuilder.toString();
                    Log.d(TAG, resultado);
                    jsonResponse = new JSONObject(resultado);

                }else{
                    Log.i(TAG, "Error en el HTTP " + responseCode);
                }
            }
            catch (JSONException e){}
            catch (MalformedURLException e){}
            catch (IOException e){}
            catch (Exception e){}

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject respuesta) {
            try{
                //barra.setVisibility(View.INVISIBLE);
                //JSONObject jsonObject = respuesta.getJSONObject(0);
                nombre.setText(respuesta.getString("nombre") );
                telefono.setText( respuesta.getString("telefono"));
                hora.setText(respuesta.getString("hora"));
                //activo.setText(respuesta.getString("activo"));
            }
            catch (JSONException e){}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guardar_datos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
