package com.example.pasabike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Deliver_form extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    private GoogleMap mMap;
    private Marker newlocationmarker;

    TextView recipient, recipient_address, items_delivered;
    Button create_deliver;

    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_form);

        recipient = findViewById(R.id.recipient);
        recipient_address = findViewById(R.id.recipient_addres);
        items_delivered= findViewById(R.id.items_delivered);
        create_deliver = findViewById(R.id.create_order);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        create_transaction();


    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        LatLng location = new LatLng(14.237493, 121.36542);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14F));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setCompassEnabled(true);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // mMap.getUiSettings().setMapToolbarEnabled(true);
       // mMap.getUiSettings().setZoomGesturesEnabled(true);
       // mMap.getUiSettings().setScrollGesturesEnabled(true);
        //mMap.getUiSettings().setTiltGesturesEnabled(true);
        //mMap.getUiSettings().setRotateGesturesEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                {


                    if (newlocationmarker != null) {
                        newlocationmarker.remove();

                    }

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    latitude = latLng.latitude;
                    longitude = latLng.longitude;


                    try {

                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0);
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String knownName = addresses.get(0).getFeatureName();
                            String province = addresses.get(0).getSubAdminArea();

                            recipient_address.setText(knownName + ", " + city + ", " + province + ", " + country);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(knownName + ", " + city + ", " + province + ", " + country);
                            newlocationmarker = mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F));

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }


    public void create_transaction() {
        final DocumentReference docRef = fstorage.collection("transactions").document();

        create_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (recipient.getText().toString().isEmpty() || recipient_address.getText().toString().isEmpty() || items_delivered.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are Required.", Toast.LENGTH_SHORT).show();
                } else {

                    String order_id = UUID.randomUUID().toString().substring(0, 6);
                    String receiver = recipient.getText().toString();
                    String item_list = items_delivered.getText().toString();
                    String recipient_add = recipient_address.getText().toString();


                    Map<String, Object> transactions = new HashMap<>();
                    Map<String, Object> orders = new HashMap<>();

                    transactions.put("courrier_id", "");
                    transactions.put("customer_id", fAuth.getCurrentUser().getUid().toString());
                    transactions.put("fee", 30);
                    transactions.put("order_date", Timestamp.now());
                    transactions.put("order_id", order_id);
                    transactions.put("status", 1);
                    transactions.put("type", "Padeliver");

                    GeoPoint geo_location = new GeoPoint(latitude,longitude);
                    orders.put("recipient", receiver);
                    orders.put("recipient_address", recipient_add);
                    orders.put("items", item_list);
                    orders.put("recipient_location", geo_location);


                    //for transaction
                    docRef.set(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(getApplicationContext(), "Request Failed. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    //for orders
                    final DocumentReference docRef2 = fstorage.collection("orders").document(order_id);
                    docRef2.set(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Padeliver Request Successfully Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "Request Failed. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });


    }



    public void dialog_box () {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogbox);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button ok = (Button)dialog.findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        dialog_box ();
    }


}








