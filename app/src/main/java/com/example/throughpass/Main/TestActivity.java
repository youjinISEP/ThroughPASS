package com.example.throughpass.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.TestService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends AppCompatActivity {
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);



        // TestService interface와 연결
        TestService testService = retrofit.create(TestService.class);

//        testService.resultRepos()

    }


}
