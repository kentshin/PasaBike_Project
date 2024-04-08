package com.example.pasabikecustomerapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

public class order_frag extends Fragment implements Firebase_adapter.OnClick{



    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private Firebase_adapter adapter;
    private FirebaseAuth fAuth;

    TextView c_name, availability;
    Switch on_off;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View order_view = inflater.inflate(R.layout.fragment_order_frag, container, false);

        fAuth = FirebaseAuth.getInstance();
        fstorage = FirebaseFirestore.getInstance();
        transaction_recycler = order_view.findViewById(R.id.oder_recycleview);

        c_name = order_view.findViewById(R.id.label1);
        on_off = order_view.findViewById(R.id.switch1);
        availability = order_view.findViewById(R.id.availability);

        check_status();
        on_off_courrier();
        display_courrier();
        display_transaction();
        return order_view;
    }



    public void display_transaction() {

        Query query = reference.whereEqualTo("status", 1);
        FirestoreRecyclerOptions<transaction_model> newoptions = new FirestoreRecyclerOptions.Builder<transaction_model>()
                .setLifecycleOwner(this)
                .setQuery(query, transaction_model.class)
                .build();
        adapter = new Firebase_adapter(newoptions, this);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);
    }

    //first click
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

       //global var
        global_var.for_location = "0";
        global_var.transaction_id = snapshot.getId();


        DocumentReference get_customer_id = fstorage.collection("customer_user").document(snapshot.get("customer_id").toString());
        get_customer_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (snapshot.exists()) {

                    GeoPoint location = documentSnapshot.getGeoPoint("geo_location");
                        double lat =  location.getLatitude();
                        double lng = location.getLongitude();
                       Intent intent = new Intent (getActivity(), Transaction_details.class);
                       intent.putExtra("lat", lat);
                       intent.putExtra("lng",lng);
                       startActivity(intent);

                }

            }
        });

    }



    //second click
    @Override
    public void onItemClick2(DocumentSnapshot snaphot, int position) {

        DocumentReference get_orders = fstorage.collection("orders").document(snaphot.get("order_id").toString());
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.orders_panel);
        dialog.show();

        final TextView orders = (TextView) dialog.findViewById(R.id.order_list);
        get_orders.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    orders.setText(documentSnapshot.getString("items"));


                }

            }
        });


    }



    public void on_off_courrier() {

        on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (on_off.isChecked()) {
                    availability.setText("Online");
                    fstorage.collection("courrier_user").document(fAuth.getCurrentUser().getUid()).update("status", 1);
                    transaction_recycler.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"You're back Online!", Toast.LENGTH_SHORT);
                } else {
                    availability.setText("Offline");
                    fstorage.collection("courrier_user").document(fAuth.getCurrentUser().getUid())
                            .update("status", 2);
                    transaction_recycler.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),"Offline mode!", Toast.LENGTH_SHORT);
                }

            }
        });


    }




    void display_courrier() {

        DocumentReference courrier_name = fstorage.collection("courrier_user").document(fAuth.getCurrentUser().getUid());

        courrier_name.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {
                    String status1 = snapshot.get("status") + "";
                    c_name.setText(snapshot.getString("fname") + " " + snapshot.getString("lname") );

                    if (status1.equals("1")) {
                        on_off.setChecked(true);
                        availability.setText("Online");

                    } else if (status1.equals("2")) {
                        on_off.setChecked(false);
                        availability.setText("Offline");

                    }

                }

            }

        });

    }


    public void check_status() {

        DocumentReference check_stats = fstorage.collection("courrier_user").document(fAuth.getCurrentUser().getUid());
        check_stats.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String status = documentSnapshot.getLong("status")+"";
                if(status.equals("1")) {
                    transaction_recycler.setVisibility(View.VISIBLE);

                }else  {
                    transaction_recycler.setVisibility(View.INVISIBLE);
                }

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }


  @Override
   public void onStop() {
      super.onStop();
    adapter.stopListening();
   }




}