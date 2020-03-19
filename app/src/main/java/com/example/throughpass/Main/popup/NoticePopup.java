package com.example.throughpass.Main.popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.throughpass.R;
import com.example.throughpass.obj.Func;

public class NoticePopup extends AppCompatActivity {
    Button okBtn, cancelBtn;
    String type;
    TextView noticeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_popup);

        noticeText = findViewById(R.id.txt_notice);
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        Intent intent = getIntent();
        if(intent == null) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }

        type = intent.getStringExtra("type");
        String notice = Func.INSTANCE.readFromAssets(typeToFileName(type), getApplicationContext());
        noticeText.setText(notice);
        noticeText.setMovementMethod(new ScrollingMovementMethod());

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 동의 이벤트 진행
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

    private String typeToFileName(String type) {
        switch (type) {
            case "wait":
                return "wait_notice.txt";
            case "reservation":
                return "reservation_notice.txt";
            default:
                return null;
        }
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
