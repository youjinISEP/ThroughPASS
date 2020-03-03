package com.example.throughpass.Main.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegistTicketService;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WriteTicketCodePopup extends AppCompatActivity {
    Button okBtn, cancelBtn;
    EditText edTicketCode1, edTicketCode2, edTicketCode3, edTicketCode4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_ticket_code_popup);

        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        edTicketCode1 = findViewById(R.id.edTicketCode1);
        edTicketCode2 = findViewById(R.id.edTicketCode2);
        edTicketCode3 = findViewById(R.id.edTicketCode3);
        edTicketCode4 = findViewById(R.id.edTicketCode4);

        Intent intent = new Intent();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = getTicketCode();
                if(TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "코드 4자리씩 알맞게 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    if(registTicket(code)) {
                        intent.putExtra("ticketCode", code);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    /*
    * 티켓 등록
    * @return true : 티켓 등록 완료 // false : 티켓 등록 실패
     */
    @SuppressLint("CheckResult")
    public boolean registTicket(String code) {
        AtomicBoolean check = new AtomicBoolean(true);

        RegistTicketService registTicketService = Prop.INSTANCE.getRetrofit().create(RegistTicketService.class);
        Prop.RegistTicketData registTicketData =
                new Prop.RegistTicketData(code, Prop.INSTANCE.getUser_nfc());

        //noinspection ResultOfMethodCallIgnored
        registTicketService.resultRepos(registTicketData)
                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
                .subscribe(item -> { // 통신 결과로 받은 Object
                    if(item.getResult().equals("success")) {
                        Toast.makeText(getApplicationContext(), "티켓 등록을 완료했습니다.", Toast.LENGTH_LONG).show();
                        check.set(true);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), item.getResult(), Toast.LENGTH_LONG).show();
                        check.set(false);
                    }
                }
                , e -> {
                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다. \n 잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    check.set(false);
                });
        return check.get();
    }

    // EditText를 통해 티켓 코드 얻어오기
    public String getTicketCode() {
            if(edTicketCode1.getText().toString().length() < 4 || edTicketCode2.getText().toString().length() < 4
            || edTicketCode3.getText().toString().length() < 4 || edTicketCode4.getText().toString().length() < 4) {
                return null;
            }
            return edTicketCode1.getText().toString() + "-" + edTicketCode2.getText().toString() + "-"
                    + edTicketCode3.getText().toString() + "-" + edTicketCode4.getText().toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
