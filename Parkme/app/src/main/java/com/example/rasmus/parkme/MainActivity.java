package com.example.rasmus.parkme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String APPID = "4cb244ca-9939-4848-a871-452aafa54d3b";
    private static final String LATITUDE = "57.656216";
    private static final String LONGITUDE = "11.920853";
    private static final String RADIUS = "100";
    private static final String FORMAT = "JSON";
    private TextView printData;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button hitbtn = (Button) findViewById(R.id.btnHit);
        printData = (TextView) findViewById(R.id.printJSON);


        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        hitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/"+APPID+"?latitude="+LATITUDE+"&longitude="+LONGITUDE+/*"&radius="+RADIUS+*/"&format="+FORMAT);
                new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/%7B4cb244ca-9939-4848-a871-452aafa54d3b%7D?latitude=57.694401&longitude=12.007370&radius=500&format=JSON");
            }
        });


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    public class JSONTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL(params[0]);
                connection =(HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                JSONArray parentObject = new JSONArray(finalJSON);
                //
                //JSONArray parentArray = parentObject.getJSONArray("");

                JSONObject finalObject = parentObject.getJSONObject(0);

                String name = finalObject.getString("Name");
                int totSpaces = finalObject.getInt("ParkingSpaces");
                if(!finalObject.isNull("FreeSpaces")){
                    int freeSpaces = finalObject.getInt("FreeSpaces");
                }
                int distance = finalObject.getInt("Distance");
                if(!finalObject.isNull("MaxParkingTime")) {
                    String maxParkingTime = finalObject.getString("MaxParkingTime");
                }
                if(!finalObject.isNull("ParkingCost")) {
                    String parkingCost = finalObject.getString("ParkingCost");
                }


                return ""+totSpaces;
                //int free = finalObject.getInt("ParkingSpaces");



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            printData.setText(result);
        }
    }

}

