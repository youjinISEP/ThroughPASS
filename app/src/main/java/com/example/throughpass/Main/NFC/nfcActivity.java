package com.example.throughpass.Main.NFC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.NfcTaggingService;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegistTicketService;

import java.util.Date;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class nfcActivity extends AppCompatActivity {
    final int TIME = 40;
    int remainTime = TIME;
    TextView nfcTime;
    private Toolbar nfcToolbar;
    final Handler handler = new Handler();
    CountDownTimer countDownTimer;
    boolean backKeyPressedTwice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        nfcTime = findViewById(R.id.txt_nfcTime);
        nfcToolbar = findViewById(R.id.toolbar);
        nfcToolbar.setTitle("NFC 태그 활성화");

        // timer 사용해서 매 초마다 활성화 시키기
        countDownTimer = new CountDownTimer(TIME * 1000, 1000) {
            // 반복 실행할 구문
            @Override
            public void onTick(long millisUntilFinished) {
                remainTime--;
                nfcTimeTextRefreshed();
                // 태그 갱신(?)
            }

            // 마지막 구문
            @Override
            public void onFinish() {
                finish();
            }
        };

        countDownTimer.start();

        // 서버에 NFC 태그 전송 Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(Prop.INSTANCE.getTAG(), "NFC 태그 전송중");
                nfcTagging();
            }
        }, 5 * Prop.INSTANCE.getSECOND());
    }

    private void nfcTagging() {
        NfcTaggingService nfcTaggingService = Prop.INSTANCE.getRetrofit().create(NfcTaggingService.class);
        Prop.NfcTaggingData taggingData = new Prop.NfcTaggingData(Prop.INSTANCE.getUser_nfc(), 3);

        //noinspection ResultOfMethodCallIgnored
        nfcTaggingService.resultRepos(taggingData)
                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
                .subscribe(item -> { // 통신 결과로 받은 Object
                            if(item.getResult().equals("success")) {
                                Toast.makeText(getApplicationContext(), "탑승 확인되었습니다.\n즐거운 놀이기구 되세요!", Toast.LENGTH_LONG).show();
                                // 대기 신청 놀이기구 0으로 변경
                            }
                            else {
                                Toast.makeText(getApplicationContext(), item.getResult(), Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                        , e -> {
                            Toast.makeText(getApplicationContext(), "오류가 발생했습니다. \n 잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        });
    }
    protected void nfcTimeTextRefreshed() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                nfcTime.setText(remainTime + " 초");
            }
        };
        handler.post(runnable);
    }

    // 뒤로가기 이벤트
    // 뒤로가기 두 번 누를 경우 종료하도록 하는 코드 구현
    @Override
    public void onBackPressed() {
        if(backKeyPressedTwice) {
            countDownTimer.cancel();
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
        }, 2000);
    }
}
