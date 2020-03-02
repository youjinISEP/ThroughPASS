package com.example.throughpass.Main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.throughpass.Main.fragments.ride.RideFragment;
import com.example.throughpass.Main.fragments.selection.SelectionFragment;
import com.example.throughpass.Main.fragments.ticket.TicketFragment;
import com.example.throughpass.R;
//import com.example.throughpass.obj.Func;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity  {

    private FloatingActionButton fab;
    private BottomNavigationView navView;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private TicketFragment ticketFragment = new TicketFragment();
    private RideFragment rideFragment = new RideFragment();
    private SelectionFragment selectionFragment = new SelectionFragment();
    private FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        fab =  findViewById(R.id.floatingActionButton);

        navView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, ticketFragment).commitAllowingStateLoss();

    }

    //BottomNavigation Bar Control
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    transaction.replace(R.id.nav_host_fragment, ticketFragment).commitAllowingStateLoss();
                    break;
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.nav_host_fragment, rideFragment).commitAllowingStateLoss();
                    break;
                case R.id.navigation_notifications:
                    transaction.replace(R.id.nav_host_fragment, selectionFragment).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }



    @Override
    protected  void onResume(){
        super.onResume();

        //

        // 서버로부터 유저정보 및 상태값 호출

        /*if(Func.INSTANCE.getUserInfo()) {
            // 제대로 받았다
        } else {
            // 아니다
        }*/
    }



}
