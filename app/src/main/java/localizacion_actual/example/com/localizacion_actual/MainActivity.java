package localizacion_actual.example.com.localizacion_actual;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.location.Location;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    //se crean las variables que se necesitaran  y se implementa dos interface a la clase
    protected static final String TAG = "Localización actual";
    TextView longitudR, latitudR,direccionR;
    protected GoogleApiClient gac;
    protected Location ultima_localizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //se vinculan las variables
        latitudR = (TextView) findViewById(R.id.latitudR);
        longitudR = (TextView) findViewById(R.id.longitudR);
        direccionR = (TextView)findViewById(R.id.direccionR);
        //se llama un metodo
        buildGoogleApiClient();

    }

    //este fue el metodo que llamamos hah este es void y esta sincronizado
    protected synchronized void buildGoogleApiClient () {
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();//se crea todo el gooleapiclient con sus respectivos listener y callbacks se pone this porque estan en esta clase como interfaces
    }



    @Override
    protected void onStart() {
        super.onStart();
        gac.connect(); //se inicia el googleapiclient
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(gac.isConnected()) {
            gac.disconnect(); //si la aplicacion se detiene y estaba conectado se detiene para economizar memoria
        }
    }
    //se agregan todos los metodos override de las dos interfaces

   //aqui es cuando esta conectado el GoogleApiClient en este caso llamado gac
    @Override
    public void onConnected(Bundle bundle) {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH); //se crea la variable geocoder

        ultima_localizacion = LocationServices.FusedLocationApi.getLastLocation(gac); //se trae la ultima localizacion y  se asigna a la variable ultima_localizacion que es de tipo location
        if(ultima_localizacion != null ){ // si existe una ultima localizacion
            //se pone en el texto para que el usuario lo vea , obvio se convierte en string para que se pueda poner
            latitudR.setText(String.valueOf(ultima_localizacion.getLatitude()));
            longitudR.setText(String.valueOf(ultima_localizacion.getLongitude()));
        } else {//si no hay ultima localizacion ponemos mensaje de no se pudo encontrar la ubicacion
            Toast.makeText(this, "Ubicación no detectada.", Toast.LENGTH_SHORT).show();
        }

        //detalles de la ubicacion
        try{
            //se trae una lista de direcciones de una longitud y latitud en este caso de la ultima conocida
            List<Address> addresses = geocoder.getFromLocation(ultima_localizacion.getLatitude(),ultima_localizacion.getLongitude(),1);

            if(addresses != null){ //si si habia alguna ubicacion para tener

                Address address = addresses.get(0); //la direccion va a ser lo que tiene la lista en posicion 0
                StringBuilder sb = new StringBuilder(); //se crea un contenedor de strings

                for(int i =0;i<address.getMaxAddressLineIndex();i++){// recorre todas las lineas de direccion que tiene
                sb.append(address.getAddressLine(i)).append(""); //las va acumulando

                }
                direccionR.setText(sb.toString());//se pone en el texto para que lo vea el usuario
            }else{

                direccionR.setText("Direccion no encontrada");
            }

        }catch (IOException e){

        e.printStackTrace();
            direccionR.setText("La direccion no se pudo obtener");
        }

    }

    //metodo override que se activa si se suspende la conexion
    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Error de conexión.");

    }

    //metodo override que se activa si falla la conexion
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "Conexión suspendida.");
        gac.connect();

    }
}
