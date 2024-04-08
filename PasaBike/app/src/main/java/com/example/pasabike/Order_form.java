package com.example.pasabike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.UUID;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Order_form extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    Button create_order;
    EditText items, est_amount;
    Spinner store_lists;

    String store = "";
    //String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        store_lists = findViewById(R.id.store_list);
        store_lists.setOnItemSelectedListener(this);
        create_order = findViewById(R.id.create_order);
        items = findViewById(R.id.items_delivered);
        est_amount = findViewById(R.id.recipient_addres);

        //order_id = UUID.randomUUID().toString().substring(0,6);
        //Toast.makeText(getApplicationContext(),order_id, Toast.LENGTH_SHORT).show();


        create_transaction();

    }




    public void create_transaction() {
        final DocumentReference docRef = fstorage.collection( "transactions").document();

        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(items.getText().toString().isEmpty() || est_amount.getText().toString().isEmpty( ) || store.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "All fields are Required.", Toast.LENGTH_SHORT).show();
                } else {

                    String order_id = UUID.randomUUID().toString().substring(0,6);
                    String store_name = store;
                    String item_list = items.getText().toString();
                    String estimate  = est_amount.getText().toString();


                    Map<String,Object> transactions = new HashMap<>();
                    Map<String,Object> orders = new HashMap<>();

                    transactions.put("courrier_id", "" );
                    transactions.put("customer_id", fAuth.getCurrentUser().getUid().toString() );
                    transactions.put("fee", 30 );
                    transactions.put("order_date", Timestamp.now());
                    transactions.put("order_id", order_id);
                    transactions.put("status", 1);
                    transactions.put("type", "Pasabuy");



                    orders.put("estimated_amount", estimate);
                    orders.put("exact_amount", "");
                    orders.put("items", item_list);
                    orders.put("order_establishment", store_name);


                    //for transaction
                    docRef.set(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                            }else {
                                Toast.makeText(getApplicationContext(),"Request Failed. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    //for orders
                    final DocumentReference docRef2 = fstorage.collection( "orders").document(order_id);
                    docRef2.set(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(),"Pasabuy Request Successfully Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(),"Order Failed. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
        });



    }








    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
        if(parent.getSelectedItemPosition() == 1) {
            store = "Alfamart";
        }else if(parent.getSelectedItemPosition() == 2) {
            store = "7-11";
        }else if(parent.getSelectedItemPosition() == 3) {
            store = "South Emerald";
        }else if(parent.getSelectedItemPosition() == 4) {
            store = "PureGold";
        }else if(parent.getSelectedItemPosition() == 5) {
            store = "GoodWill Mart";
        }else if(parent.getSelectedItemPosition() == 6) {
            store = "M and W";
        }else if(parent.getSelectedItemPosition() == 7) {
            store = "Jollibee";
        }else if(parent.getSelectedItemPosition() == 8) {
            store = "McDonalds";
        }else if(parent.getSelectedItemPosition() == 9) {
            store = "Chowking";
        }else if(parent.getSelectedItemPosition() == 10) {
            store = "Mang Inasal";
        }else if(parent.getSelectedItemPosition() == 11) {
            store = "KFC";
        }else if(parent.getSelectedItemPosition() == 12) {
            store = "Shakeys";
        }else if(parent.getSelectedItemPosition() == 13) {
            store = "Goldilucks";
        }else if(parent.getSelectedItemPosition() == 14) {
            store = "Red Ribbon";
        }else if(parent.getSelectedItemPosition() == 15) {
            store = "Public Market";
        }else if(parent.getSelectedItemPosition() == 16) {
            store = "Sunstar WalterMArt";
        }else if(parent.getSelectedItemPosition() == 17) {
            store = "Metro Robinsons Supermart";
        }else if(parent.getSelectedItemPosition() == 18) {
            store = "Savemore Hypermart";
        }else if(parent.getSelectedItemPosition() == 19) {
            store = "Any Store";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}