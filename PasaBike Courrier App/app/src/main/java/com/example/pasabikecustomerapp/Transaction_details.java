package com.example.pasabikecustomerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class Transaction_details extends FragmentActivity implements OnMapReadyCallback,
        com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;



    private boolean ispermission;
    private Location location;
    private LocationRequest locationRequest;
    private long update_interval = 1000;
    private long fastest_interval = 1000;

    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private LocationManager locationManager2;
    private LatLng latLng;
    private Marker newlocationmarker;

    double courrier_longitude;
    double courrier_latitude;


    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    Button Accept;
    RelativeLayout top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        Accept = findViewById(R.id.accept);
        top = findViewById(R.id.top);
        accept_request();

        //String  transaction_id =  global_var.transaction_id;
        //Toast.makeText(Transaction_details.this, transaction_id, Toast.LENGTH_SHORT).show();

        if (requestPermission()) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


            checklocation();

        }

    }

    private boolean requestPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ispermission = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            ispermission = false;
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        return ispermission;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        MarkerOptions marker;
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (latLng!=null) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));
        }



        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("lng");
            String title_mark = bundle.getString("title");

            LatLng customer_loc = new LatLng(lat, lng);
            marker = new MarkerOptions().position(customer_loc).title(title_mark)
                    .icon(BitmapFromVector(getApplicationContext(),R.drawable.customer_pin));
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customer_loc, 15));
        }


    }





    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (newlocationmarker != null) {
            newlocationmarker.remove();
        }

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title("My Location").icon(BitmapFromVector(getApplicationContext(),R.drawable.bike));
        newlocationmarker = mMap.addMarker(markerOptions);

    }
















    //converting vector resource to bitmap
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location Services?")
                .setMessage("Please enable Location Services to locate your current address")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myintent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();

    }



    //CHECKING LOCATION
    private boolean checklocation() {
        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }



    private boolean isLocationEnabled() {
        locationManager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager2.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager2.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdate();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location == null) {
            startLocationUpdate();
        }else {
           // Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }



        private void startLocationUpdate() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(update_interval)
                .setFastestInterval(fastest_interval);
        //checking permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }



    protected void onStart() {
        super.onStart();
        if(googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed! Please try again!", Toast.LENGTH_SHORT).show();
    }




    void accept_request() {

        if (global_var.for_location == "1") {

            top.setVisibility(View.GONE);
        }

        String  transaction_id =  global_var.transaction_id;
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fstorage.collection("transactions").document(transaction_id).update("courrier_id", fAuth.getCurrentUser().getUid(), "status", 2);
                startActivity(new Intent(getApplicationContext(),  Home.class));
                finish();

                //Intent intent = new Intent (Transaction_details.this, Home.class);
                //startActivity(intent);
                //finish();
                //Toast.makeText(Transaction_details.this, "Request Accepted", Toast.LENGTH_SHORT).show();
            }
        });


    }




}