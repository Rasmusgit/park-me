package com.example.rasmus.parkme;
        import android.content.pm.PackageManager;
        import android.content.res.AssetManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.webkit.WebView;

        import com.example.rasmus.parkme.R;
        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.MapsInitializer;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.MarkerOptions;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.io.StringWriter;
        import java.io.Writer;

public class ContactFragment extends Fragment implements OnMapReadyCallback{
    MapView mapView;
    GoogleMap googleMap;
    private WebView contactWebView;
    public ContactFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        getActivity().setTitle("Contact Us");
        contactWebView = (WebView)view.findViewById(R.id.contact_us);
        contactWebView.setBackgroundColor(0);
        if(Build.VERSION.SDK_INT >= 11){
            contactWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        contactWebView.getSettings().setBuiltInZoomControls(true);
        AssetManager mgr = getContext().getAssets();
        String filename = null;
        try {
            filename = "contact.html";
            System.out.println("filename : " + filename);
            InputStream in = mgr.open(filename, AssetManager.ACCESS_BUFFER);
            String sHTML = StreamToString(in);
            in.close();
            contactWebView.loadDataWithBaseURL(null, sHTML, "text/html", "utf-8", null);
            //singleContent.setText(Html.fromHtml(sHTML));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {

            mapView.getMapAsync(this);

        }
        return view;
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    public static String StreamToString(InputStream in) throws IOException {
        if(in == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
        }
        return writer.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .anchor(0.0f, 1.0f)
                .position(new LatLng(55.854049, 13.661331)));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        MapsInitializer.initialize(this.getActivity());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(55.854049, 13.661331));
        LatLngBounds bounds = builder.build();
        int padding = 0;
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);
    }
}