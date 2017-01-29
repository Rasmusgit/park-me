package com.example.rasmus.parkme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String APPID = "4cb244ca-9939-4848-a871-452aafa54d3b";
    private static final String FORMAT = "JSON";
    private static String latitude = "57.656216";
    private static String longitude = "11.920853";
    private static String radius = "10000";
    private TextView printData;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ListView listview;
    private ParkingLot[] parkingLotArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        Button hitbtn = (Button) findViewById(R.id.btnHit);
        printData = (TextView) findViewById(R.id.printJSON);
        final TextView seekbarTextView = (TextView) findViewById(R.id.seekBarTextView);

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int val = i * 10000;
                radius = val + "";
                seekbarTextView.setText(radius + " m");

                if (mLastLocation != null) {
                    latitude = String.valueOf(mLastLocation.getLatitude());
                    longitude = String.valueOf(mLastLocation.getLongitude());
                }

                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/"+APPID+"?latitude="+LATITUDE+"&longitude="+LONGITUDE+/*"&radius="+RADIUS+*/"&format="+FORMAT);
                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/%7B4cb244ca-9939-4848-a871-452aafa54d3b%7D?latitude=57.694401&longitude=12.007370&radius=1000&format=JSON");
                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/4cb244ca-9939-4848-a871-452aafa54d3b?latitude=57.694401&longitude=12.007370&radius=1000&format=JSON");
                new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/" + APPID + "?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius + "&format=" + FORMAT + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        listview = (ListView) findViewById(R.id.listJSON);



        hitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mLastLocation != null) {
                    latitude = String.valueOf(mLastLocation.getLatitude());
                    longitude = String.valueOf(mLastLocation.getLongitude());
                }

                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/"+APPID+"?latitude="+LATITUDE+"&longitude="+LONGITUDE+/*"&radius="+RADIUS+*/"&format="+FORMAT);
                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/%7B4cb244ca-9939-4848-a871-452aafa54d3b%7D?latitude=57.694401&longitude=12.007370&radius=1000&format=JSON");
                //new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/4cb244ca-9939-4848-a871-452aafa54d3b?latitude=57.694401&longitude=12.007370&radius=1000&format=JSON");
                new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/"+APPID+"?latitude="+latitude+"&longitude="+longitude+"&radius="+radius+"&format="+FORMAT+"");
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



                ParkingLot parkingLot;
                parkingLotArray = new ParkingLot[parentObject.length()];
                JSONObject finalObject;

                for(int i = 0; i < parentObject.length(); i++){
                    finalObject = parentObject.getJSONObject(i);

                    String name = finalObject.getString("Name");
                    int totSpaces;
                    if(!finalObject.isNull("ParkingSpaces")) {
                        totSpaces = finalObject.getInt("ParkingSpaces");
                    }else{
                        totSpaces = -1;
                    }

                    int freeSpaces;
                    if(!finalObject.isNull("FreeSpaces")){
                        freeSpaces = finalObject.getInt("FreeSpaces");
                    }else{
                        freeSpaces = -1;
                    }

                    int distance;
                    if(!finalObject.isNull("Distance")){
                        distance = finalObject.getInt("Distance");
                    }else{
                        distance = -1;
                    }

                    String maxTime;
                    if(!finalObject.isNull("MaxParkingTime")) {
                        maxTime = finalObject.getString("MaxParkingTime");
                    }else {
                        maxTime = null;
                    }

                    String cost;
                    if(!finalObject.isNull("ParkingCost")) {
                        cost = finalObject.getString("ParkingCost");
                    }else{
                        cost = null;
                    }


                    parkingLot = new ParkingLot(name, totSpaces, freeSpaces, distance, maxTime, cost);
                    parkingLotArray[i] = parkingLot;

                }

                //sorting distance
                Arrays.sort(parkingLotArray);

                return "Done! length: " + parkingLotArray.length + "    lat:"+ latitude +" long: " + longitude ;
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
            listview.setAdapter(new parkingAdapter(MainActivity.this, parkingLotArray));
        }
    }

}

