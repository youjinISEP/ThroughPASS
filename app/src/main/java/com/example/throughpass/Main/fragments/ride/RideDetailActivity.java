package com.example.throughpass.Main.fragments.ride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.throughpass.Main.SSLexception.GlideApp;
import com.example.throughpass.R;

public class RideDetailActivity extends AppCompatActivity {

    //TODO : image URL load 직접 선언 이외에는 로드 불가 에러처리하기

    private Toolbar toolbar;
    public String selectedRide, imageURL, information;
    public Intent intent;
    ImageView rideImage;
    TextView rideInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail_fragment);

        intent = getIntent();
        toolbar = findViewById(R.id.toolbar);
        rideImage = findViewById(R.id.img_rDetail);
        rideInfo = findViewById(R.id.txt_rInfo);

        selectedRide = intent.getExtras().getString("name");
        imageURL = intent.getExtras().getString("img");
        information = intent.getExtras().getString("info");

        //toolbar 세팅
        toolbar.setTitle(selectedRide);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String a = "http://adventure.lotteworld.com/image/2018/7/201807251058185011_1350.jpg";
        GlideApp.with(this)
                .load(imageURL)
                .dontTransform()
                .apply(new RequestOptions().override(500,500))
                .centerCrop()
                .into(this.rideImage);

        rideInfo.setText(information);

    }
}
