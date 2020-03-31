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
import static com.example.throughpass.obj.Prop.TAG;

public class RideDetailActivity extends AppCompatActivity {

    //TODO : image URL load 직접 선언 이외에는 로드 불가 에러처리하기

    public String name, imageURL, possibleRunTimeStr, locationStr, information;
    public int personnelCount;
    public Intent intent;
    ImageView rideImage;
    TextView attrName, attrTitle, attrDetailTitle, personHeight, personAge, location, possibleRunTime, personnel;
//    TextView rideInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail_fragment);

        intent = getIntent();
        if(intent == null) {
            Log.d(TAG, "상세정보 필요한 데이터 NULL");
            finish();
        }
        attrName = findViewById(R.id.txt_attrName);
        rideImage = findViewById(R.id.img_rDetail);
        attrTitle = findViewById(R.id.txt_attrTitle);
        attrDetailTitle = findViewById(R.id.txt_attrDetailTitle);
        personHeight = findViewById(R.id.txt_personHeight);
        personAge = findViewById(R.id.txt_personAge);
        location = findViewById(R.id.txt_location);
        possibleRunTime = findViewById(R.id.txt_possibleRunTime);
        personnel = findViewById(R.id.txt_personnel);


        name = intent.getExtras().getString("name");
        imageURL = intent.getExtras().getString("img");
        information = intent.getExtras().getString("info");
        locationStr = intent.getExtras().getString("location");
        possibleRunTimeStr = intent.getExtras().getString("startTime") + " ~ " + intent.getExtras().getString("endTime");
        personnelCount = intent.getIntExtra("personnel", -1);

        // DB와 통일
        String[] splitInfo = information.split(",");
        String height = splitInfo[0];
        String age = splitInfo[1];
        String title = splitInfo[2];
        String detailTitle = splitInfo[3];

       // String a = "http://adventure.lotteworld.com/image/2018/7/201807251058185011_1350.jpg";
        GlideApp.with(this)
                .load(imageURL)
                .dontTransform()
                .apply(new RequestOptions().override(500,500))
                .centerCrop()
                .into(this.rideImage);

        attrName.setText(name);
        attrTitle.setText(title);
        attrDetailTitle.setText(detailTitle);
        personHeight.setText(height);
        personAge.setText(age);
        location.setText(locationStr);
        possibleRunTime.setText(possibleRunTimeStr);
        personnel.setText(String.valueOf(personnelCount) + " 명");

    }
}
