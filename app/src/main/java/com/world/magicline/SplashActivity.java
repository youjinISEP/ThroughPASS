package com.world.magicline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.world.magicline.Main.MainActivity;
import com.world.magicline.Main.WriteTicketCodeActivity;
import com.world.magicline.obj.Func;
import com.world.magicline.obj.Prop;
import com.world.magicline.obj.RegisteredTodayTicketService;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.world.magicline.obj.Prop.TAG;
import static com.world.magicline.obj.Prop.TUTORIAL_CODE;

/**
 * 스플래시 화면
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prop.INSTANCE.setUser_nfc(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));    // 안드로이드 ID 넣기

        // 앱을 처음 설치했는지 확인하는 코드
//        SharedPreferences pref = getSharedPreferences("checkFirst", AppCompatActivity.MODE_PRIVATE);
//        boolean checkFirst = pref.getBoolean("checkFirst", false);

        boolean checkFirst = false;
        // 앱 최초 실행 시
        if (!checkFirst) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putBoolean("checkFirst", true);
//            editor.commit();

            Intent intent = new Intent(this, TutorialActivity.class);
            startActivityForResult(intent, TUTORIAL_CODE);
        } else {
            checkTodayRegisteredTicket();
        }
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
                    if (item.getResult().equals("success")) {
                        Prop.INSTANCE.setTicketCode(item.getTicket_code());
                        Prop.INSTANCE.setRegistDate(item.getReg_date());

                        Date date = new Date(Prop.INSTANCE.getRegistDate().longValue());
                        String strDate = Func.INSTANCE.formatDateKST(date);
                        Prop.INSTANCE.setRegistDateStr(strDate);

                        Toast.makeText(getApplicationContext(), "금일 등록한 티켓이 존재합니다.\n환영합니다!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TUTORIAL_CODE) {
            if (resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                checkTodayRegisteredTicket();
            }
        }
    }
}