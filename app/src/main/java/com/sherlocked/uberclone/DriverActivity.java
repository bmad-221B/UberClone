package com.sherlocked.uberclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DriverActivity extends AppCompatActivity {
    ListView listView ;
    FirebaseDatabase database = FirebaseDatabase.getInstance() ;
    //private GoogleMap mMap;
    DatabaseReference myRef  = database.getReference() ;
    ArrayAdapter<String>arrayAdapter ;
    ArrayList<String>distanceList= new ArrayList<String>();
    LocationManager locationManager;
    LocationListener locationListener;
    String adminArea = "" ;
    double lat, lng;
    double R1 = 6371.0 ;
    double lat1;
    double lng1 ;
    String name;
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
        setContentView(R.layout.activity_driver);
        listView = findViewById(R.id.distanceList);
        /*mMap = googleMap;*/
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,distanceList);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener  = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                /*mMap.addMarker(new MarkerOptions().position(userLocation).title("You are Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));*/
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList.size()>0 && addressList!=null){
                        String address = "" ;
                        if(addressList.get(0).getThoroughfare()!=null){
                            address = address + addressList.get(0).getThoroughfare() + " " ;
                        }
                        if(addressList.get(0).getLocality()!=null){
                            address = address + addressList.get(0).getLocality() + " " ;

                        }
                        if(addressList.get(0).getPostalCode()!=null) {
                            address = address + addressList.get(0).getPostalCode() + " " ;
                        }
                        if(addressList.get(0).getAdminArea()!=null){
                            address = address + addressList.get(0).getAdminArea() ;
                            if(adminArea.equals("")){
                                adminArea = adminArea + addressList.get(0).getAdminArea();
                            }


                        }
                        Log.i("Stu2",adminArea);
                        Toast.makeText(DriverActivity.this,address,Toast.LENGTH_SHORT).show();
                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        /*mMap.addMarker(new MarkerOptions().position(sydney).title(addressList.get(0).getLocality()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16));*/
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
        if (Build.VERSION.SDK_INT < 23) {
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
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                //mMap.clear();
                lat = lastKnownLocation.getLatitude();
                lng = lastKnownLocation.getLongitude();
                Log.i("Location",  lastKnownLocation.toString());
                LatLng  userLastKnownLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                /*mMap.addMarker(new MarkerOptions().position( userLastKnownLocation).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( userLastKnownLocation,16));*/
            }

        }

        // Add a marker in Sydney and move the camera


        //FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("Request")){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        String info = "" ;
                        String str = dataSnapshot1.getValue().toString() ;
                        String[] splitString = str.split("->");
                         name = splitString[0];
                        String[] split1 = splitString[1].split("x");
                         lat1 = Double.parseDouble(split1[0]);
                        String[] split2 = split1[1].split("=");
                         lng1 = Double.parseDouble(split2[0]);
                        String place = split2[1];
                        Log.i("Stu1",place);
                        double dLat = (lat-lat1)*3.14/180 ;
                        double dLng = (lng-lng1)*3.14/180 ;
                        double a = Math.sin(dLat/2)*Math.sin(dLng/2) + Math.cos(lat1*3.14/180)*Math.cos(lat*3.14/180)*Math.sin(dLng/2)*Math.sin(dLng/2);
                        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(4);
                        double dist = R1*c ;
                        Log.i("Distance",Double.toString(dist));
                        Log.i("LatLng",lat + "  " + lng + "\n" + lat1 + "  " + lng1 + "\t" + dLat + "\t" + dLng + "\t" + a + "\t" + c + "\t" + dist) ;
                        Log.i("Size", Integer.toString(distanceList.size()));
                        if(dist<=1000000.0){
                            Log.i("Size1", Integer.toString(distanceList.size()));
                            distanceList.add(df.format(dist) + " km" + "(" + name + ")");
                            Log.i("Stun",dist + "miles" + "(" + name + ")");
                            arrayAdapter.notifyDataSetChanged();
                        }
                        Log.i("Sttus",str);

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
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Intent intent = new Intent(getApplicationContext(),DriverMapsActivity.class);
                intent.putExtra("DriverPosition",lat+"x" + lng);
                intent.putExtra("RiderPosition",lat1+"x" + lng1);
                String nm = distanceList.get(i);
                String[] sp = nm.split("km");
                int l1 = sp[1].length();
                final String string = sp[1].substring(1,l1-1);
                intent.putExtra("RiderName",string);
                new AlertDialog.Builder(DriverActivity.this)
                        .setTitle("Confirm Ride")
                        .setMessage("Want to give Ride to " + string + " ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(DriverActivity.this,"Ride Cancelled!",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

            }
        });
    }
}
