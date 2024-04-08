package com.example.pasabike;

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

public class My_record_adapter extends FirestoreRecyclerAdapter<transaction_db_model, My_record_adapter.transholder> {

    private My_record_adapter.OnClick onclick;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    public My_record_adapter(@NonNull FirestoreRecyclerOptions<transaction_db_model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull transholder holder, int position, @NonNull transaction_db_model model) {

            holder.fee.setText(model.getFee()+" "+"Php");
            holder.t_date.setText(model.getOrder_date().toDate().toString());
            holder.transaction_type.setText(model.getType());

            DocumentReference costumer = fstorage.collection("courrier_user").document(model.getCourrier_id());
            costumer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String customer_fn = snapshot.getString("fname") + " " + snapshot.getString("lname");
                        //String address = snapshot.getString("home_address");
                        holder.cour_name.setText(customer_fn);
                    }
                }
            });

    }

    @NonNull
    @Override
    public transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_transaction_holder, parent, false);
        return new My_record_adapter.transholder(view);
    }


    public class transholder extends RecyclerView.ViewHolder{

        TextView t_date;
        TextView cour_name;
        TextView fee;
        TextView transaction_type;


        public transholder(View view) {
            super(view);

            t_date = itemView.findViewById(R.id.t_date );
            cour_name = itemView.findViewById(R.id.cour_name);
            fee = itemView.findViewById(R.id.fee);
            transaction_type = itemView.findViewById(R.id.transaction_type);


        }
    }


    public class OnClick {
    }
}
