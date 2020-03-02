package com.example.throughpass.Main;

import android.os.Bundle;

import com.example.throughpass.R;
//import com.example.throughpass.obj.Func;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    // 뷰 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Define all of view item by FINDVIEWBYID
        BottomNavigationView navView = findViewById(R.id.nav_view);
        fab =  findViewById(R.id.floatingActionButton);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
