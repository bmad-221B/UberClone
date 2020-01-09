package com.sherlocked.uberclone;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String name ;
    double lat,lng,lat1,lng1 ;
    Button button ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button = findViewById(R.id.navigateButton);
        Intent intent = getIntent() ;
        name = intent.getStringExtra("RiderName");
        String pos = intent.getStringExtra("DriverPosition");
        String[] str = pos.split("x");
        lat = Double.parseDouble(str[0]);
        lng = Double.parseDouble(str[1]);
        String pos1 = intent.getStringExtra("RiderPosition");
        String[] str1 = pos1.split("x");
        lat1 = Double.parseDouble(str1[0]);
        lng1 = Double.parseDouble(str1[1]);
        double dLat = (lat-lat1)*3.14/180 ;
        double dLng = (lng-lng1)*3.14/180 ;
        double a = Math.sin(dLat/2)*Math.sin(dLng/2) + Math.cos(lat1*3.14/180)*Math.cos(lat*3.14/180)*Math.sin(dLng/2)*Math.sin(dLng/2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        double R1 = 6371.0 ;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        double dist = R1*c ;
        FirebaseDatabase.getInstance().getReference().child("Confirmation").child(name).setValue(df.format(dist) + "x" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + lat + "," + lng + "&daddr=" + lat1 + "," + lng1));
                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng rider = new LatLng(lat1, lng1);
        mMap.addMarker(new MarkerOptions().position(rider).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rider,16));
        LatLng you = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(you).title(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(you,16));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
