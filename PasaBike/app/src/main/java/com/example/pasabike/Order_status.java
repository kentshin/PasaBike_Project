package com.example.pasabike;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class Order_status extends Fragment implements My_order_adapter.OnClick {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private RecyclerView transaction;
    private My_order_adapter adapter_order;
    private FirebaseAuth  fAuth = FirebaseAuth.getInstance();

    Spinner reason;
    String report_reason = "";

    private CollectionReference my_order_reference = fstorage.collection("transactions");
    String uid = fAuth.getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View my_order=  inflater.inflate(R.layout.fragment_order_status, container, false);
        transaction = my_order.findViewById(R.id.my_order_recyclerview);

        display_status();

        return my_order;
    }



    public void display_status() {

        Query my_orders = my_order_reference.whereLessThan("status", 6).whereEqualTo("customer_id", uid).orderBy("status").orderBy("order_date");
        FirestoreRecyclerOptions<transaction_db_model> newoptions = new FirestoreRecyclerOptions.Builder<transaction_db_model>()
                .setLifecycleOwner(this)
                .setQuery(my_orders, transaction_db_model.class)
                .build();
        adapter_order = new My_order_adapter(newoptions, this);
        transaction.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction.setAdapter(adapter_order);

    }



    /*
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        //Toast.makeText(getContext(), snapshot.get("status").toString(),Toast.LENGTH_LONG).show();
    }
    */


    @Override
    public void onItemClick2(DocumentSnapshot snapshot, int position) {

        long s_status = (long) snapshot.get("status");
        if (s_status == 5) {
            fstorage.collection("transactions").document(snapshot.getId())
                    .update("status", 6);
            Toast.makeText(getContext(), "Request Succesfully Completed", Toast.LENGTH_LONG).show();
        }else {

        }

    }

    @Override
    public void onItemClick3(DocumentSnapshot snapshot, int position) {

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.report_dialog);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        Button report_button = (Button)dialog.findViewById(R.id.report_button);
        Button cancel = (Button)dialog.findViewById(R.id.cancel);
        TextView reason = (TextView) dialog.findViewById(R.id.report_reason);

        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final DocumentReference docRef = fstorage.collection( "reported_courrier_accounts"). document();

                        if(reason.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Please Enter Reason for Reporting", Toast.LENGTH_LONG).show();
                        } else {

                            //updating transaction status
                            fstorage.collection("transactions").document(snapshot.getId())
                                    .update("status", 7);

                            //creating reported courrier
                            String courrier_id = snapshot.get("courrier_id").toString();
                            String reported_by = fAuth.getCurrentUser().getUid().toString();

                            Map<String,Object> Reported_courrier = new HashMap<>();

                            Reported_courrier.put("reported_courrier_id", courrier_id);
                            Reported_courrier.put("reported_by", reported_by );
                            Reported_courrier.put("report_reason", reason.getText().toString());
                            Reported_courrier.put("reported_date", Timestamp.now());
                            Reported_courrier.put("transaction_id", snapshot.getId().toString());


                            //for report documents
                            docRef.set(Reported_courrier).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(),"Complaint Report Sent!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }else {
                                        Toast.makeText(getContext(),"Sorry, Complaint Report failed! Please try again.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }



            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });




    }








    @Override
    public void onStart() {
        super.onStart();
        adapter_order.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter_order.stopListening();
    }



}