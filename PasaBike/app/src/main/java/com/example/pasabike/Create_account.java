package com.example.pasabike;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Create_account extends AppCompatActivity  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    FirebaseAuth Fauth = FirebaseAuth.getInstance();
    FirebaseFirestore Fstorage = FirebaseFirestore.getInstance();

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long update_interval = 2000;
    private long fastest_interval = 2000;
    private LocationManager locationManager2;
    private LatLng latLng;
    private boolean ispermission;
    private Marker newlocationmarker;

    double lat;
    double longi;


    EditText fn, ln, phone_num, home_address;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        fn = findViewById(R.id.fn);
        ln = findViewById(R.id.ln);
        phone_num = findViewById(R.id.phone_num);
        home_address = findViewById(R.id.volunter_address);
        save = findViewById(R.id.save);

        phone_num .setText(Fauth.getCurrentUser().getPhoneNumber());
        save();

        if (requestPermission()) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
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
    public void onConnected(@Nullable Bundle bundle) {

        //checking permission
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
        }else
        {
            Toast.makeText(this, "Getting Location...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (newlocationmarker != null) {
            newlocationmarker.remove();
        }


        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        lat = latitude;
        longi = longitude;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String knownName = addresses.get(0).getFeatureName();
                String province = addresses.get(0).getSubAdminArea();

                //marker postition
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(address);;
                newlocationmarker = mMap.addMarker(markerOptions);
                //

                home_address.setText(knownName + ", " + city + ", " + province + ", " + country);

            } else {
                home_address.setText("Getting Your Address");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



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



    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location Services?")
                .setMessage("Your Location Settings is Off. Please enable Location Services to locate your current address")
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






    public void save() {

        final DocumentReference docRef = Fstorage.collection( "customer_user"). document(Fauth.getCurrentUser().getUid());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fn.getText().toString().isEmpty() || ln.getText().toString().isEmpty( ) || home_address.getText().toString().isEmpty( )||phone_num.getText().toString().isEmpty( ) ) {

                    Toast.makeText(getApplicationContext(), "All fields are Required.", Toast.LENGTH_SHORT).show();
                }

                else {

                    String firstname = fn.getText().toString();
                    String lastname = ln.getText().toString();
                    String phone = phone_num.getText().toString();
                    String address_= home_address.getText().toString();
                    GeoPoint geo = new GeoPoint(lat,longi);



                    Map<String,Object> Users = new HashMap<>();

                    Users.put("fname", firstname );
                    Users.put("lname", lastname );
                    Users.put("phone_number", phone);
                    Users.put("home_address", address_ );
                    Users.put("geo_location", geo);


                    //for profile
                    docRef.set(Users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"PasaBike Customer Account Created", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Home.class));


                            }else {
                                Toast.makeText(getApplicationContext(),"There's a problem creating a user account. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        if (latLng!=null) {

            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F));
        }
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


}