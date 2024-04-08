package com.example.pasabike;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class My_order_adapter extends FirestoreRecyclerAdapter<transaction_db_model, My_order_adapter.transholder> {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private My_order_adapter.OnClick onclick;

    public My_order_adapter(@NonNull FirestoreRecyclerOptions<transaction_db_model> options, OnClick onclick) {
        super(options);

        this.onclick = onclick;
    }

    @Override
    protected void onBindViewHolder(@NonNull transholder holder, int position, @NonNull transaction_db_model model) {

        holder.orderid.setText(model.getOrder_id());
        holder.courrier_fee.setText(model.getFee()+" "+"Php");


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

        if (model.getStatus() == 1) {
            holder.report.setVisibility(View.INVISIBLE);
        }


        if (model.getStatus() == 2) {
            holder.zero.setVisibility(View.VISIBLE);
            DocumentReference courrier = fstorage.collection("courrier_user").document(model.getCourrier_id());
            courrier.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String courrier_fn = snapshot.getString("fname") + " " + snapshot.getString("lname");
                        holder.courrier_name.setText(courrier_fn);
                    }
                }
            });

        } else if (model.getStatus() == 3) {
            holder.zero.setVisibility(View.VISIBLE);
            holder.uno.setVisibility(View.VISIBLE);
            holder.down1.setVisibility(View.VISIBLE);
        }else if (model.getStatus() == 4) {
            holder.zero.setVisibility(View.VISIBLE);
            holder.dos.setVisibility(View.VISIBLE);
            holder.uno.setVisibility(View.VISIBLE);
            holder.down1.setVisibility(View.VISIBLE);
            holder.down2.setVisibility(View.VISIBLE);
        }else if (model.getStatus() == 5) {
            holder.zero.setVisibility(View.VISIBLE);
            holder.uno.setVisibility(View.VISIBLE);
            holder.dos.setVisibility(View.VISIBLE);
            holder.tres.setVisibility(View.VISIBLE);
            holder.down1.setVisibility(View.VISIBLE);
            holder.down2.setVisibility(View.VISIBLE);
            holder.down3.setVisibility(View.VISIBLE);
            holder.confirm.setVisibility(View.VISIBLE);

        }











    }






    @NonNull
    @Override
    public transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_request, parent, false);
        return new transholder(view);
      
    }


    public class transholder extends RecyclerView.ViewHolder {

        TextView customer_name;
        TextView courrier_name;

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
        RelativeLayout uno, zero;
        RelativeLayout dos;
        RelativeLayout tres;

        RelativeLayout second, first;

        Button confirm;
        ImageView report;


        public transholder(@NonNull View itemView) {
            super(itemView);
            down1 = itemView.findViewById(R.id.down1);
            down2 = itemView.findViewById(R.id.down2);
            down3 = itemView.findViewById(R.id.down3);
            zero= itemView.findViewById(R.id.uno);
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
            orderlists = itemView.findViewById(R.id.order_lists);
            total= itemView.findViewById(R.id.total);
            orderid = itemView.findViewById(R.id.orderid);
            confirm = itemView.findViewById(R.id.confirm_button);
            report = itemView.findViewById(R.id.report);


            second = itemView.findViewById(R.id.second);
            first = itemView.findViewById(R.id.first);


            /*
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
            */

            //second click
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && onclick != null) {
                        onclick.onItemClick2(getSnapshots().getSnapshot(postion), postion);
                    }
                }
            });

            //third click
            report.setOnClickListener(new View.OnClickListener() {
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


        public interface OnClick {

            //void onItemClick(DocumentSnapshot snapshot, int position);
            void onItemClick2(DocumentSnapshot snapshot, int position);
            void onItemClick3(DocumentSnapshot snapshot, int position);

        }

}
