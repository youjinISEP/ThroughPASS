package com.world.magicline.Main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.world.magicline.Main.fragments.ride.RideFragment;
import com.world.magicline.Main.fragments.selection.SelectionFragment;
import com.world.magicline.Main.fragments.ticket.TicketFragment;
import com.world.magicline.R;
import com.world.magicline.obj.Prop;

import static com.world.magicline.obj.Prop.TAG;

// 메인아님
public class MainActivity extends AppCompatActivity  {
    private boolean backKeyPressedTwice = false;
    private FloatingActionButton fab;
    private BottomNavigationView navView;
    private FragmentManager fragmentManager;
    private TicketFragment ticketFragment = new TicketFragment();
    private RideFragment rideFragment = new RideFragment();
    private SelectionFragment selectionFragment = new SelectionFragment();
    private FragmentTransaction transaction;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        fab =  findViewById(R.id.floatingActionButton);
        fragmentManager = getSupportFragmentManager();

        fab.setVisibility(View.GONE);
        navView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.nav_host_fragment, ticketFragment).commitAllowingStateLoss();

//        Prop.INSTANCE.setUser_nfc(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));    // 안드로이드 ID 넣기

        Log.d(TAG, Prop.INSTANCE.getUser_nfc());
//        fab.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(Func.INSTANCE.checkRegistTicket()) {
//                    Intent intent = new Intent(MainActivity.this, nfcActivity.class);
//                    //intent.putExtra("select",rideName);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "탑승을 위해서는 먼저 티켓 등록이 필요합니다.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
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


    // 뒤로가기 이벤트
    // 뒤로가기 두 번 누를 경우 종료하도록 하는 코드 구현
    @Override
    public void onBackPressed() {
        if(backKeyPressedTwice) {
            super.onBackPressed();
            return;
        }

        backKeyPressedTwice = true;
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backKeyPressedTwice = false;
            }
        }, 2 * Prop.INSTANCE.getSECOND());
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
