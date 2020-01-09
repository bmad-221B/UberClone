package com.sherlocked.uberclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;
import android.widget.ViewSwitcher;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch ;
    int RC_SIGN_IN = 1 ;
    FirebaseDatabase database ;
    DatabaseReference myRef ;
    DatabaseReference myRef1 ;
    public void getStarted(View view){

        List<AuthUI.IdpConfig>providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build() );
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
                RC_SIGN_IN);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Uber");
        aSwitch = findViewById(R.id.switchView);
        database  = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef1 = database.getReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(aSwitch.isChecked()){
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    // The user is new, show them a fancy intro screen!
                    myRef.child("Driver").child("Name").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Log.i("Driver",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                } else {
                    // This is an existing user, show them a welcome back screen.
                }

                Intent intent = new Intent(getApplicationContext(),DriverActivity.class);
                startActivity(intent);
            }else{
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    // The user is new, show them a fancy intro screen!
                    Toast.makeText(MainActivity.this,FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();
                    myRef.child("Rider").child("Name").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                } else {
                    // This is an existing user, show them a welcome back screen.
                }

                Intent intent = new Intent(getApplicationContext(),RiderMapsActivity.class);
                startActivity(intent);
            }
        }
    }
}
