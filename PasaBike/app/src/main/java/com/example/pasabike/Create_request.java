package com.example.pasabike;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Create_request extends Fragment {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    TextView customer_fn, customer_address;
    RelativeLayout first;
    RelativeLayout second;
    ImageView edit_loc;

    //for edit location
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationListener locationListener;

    private long update_interval = 2000;
    private long fastest_interval = 2000;
    private LocationManager locationManager2;
    private LatLng latLng;
    private boolean ispermission;
    private Marker newlocationmarker;

    double lat;
    double longi;

    String new_address;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View create_request = inflater.inflate(R.layout.fragment_create_request, container, false);

        customer_fn = create_request.findViewById(R.id.customer_fullname);
        customer_address = create_request.findViewById(R.id.customer_addres);
        first = create_request.findViewById(R.id.uno);
        second = create_request.findViewById(R.id.dos);
        edit_loc = create_request.findViewById(R.id.edit_loc);


        display_name();
        create_order();

        return  create_request;

    }


    public void display_name(){

        DocumentReference customer = fstorage.collection("customer_user").document(fAuth.getCurrentUser().getUid());
        customer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                customer_fn.setText(documentSnapshot.get("fname") + " " + documentSnapshot.get("lname"));
                customer_address.setText(documentSnapshot.get("home_address").toString());
            }
        });


    }


    public void create_order() {

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (getContext(), Order_form.class);
                startActivity(intent);

            }
        });


        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), Deliver_form.class);
                startActivity(intent);


            }
        });


    }





    public void dialog_box () {

        final Dialog dialog_location = new Dialog(getContext());
        dialog_location.setContentView(R.layout.edit_location_layout);
        dialog_location.show();
        dialog_location.setCanceledOnTouchOutside(false);

        Button ok = (Button)dialog_location.findViewById(R.id.report_button);
        Button cancel = (Button)dialog_location.findViewById(R.id.cancel);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_location.dismiss();
            }
        });
    }









}