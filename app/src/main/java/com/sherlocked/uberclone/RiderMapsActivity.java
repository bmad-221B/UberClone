package com.sherlocked.uberclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class RiderMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button ;
    boolean cabBooked = false ;
    LocationManager locationManager ;
    LocationListener locationListener ;
    Double lat = 0.0 ;
    String address = "" ;
    String adminArea = "" ;
    Double lng = 0.0 ;
    int po ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_maps);
        setTitle("Uber");
        button = findViewById(R.id.button);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button.getText().toString().equals("Book Uber")){
                    cabBooked = true ;
                    Toast.makeText(RiderMapsActivity.this,"Searching For Nearest Cab...",Toast.LENGTH_SHORT).show();
                    //myRef.child("Rider").child("Name").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    myRef.child("Request").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "->" + lat + "x" + lng + "=" + adminArea);
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.i("Result",dataSnapshot.getKey());
                             po = 1 ;
                            if(dataSnapshot.getKey().equals("Confirmation")){
                                for(final DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                                    if(dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                                        String str1 = dataSnapshot1.getValue().toString();
                                        String[] sp = str1.split("x");
                                        double cost = Double.parseDouble(sp[0])*7 + 10.0 ;
                                        String driver = sp[1] ;
                                        new AlertDialog.Builder(RiderMapsActivity.this)
                                                .setTitle("Confirm Ride")
                                                .setMessage("Your Ride is Estimated around ₹" + cost + "\nAnd is served by " + driver)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        po = 1 ;
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dataSnapshot1.getRef().removeValue();
                                                        po = 0 ;
                                                        button.setText("Book Uber");
                                                    }
                                                })
                                                .show();
                                    }
                                    if(po==0){
                                        break;
                                    }
                                }
                            }
                            if(po==0){
                                cabBooked = false ;
                                if(dataSnapshot.getKey().equals("Request")){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        Log.i("bhai Bhai",dataSnapshot1.getKey());
                                        Log.i("Bhai Bhai",dataSnapshot1.getValue().toString());
                                        if(dataSnapshot1.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "->" + lat + "x" + lng + "=" + adminArea) && !cabBooked){
                                            Log.i("Bhai Bhai",dataSnapshot1.getValue().toString());
                                            dataSnapshot1.getRef().removeValue();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    button.setText("Cancel Uber");
                }else if(button.getText().toString().equals("Cancel Uber")){
                    cabBooked = false ;
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.getKey().equals("Request")){
                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    Log.i("bhai Bhai",dataSnapshot1.getKey());
                                    Log.i("Bhai Bhai",dataSnapshot1.getValue().toString());
                                    if(dataSnapshot1.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "->" + lat + "x" + lng + "=" + adminArea) && !cabBooked){
                                        Log.i("Bhai Bhai",dataSnapshot1.getValue().toString());
                                        dataSnapshot1.getRef().removeValue();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    button.setText("Book Uber");
                }
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
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener  = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You are Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address>addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList.size()>0 && addressList!=null){

                        if(addressList.get(0).getThoroughfare()!=null){
                            address = address + addressList.get(0).getThoroughfare() + " " ;
                            Log.i("Through",addressList.get(0).getThoroughfare());
                        }
                        if(addressList.get(0).getLocality()!=null){
                            address = address + addressList.get(0).getLocality() + " " ;
                        }
                        if(addressList.get(0).getPostalCode()!=null) {
                            address = address + addressList.get(0).getPostalCode() + " " ;
                        }
                        if(addressList.get(0).getAdminArea()!=null){
                            address = address + addressList.get(0).getAdminArea() ;
                            adminArea = adminArea + addressList.get(0).getAdminArea();
                        }
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title(addressList.get(0).getLocality()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (Build.VERSION.SDK_INT < 27) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                try {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    mMap.clear();
                    lat = lastKnownLocation.getLatitude();
                    lng = lastKnownLocation.getLongitude();
                    Log.i("Location",  lastKnownLocation.toString());
                    LatLng  userLastKnownLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position( userLastKnownLocation).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( userLastKnownLocation,16));
                }catch (Exception e){
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    Log.i("Err12",e.getMessage());
                }

            }

        }

        // Add a marker in Sydney and move the camera

    }
}
