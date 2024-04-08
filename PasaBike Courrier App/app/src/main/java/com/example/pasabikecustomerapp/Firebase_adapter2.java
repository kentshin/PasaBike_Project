package com.example.pasabikecustomerapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.protobuf.StringValue;

public class Firebase_adapter2 extends FirestoreRecyclerAdapter<transaction_model, com.example.pasabikecustomerapp.Firebase_adapter2.transholder> {

    private Firebase_adapter2.OnClick onclick;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    public Firebase_adapter2(@NonNull FirestoreRecyclerOptions<transaction_model> options, OnClick onclick) {
        super(options);

        this.onclick = onclick;
    }




    @Override
    protected void onBindViewHolder(@NonNull final com.example.pasabikecustomerapp.Firebase_adapter2.transholder holder, int position, @NonNull transaction_model model) {

        holder.orderid.setText(model.getOrder_id());
        holder.courrier_fee.setText(model.getFee()+" "+"Php");
        holder.transaction_id.setText(model.getType());
        if (model.getStatus() == 3) {
            holder.down1.setVisibility(View.VISIBLE);
            holder.uno.setVisibility(View.VISIBLE);
        }else if (model.getStatus() == 4) {
            holder.dos.setVisibility(View.VISIBLE);
            holder.uno.setVisibility(View.VISIBLE);
            holder.down1.setVisibility(View.VISIBLE);
            holder.down2.setVisibility(View.VISIBLE);
        }else if (model.getStatus() == 5) {
            holder.uno.setVisibility(View.VISIBLE);
            holder.dos.setVisibility(View.VISIBLE);
            holder.tres.setVisibility(View.VISIBLE);
            holder.down1.setVisibility(View.VISIBLE);
            holder.down2.setVisibility(View.VISIBLE);
            holder.down3.setVisibility(View.VISIBLE);

        }

        if (model.getType().equals("Padeliver")) {
            holder.type_changeable.setText("Items to be deliver secured.");
            holder.top_label.setText("DELIVERY ID");
            holder.label.setText("Delivery Items: ");
            holder.label_address.setText("Recipient: ");
            holder.total_amount.setVisibility(View.GONE);
            DocumentReference details = fstorage.collection("orders").document(model.getOrder_id());
            details.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.orderlists.setText(documentSnapshot.getString("items"));
                    holder.recepient_address.setText(documentSnapshot.getString("recipient"));

                }
            });

        } else if (model.getType().equals("Pasabuy")) {
            DocumentReference details = fstorage.collection("orders").document(model.getOrder_id());
            details.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    holder.total.setText(documentSnapshot.getString("exact_amount") + " " + "Php");
                    holder.orderlists.setText(documentSnapshot.getString("items"));
                }
            });

        }



        DocumentReference courrier = fstorage.collection("courrier_user").document(fAuth.getCurrentUser().getUid());
        courrier.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    String courrier_fn = snapshot.getString("fname") + " " + snapshot.getString("lname");
                    holder.courrier_name.setText(courrier_fn);
                }
            }
        });



        DocumentReference costumer = fstorage.collection("customer_user").document(model.getCustomer_id());
        costumer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    String customer_fn = snapshot.getString("fname") + " " + snapshot.getString("lname");
                    //String address = snapshot.getString("home_address");
                    holder.customer_name.setText(customer_fn);
                }
            }
        });







    }




    @NonNull
    @Override
    public Firebase_adapter2.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_request, parent, false);

            return new com.example.pasabikecustomerapp.Firebase_adapter2.transholder(view);

    }



    public interface OnClick {

        void onItemClick(DocumentSnapshot snapshot, int position);
        void onItemClick2(DocumentSnapshot snapshot, int position);
        void onItemClick3(DocumentSnapshot snapshot, int position);

    }


    public class transholder extends RecyclerView.ViewHolder {

        TextView customer_name;
        TextView courrier_name;
        TextView transaction_id;
        TextView orderid;
        TextView orderlists;
        TextView total;
        TextView courrier_fee;

        TextView type_changeable;
        TextView top_label;
        TextView label;
        TextView label_address;
        TextView recepient_address;
        TextView total_amount;

        ImageView down1;
        ImageView down2;
        ImageView down3;
        RelativeLayout uno;
        RelativeLayout dos;
        RelativeLayout tres;

        RelativeLayout second, first;

        FloatingActionButton update;


        public transholder(@NonNull View itemView) {
            super(itemView);
            down1 = itemView.findViewById(R.id.down1);
            down2 = itemView.findViewById(R.id.down2);
            down3 = itemView.findViewById(R.id.down3);
            uno = itemView.findViewById(R.id.dos);
            dos = itemView.findViewById(R.id.tres);
            tres = itemView.findViewById(R.id.quatro);

            type_changeable = itemView.findViewById(R.id.requestor_address);
            top_label = itemView.findViewById(R.id.top_label);
            label = itemView.findViewById(R.id.label);
            label_address = itemView.findViewById(R.id.label_address);
            recepient_address = itemView.findViewById(R.id.recipient_address);
            courrier_fee = itemView.findViewById(R.id.courrier_fee);


            total_amount = itemView.findViewById(R.id.total_amount);
            customer_name = itemView.findViewById(R.id.customer_name);
            courrier_name = itemView.findViewById(R.id.courrier_name);
            transaction_id = itemView.findViewById(R.id.transaction_id);
            orderlists = itemView.findViewById(R.id.order_lists);
            total= itemView.findViewById(R.id.total);
            orderid = itemView.findViewById(R.id.orderid);
            update = itemView.findViewById(R.id.update);

            second = itemView.findViewById(R.id.second);
            first = itemView.findViewById(R.id.first);



            //for getting id onclick
           first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null) {

                        onclick.onItemClick(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });



            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null){

                        onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });



            second.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null) {
                        onclick.onItemClick3(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });

        }


    }






}

