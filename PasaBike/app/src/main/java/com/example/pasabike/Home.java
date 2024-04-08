 package com.example.pasabike;

 import android.os.Bundle;
 import android.view.MenuItem;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.fragment.app.Fragment;

 import com.google.android.material.bottomnavigation.BottomNavigationView;

 public class Home extends AppCompatActivity {

     private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        bottomNavigationView=findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_frag, new com.example.pasabike.Create_request()).commit();

    }


     private  BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
             new BottomNavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                     Fragment frag = null;

                     switch  (menuItem.getItemId()) {
                         case R.id.Home:
                             frag = new Create_request();

                             break;

                         case R.id.order_status:
                             frag = new Order_status();

                             break;

                         case R.id.order_history:
                             frag = new Record_history();
                             break;

                     }

                     getSupportFragmentManager().beginTransaction().replace(R.id.container_frag, frag).commit();

                     return true;

                 }



             };















}