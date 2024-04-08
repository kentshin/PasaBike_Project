package com.example.pasabike;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Record_history extends Fragment{

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference reference = firestore .collection("transactions");
    private RecyclerView transaction_recycler;
    private My_record_adapter adapter;
    CollectionReference transaction = firestore.collection("transactions");

    String uid = fAuth.getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View records =  inflater.inflate(R.layout.fragment_record_history, container, false);

        transaction_recycler = records.findViewById(R.id.my_order_recyclerview);


        display__history();
        return records;
    }




    public void display__history() {

        Query query = reference.whereEqualTo("status", 6).whereEqualTo("customer_id", uid).orderBy("order_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<transaction_db_model> newoptions = new FirestoreRecyclerOptions.Builder<transaction_db_model>()
                .setLifecycleOwner(this)
                .setQuery(query, transaction_db_model.class)
                .build();
        adapter = new My_record_adapter(newoptions);
        transaction_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        transaction_recycler.setAdapter(adapter);

    }






}