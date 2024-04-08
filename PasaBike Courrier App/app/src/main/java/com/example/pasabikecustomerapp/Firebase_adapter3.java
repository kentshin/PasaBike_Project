package com.example.pasabikecustomerapp;

import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase_adapter3 extends FirestoreRecyclerAdapter<transaction_model, com.example.pasabikecustomerapp.Firebase_adapter3.transholder> {

    private Firebase_adapter2.OnClick onclick;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    public Firebase_adapter3(@NonNull FirestoreRecyclerOptions<transaction_model> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull com.example.pasabikecustomerapp.Firebase_adapter3.transholder holder, int position, @NonNull transaction_model model) {

        holder.fee_collected.setText(model.getFee()+" "+"Php");
        holder.t_date.setText(model.getOrder_date().toDate().toString());
        holder.transaction_type.setText(model.getType());

        DocumentReference costumer = fstorage.collection("customer_user").document(model.getCustomer_id());
        costumer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    String customer_fn = snapshot.getString("fname") + " " + snapshot.getString("lname");
                    //String address = snapshot.getString("home_address");
                    holder.cust_name.setText(customer_fn);
                }
            }
        });

    }

    @NonNull
    @Override
    public Firebase_adapter3.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_transaction_holder, parent, false);

        return new com.example.pasabikecustomerapp.Firebase_adapter3.transholder(view);
    }




    public class transholder extends RecyclerView.ViewHolder {

        TextView t_date;
        TextView cust_name;
        TextView fee_collected;
        TextView transaction_type;


        public transholder(@NonNull View itemView) {
            super(itemView);

            t_date = itemView.findViewById(R.id.t_date );
            cust_name = itemView.findViewById(R.id.cust_name);
            fee_collected = itemView.findViewById(R.id.fee_collected);
            transaction_type = itemView.findViewById(R.id.transaction_type);


        }
    }
}
