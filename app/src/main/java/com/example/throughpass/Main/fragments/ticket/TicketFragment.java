package com.example.throughpass.Main.fragments.ticket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.throughpass.Main.popup.NoticePopup;
import com.example.throughpass.Main.popup.WriteTicketCodePopup;
import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegisteredTodayTicketService;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TicketFragment extends Fragment {
    final static int TICKET_POPUP_CODE = 111;
    ImageButton registBtn;
    TextView ticketStatus, name, registTime;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_ticket, container, false);
        registBtn = (ImageButton) view.findViewById(R.id.btn_tRegist);
        ticketStatus = view.findViewById(R.id.txt_tStatus);
        name = view.findViewById(R.id.txt_rName);
        registTime = view.findViewById(R.id.txt_rRegistTime);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        이전에 티켓 등록 했는지
        찾아보기
        */
        Log.d(Prop.INSTANCE.getTAG(), " ㄹ??");
        if(Func.INSTANCE.checkRegistTicket()) {
            setTicketInfo();
        }
        else {
            checkTodayRegisteredTicket();
        }




        // 티켓 등록 버튼 클릭 이벤트
        registBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* notice test */
//                Intent intent = new Intent(getActivity(), NoticePopup.class);
//                intent.putExtra("type", "wait");
//                startActivity(intent);


                if(Func.INSTANCE.checkRegistTicket()) {
                    Toast.makeText(getActivity(), "현재 티켓이 등록되어 있습니다. \n 최대 등록 횟수 : 1회", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), WriteTicketCodePopup.class);
                    startActivityForResult(intent, TICKET_POPUP_CODE);
                }

            }
        });
    }

    // 티켓 정보 바 변경
    private void setTicketInfo() {
        if(Func.INSTANCE.checkRegistTicket()) {  // 티켓 정보가 등록되어있다면
            ticketStatus.setBackgroundResource(R.color.colorTrueTicketInfoBox);
            ticketStatus.setText(R.string.trueTicketInfoBoxTxt);
            name.setText(Prop.INSTANCE.getTicketCode());
            registTime.setText(Prop.INSTANCE.getRegistDateStr());
        }
        else {  // 티켓 정보가 등록되어있지 않다면
            ticketStatus.setBackgroundResource(R.color.colorFalseTicketInfoBox);
            ticketStatus.setText(R.string.falseTicketInfoBoxTxt);
            name.setText(" - ");
            registTime.setText(" - ");
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
                            Prop.INSTANCE.setTicketCode(item.getTicket_code());
                            Prop.INSTANCE.setRegistDate(item.getReg_date());

                            Date date = new Date(Prop.INSTANCE.getRegistDate().longValue());
                            String strDate = Func.INSTANCE.formatDateKST(date);
                            Prop.INSTANCE.setRegistDateStr(strDate);
                            Log.d(Prop.INSTANCE.getTAG(), " ㅁㄴㅇ");
                            setTicketInfo();
                        }
                        , e -> {
                            Toast.makeText(getActivity(), "기존 티켓 등록 찾기 오류가 발생했습니다. \n 잠시후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TICKET_POPUP_CODE) {
            if(resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                Toast.makeText(getActivity(), "ticket : " + Prop.INSTANCE.getTicketCode() + " \n " + Prop.INSTANCE.getRegistDateStr(), Toast.LENGTH_LONG).show();
                Func.INSTANCE.refreshFragment(this, getFragmentManager());
            }
        }
    }
}