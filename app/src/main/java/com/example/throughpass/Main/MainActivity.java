package com.example.throughpass.Main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.throughpass.Main.NFC.nfcActivity;
import com.example.throughpass.Main.fragments.ride.RideFragment;
import com.example.throughpass.Main.fragments.selection.SelectionFragment;
import com.example.throughpass.Main.fragments.ticket.TicketFragment;
import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;
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

    /*
     * 티켓이 등록되어있지 않으면, 다른 fragment로 넘어가지 못하게 막기
     * NFC 버튼 활성화
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        fab =  findViewById(R.id.floatingActionButton);

        navView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, ticketFragment).commitAllowingStateLoss();

        Prop.INSTANCE.setUser_nfc(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));    // 안드로이드 ID 넣기

        Log.d("@@@@@@@@", "user nfc "+Prop.INSTANCE.getUser_nfc());
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, nfcActivity.class);
                //intent.putExtra("select",rideName);
                startActivity(intent);
            }
        });
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
    }



}
