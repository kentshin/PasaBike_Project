package com.example.pasabikecustomerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class Firebase_adapter extends FirestoreRecyclerAdapter<transaction_model, com.example.pasabikecustomerapp.Firebase_adapter.transholder> {

    private OnClick onclick;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();


    //for clicking events
    public Firebase_adapter(@NonNull FirestoreRecyclerOptions<transaction_model> options, OnClick onclick) {
        super(options);

        this.onclick = onclick;
    }



    @Override
    protected void onBindViewHolder(@NonNull final com.example.pasabikecustomerapp.Firebase_adapter.transholder holder, int position, @NonNull transaction_model model) {

        holder.order_id.setText(model.getOrder_id());
        holder.type.setText(model.getType());
        
        DocumentReference costumer = fstorage.collection("customer_user").document(model.getCustomer_id());
        costumer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {
                    String customer_fn = snapshot.getString("fname") + " " +  snapshot.getString("lname");
                    String address = snapshot.getString("home_address");
                    holder.customer_name.setText(customer_fn);
                    holder.customer_address.setText(address);
                }
            }
        });

        if(model.getType().equals("Pasabuy")) {
            DocumentReference order_details = fstorage.collection("orders").document(model.getOrder_id());
            order_details.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String estimated_total = snapshot.getString("estimated_amount");
                        String store = snapshot.getString("order_establishment");
                        holder.establishment.setText(store);
                        holder.estimated_total.setText("Estimated Amount:" + " " +estimated_total + " " + "Php");
                    }

                }
            });
        }else if (model.getType().equals("Padeliver")) {

            DocumentReference order_details = fstorage.collection("orders").document(model.getOrder_id());
            order_details.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String recipient = snapshot.getString("recipient");
                        holder.top_id.setText("Delivery ID:");
                        holder.establishment.setText(recipient);
                        holder.estimated_total.setVisibility(View.GONE);
                    }

                }
            });



        }




    }





    @NonNull
    @Override
    public com.example.pasabikecustomerapp.Firebase_adapter.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders, parent, false);

        return new com.example.pasabikecustomerapp.Firebase_adapter.transholder(view);
    }



    public class transholder extends RecyclerView.ViewHolder {

        TextView customer_name;
        TextView customer_address;
        TextView order_id;
        TextView establishment;
        TextView estimated_total;
        TextView distance;
        TextView type;
        TextView top_id;
        FloatingActionButton view_order;

        public transholder(@NonNull View itemView) {
            super(itemView);

            customer_name = itemView.findViewById(R.id.textView4);
            customer_address = itemView.findViewById(R.id.requestor_address);
            establishment = itemView.findViewById(R.id.variable);
            distance = itemView.findViewById(R.id.type);
            order_id = itemView.findViewById(R.id.order_id);
            estimated_total = itemView.findViewById(R.id.total_estimate);
            view_order = itemView.findViewById(R.id.orders);
            type = itemView.findViewById(R.id.type);
            top_id = itemView.findViewById(R.id.textView2);


            //for getting id onclick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null) {

                        onclick.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });

            view_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });




        }
    }




    public interface OnClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
        void onItemClick2(DocumentSnapshot snapshot, int postion);
    }

    public void setOnclick (com.example.pasabikecustomerapp.Firebase_adapter.OnClick onclick) {
        this.onclick = onclick;

    }








}
