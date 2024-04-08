package com.example.pasabikecustomerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class order_history_frag extends Fragment {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference reference = firestore .collection("transactions");
    private RecyclerView transaction_recycler;
    private Firebase_adapter3 adapter3;
    CollectionReference number_of_transaction = firestore.collection("transactions");


    String uid = fAuth.getCurrentUser().getUid();

    TextView name, start_date, address, contact, profit_total, total_delivery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View record_panel = inflater.inflate(R.layout.fragment_order_history_frag, container, false);

        name = record_panel.findViewById(R.id.name);
        address = record_panel.findViewById(R.id.address);
        start_date = record_panel.findViewById(R.id.start_date);
        contact = record_panel.findViewById(R.id.contact);
        profit_total = record_panel.findViewById(R.id.profit_total);
        total_delivery = record_panel.findViewById(R.id.total_delivery);
        transaction_recycler = record_panel.findViewById(R.id.order_history_recyclerview);


        get_number_of_services();
        display_transaction_history();
        get_total_profit();
        get_details();
        return record_panel;

    }




    public void display_transaction_history() {

        Query query = reference.whereEqualTo("status", 6).whereEqualTo("courrier_id", uid).orderBy("order_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<transaction_model> newoptions = new FirestoreRecyclerOptions.Builder<transaction_model>()
                .setLifecycleOwner(this)
                .setQuery(query, transaction_model.class)
                .build();
        adapter3 = new Firebase_adapter3(newoptions);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter3);

    }




    public void get_details () {

        DocumentReference docRef = firestore.collection("courrier_user").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String fname = (documentSnapshot.getString("fname")) + " " + (documentSnapshot.getString("lname"));
                    //converting the metadata timestamp long to date
                    Long date_reg = fAuth.getCurrentUser().getMetadata().getCreationTimestamp();
                    String dateString = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date_reg));

                    name.setText(fname);
                    contact.setText(fAuth.getCurrentUser().getPhoneNumber().toString());
                    address.setText(documentSnapshot.getString("address"));
                    total_delivery.setText(documentSnapshot.getString("total_deliveries"));
                    start_date.setText(dateString);
                    profit_total.setText(documentSnapshot.getLong("total_profit").toString());

                }
            }
        });


    }



    public void get_number_of_services() {

        final Query total_service = number_of_transaction.whereEqualTo("status", 6).whereEqualTo("courrier_id", fAuth.getCurrentUser().getUid());
        total_service.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String total_number_of_service = String.valueOf(queryDocumentSnapshots.size());
                total_delivery.setText(total_number_of_service);

                firestore.collection("courrier_user").document(fAuth.getCurrentUser().getUid())
                        .update("total_deliveries", total_number_of_service);

            }
        });



    }



    public void get_total_profit() {

        Query total_profit = number_of_transaction.whereEqualTo("status", 6).whereEqualTo("courrier_id", fAuth.getCurrentUser().getUid());

        total_profit.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    double total_fees = 0;

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        double total_fee_new = documentSnapshot.getLong("fee");
                        total_fees = total_fees + total_fee_new;
                        profit_total.setText(String.valueOf(total_fees) + " " + "Php");

                        firestore.collection("courrier_user").document(fAuth.getCurrentUser().getUid())
                                .update("total_profit", total_fees);

                    }
                }


            }

        });





    }




}