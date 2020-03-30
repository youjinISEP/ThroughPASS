package com.example.throughpass;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.throughpass.Main.MainActivity;
import com.example.throughpass.Main.WriteTicketCodeActivity;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegisteredTodayTicketService;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import static com.example.throughpass.obj.Prop.TAG;

/**
 * 스플래시 화면
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prop.INSTANCE.setUser_nfc(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));    // 안드로이드 ID 넣기
        checkTodayRegisteredTicket();
    }

    // 티켓 오늘 등록했었는지 확인, 값 저장하는 함수
    @SuppressLint("CheckResult")
    private void checkTodayRegisteredTicket() {
        RegisteredTodayTicketService registeredTodayTicketService = Prop.INSTANCE.getRetrofit().create(RegisteredTodayTicketService.class);
        Prop.RegisteredTodayTicketData registeredTodayTicketData = new Prop.RegisteredTodayTicketData(Prop.INSTANCE.getUser_nfc());

        //noinspection ResultOfMethodCallIgnored
        registeredTodayTicketService.resultRepos(registeredTodayTicketData)
                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
                .subscribe(item -> { // 통신 결과로 받은 Object
                    if(item.getResult().equals("success")) {
                        Prop.INSTANCE.setTicketCode(item.getTicket_code());
                        Prop.INSTANCE.setRegistDate(item.getReg_date());

                        Date date = new Date(Prop.INSTANCE.getRegistDate().longValue());
                        String strDate = Func.INSTANCE.formatDateKST(date);
                        Prop.INSTANCE.setRegistDateStr(strDate);

                        Toast.makeText(getApplicationContext(), "금일 등록한 티켓이 존재합니다.\n환영합니다!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "금일 등록한 티켓이 없습니다. 새로 등록해주세요.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), WriteTicketCodeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }, e -> {
                    Toast.makeText(getApplicationContext(), "서버 오류입니다. 앱을 재실행하세요.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "checkTodayRegisteredTicket " + e);
                });
            }
    }




//package com.example.throughpass;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.example.throughpass.Main.MainActivity;
//import com.example.throughpass.Main.WriteTicketCodeActivity;
//import com.example.throughpass.obj.Func;
//import com.example.throughpass.obj.Prop;
//import com.example.throughpass.obj.RegisteredTodayTicketService;
//
//import java.util.Date;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//import static com.example.throughpass.obj.Prop.TAG;
//
//
//public class SplashActivity extends AppCompatActivity {
//    Handler handler;
//    Runnable runnable;
//    ImageView splashView;
//    final int SPLASH_TIME = 2500;
//    boolean checkTicket = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        splashView = findViewById(R.id.splashView);
//
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                Intent intent;
//                if(checkTicket) {
//                    Toast.makeText(getApplicationContext(), "금일 등록한 티켓이 존재합니다.\n환영합니다!", Toast.LENGTH_LONG).show();
//                    intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "금일 등록한 티켓이 없습니다. 새로 등록해주세요.", Toast.LENGTH_LONG).show();
//                    intent = new Intent(getApplicationContext(), WriteTicketCodeActivity.class);
//                    startActivity(intent);
//                }
//                finish();
//            }
//        };
//
//
//        Glide.with(this)
//                .load(R.drawable.magicline_launcher2)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(splashView);
//
//        Prop.INSTANCE.setUser_nfc(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));    // 안드로이드 ID 넣기
//
//        if(!Func.INSTANCE.checkRegistTicket()) {
//            checkTodayRegisteredTicket();
//        }
//
//        handler = new Handler();
//        handler.postDelayed(runnable, SPLASH_TIME);
//    }
//
//    // 티켓 오늘 등록했었는지 확인, 값 저장하는 함수
//    @SuppressLint("CheckResult")
//    private void checkTodayRegisteredTicket() {
//        RegisteredTodayTicketService registeredTodayTicketService = Prop.INSTANCE.getRetrofit().create(RegisteredTodayTicketService.class);
//        Prop.RegisteredTodayTicketData registeredTodayTicketData = new Prop.RegisteredTodayTicketData(Prop.INSTANCE.getUser_nfc());
//
//        //noinspection ResultOfMethodCallIgnored
//        registeredTodayTicketService.resultRepos(registeredTodayTicketData)
//                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
//                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
//                .subscribe(item -> { // 통신 결과로 받은 Object
//                            Prop.INSTANCE.setTicketCode(item.getTicket_code());
//                            Prop.INSTANCE.setRegistDate(item.getReg_date());
//
//                            Date date = new Date(Prop.INSTANCE.getRegistDate().longValue());
//                            String strDate = Func.INSTANCE.formatDateKST(date);
//                            Prop.INSTANCE.setRegistDateStr(strDate);
//                            checkTicket = true;
//                        }
//                        , e -> {
//                            checkTicket = false;
//                            Log.d(TAG, " 0" + e);
//                        });
//    }
//}
