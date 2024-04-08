  package com.example.pasabikecustomerapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


public class order_stats_frag extends  Fragment implements Firebase_adapter2.OnClick{

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("transactions");
    private RecyclerView transaction_recycler;
    private Firebase_adapter2 adapter;
    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View order_status = inflater.inflate(R.layout.fragment_order_stats_frag, container, false);

        fAuth = FirebaseAuth.getInstance();
        fstorage = FirebaseFirestore.getInstance();
        transaction_recycler = order_status.findViewById(R.id.order_stats_recyclerview);

        display_status();

        return  order_status;
    }



    public void display_status() {

        Query query = reference.whereEqualTo("courrier_id", fAuth.getCurrentUser().getUid()).whereLessThan("status", 6).orderBy("status", Query.Direction.DESCENDING).orderBy("order_date");
        FirestoreRecyclerOptions<transaction_model> newoptions = new FirestoreRecyclerOptions.Builder<transaction_model>()
                .setLifecycleOwner(this)
                .setQuery(query, transaction_model.class)
                .build();
        adapter = new Firebase_adapter2(newoptions, this);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);


    }


    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

        global_var.for_location = "1";
        DocumentReference get_customer_id = fstorage.collection("customer_user").document(snapshot.get("customer_id").toString());
        get_customer_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (snapshot.exists()) {

                    GeoPoint location = documentSnapshot.getGeoPoint("geo_location");
                    double lat =  location.getLatitude();
                    double lng = location.getLongitude();
                    Intent intent = new Intent (getActivity(), Transaction_details.class);
                    intent.putExtra("title", "Customer Address");
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng",lng);
                    startActivity(intent);

                }

            }
        });

    }

    @Override
    public void onItemClick2(DocumentSnapshot snapshot, int position) {

        String transaction_type = snapshot.get("type").toString();
        long s_status = (long) snapshot.get("status");

            if (s_status == 2) {

                switch (transaction_type) {
                    case "Pasabuy":
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.amount);
                        dialog.show();

                        final EditText amount = (EditText) dialog.findViewById(R.id.amnt);
                        final Button update_amount = (Button) dialog.findViewById(R.id.update_amount);
                        update_amount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fstorage.collection("orders").document(snapshot.get("order_id").toString())
                                        .update("exact_amount", amount.getText().toString());
                                fstorage.collection("transactions").document(snapshot.getId())
                                        .update("status", 3);
                                Toast.makeText(getContext(), "Transaction Updated", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        break;

                    case "Padeliver":
                        fstorage.collection("transactions").document(snapshot.getId())
                                .update("status", 3);
                        break;
                }

            } else if (s_status == 3) {
                fstorage.collection("transactions").document(snapshot.getId())
                        .update("status", 4);
                Toast.makeText(getContext(), "Transaction Updated", Toast.LENGTH_SHORT).show();
            } else if (s_status == 4) {
                fstorage.collection("transactions").document(snapshot.getId())
                        .update("status", 5);
                Toast.makeText(getContext(), "Transaction Updated", Toast.LENGTH_SHORT).show();
            } else if (s_status == 5) {
                Toast.makeText(getContext(), "Customer to Confirm if Succesful", Toast.LENGTH_LONG).show();

            }




    }

    @Override
    public void onItemClick3(DocumentSnapshot snapshot, int position) {
        DocumentReference recipient_geolocation = fstorage.collection("orders").document(snapshot.get("order_id").toString());

        String transaction_type = snapshot.get("type").toString();

        if (transaction_type.equals("Pasabuy")) {

        }else {
            global_var.for_location = "1";
            recipient_geolocation.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        GeoPoint location2 = documentSnapshot.getGeoPoint("recipient_location");
                        double lat2 = location2.getLatitude();
                        double lng2 = location2.getLongitude();

                        Intent intent = new Intent(getActivity(), Transaction_details.class);
                        intent.putExtra("title", "Delivery Recipient Address");
                        intent.putExtra("lat", lat2);
                        intent.putExtra("lng", lng2);
                        startActivity(intent);


                    }

                }
            });

        }
    }











}










