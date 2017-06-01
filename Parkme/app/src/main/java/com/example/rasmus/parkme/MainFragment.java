package com.example.rasmus.parkme;

import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
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
import java.util.Arrays;

/**
 * Created by rasmu on 2017-06-01.
 */

public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String APPID = "87e406c1-dd59-41bf-9e7a-de4cbd580dc6";
    private static final String FORMAT = "JSON";
    private static String latitude = "57.6907990";
    private static String longitude = "11.9719640";
    private static String radius = "10000";
    private TextView printData;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ListView listview;
    private ParkingLot[] parkingLotArray;
    private SeekBar seekbar;
    private TextView seekbarTextView;
    private boolean useCurrentPosition = true;

    public MainFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);



        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        /*
        Button hitbtn = (Button) findViewById(R.id.btnHit);
        printData = (TextView) findViewById(R.id.printJSON);
        */
        //Seelbar and it's TextView
        seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        seekbarTextView = (TextView) view.findViewById(R.id.seekBarTextView);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                updateParkingLotList();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        listview = (ListView) view.findViewById(R.id.listJSON);

        /*
        hitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateParkingLotList();

            }
        });*/


        return view;
    }

    private void updateParkingLotList() {

        radius = (seekbar.getProgress() + 1) * 300 + "";
        seekbarTextView.setText(radius + " m");

        if (mLastLocation != null && useCurrentPosition) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }

        new JSONTask().execute("http://data.goteborg.se/ParkingService/v1.3/PrivateParkings/" + APPID + "?latitude=" + latitude + "&longitude=" + longitude + "&radius=" + radius + "&format=" + FORMAT + "");

    }



    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        updateParkingLotList();

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

                //Connection to url
                URL url = new URL(params[0]);
                connection =(HttpURLConnection) url.openConnection();
                connection.connect();

                //Start input stream
                InputStream stream = connection.getInputStream();

                //Initiating reader
                reader = new BufferedReader(new InputStreamReader(stream));

                //Initiating string buffer
                StringBuffer buffer = new StringBuffer();

                String line = "";
                //loop through all lines
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                //convert string to an JSON array
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

                //return "Done! length: " + parkingLotArray.length + " lat:"+ latitude +" long: " + longitude ;




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
            //printData.setText(result);

            if (parkingLotArray != null){
                Snackbar snackbar = Snackbar
                        .make(getView().findViewById(R.id.activity_main), "Hittade " + parkingLotArray.length + " parkeringar nära dig ", Snackbar.LENGTH_LONG);
                snackbar.show();

            }

            listview.setAdapter(new parkingAdapter(getActivity(), parkingLotArray));
        }
    }

}
