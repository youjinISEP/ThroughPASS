package com.example.throughpass.Main.fragments.ride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import com.example.throughpass.R;

public class RideDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public String selectedRide;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail_fragment);

        intent = getIntent();
        toolbar = findViewById(R.id.toolbar);

        selectedRide = intent.getExtras().getString("select");

        //toolbar 세팅
        toolbar.setTitle(selectedRide);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
