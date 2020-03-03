package com.example.throughpass.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.TestService;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestActivity extends AppCompatActivity {
    Button button;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        button = findViewById(R.id.temp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TestService interface와 연결
                TestService testService = Prop.INSTANCE.getRetrofit().create(TestService.class);
                Prop.AddTicketData addTicketData =
                        new Prop.AddTicketData("1122-3333-4444-5555", null);

                //noinspection ResultOfMethodCallIgnored
                testService.resultRepos(addTicketData)
                        .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                        .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
//                .toObservable().
                        .subscribe(item -> { // 통신 결과로 받은 Object
                                    Toast.makeText(getApplicationContext(), item.getResult(), Toast.LENGTH_LONG).show();
                                }
                                , e -> {
                                    Toast.makeText(getApplicationContext(), "error : " + e, Toast.LENGTH_LONG).show();
                                    Log.e("TEST", "" + e);
                                });
            }
        });

//        // TestService interface와 연결
//        TestService testService = Prop.INSTANCE.getRetrofit().create(TestService.class);
//        Prop.AddTicketData addTicketData =
//                new Prop.AddTicketData("1122-3333-4444-5555", null);
//
//        //noinspection ResultOfMethodCallIgnored
//        testService.resultRepos(addTicketData)
//                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
//                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
////                .toObservable().
//                .subscribe(item -> {    // 통신 결과로 받은 Object
//                    Toast.makeText(getApplicationContext(), item.getResult(), Toast.LENGTH_LONG).show();
//                }
//                , e -> {
//                    Toast.makeText(getApplicationContext(), "error : " + e, Toast.LENGTH_LONG).show();
//                            Log.e("TEST", "" + e);
//                        });

        // temp
//        Prop.RegistTicketData registTicketData = new Prop.RegistTicketData("1111-abcd-2222-efgh", "1q2w3e4r5t");
//        testService.resultRepos(registTicketData)
//                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
//                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
////                .toObservable().timeInterval()
//                .subscribe(item -> {    // 통신 결과로 받은 Object
//                    Toast.makeText(getApplicationContext(), item.getResult(), Toast.LENGTH_LONG).show();
//                }
//                , e -> Toast.makeText(getApplicationContext(), "error : " + e, Toast.LENGTH_LONG).show());
    }


}
