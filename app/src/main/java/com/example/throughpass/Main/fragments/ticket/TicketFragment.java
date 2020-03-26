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

import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegisteredTodayTicketService;
import com.example.throughpass.obj.getAllLostsService;
import com.example.throughpass.obj.getAllNoticeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.throughpass.obj.Prop.TICKET_POPUP_CODE;
import static com.example.throughpass.obj.Prop.TAG;

public class TicketFragment extends Fragment {
    ImageButton registBtn;
    TextView ticketStatus, name, registTime, worldNotice, worldLosts;
    private View view;
    ArrayList<Prop.LostsData> lostsList = new ArrayList<Prop.LostsData>();
    ArrayList<Prop.NoticeData> noticesList = new ArrayList<Prop.NoticeData>();
    TimerTask noticeRefreshTimerTask;
    TimerTask lostsRefreshTimerTask;
    Timer noticeRefreshTimer;
    Timer lostsRefreshTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_ticket, container, false);
        registBtn = (ImageButton) view.findViewById(R.id.btn_tRegist);
        ticketStatus = view.findViewById(R.id.txt_tStatus);
        worldNotice = view.findViewById(R.id.txt_tWorldNotice);
        worldLosts = view.findViewById(R.id.txt_tWorldLosts);
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
        setTicketInfo();

        /*
        공지사항 갱신
         */
        getTodayAllNotice();
//        refreshPrintNotice();
        /*
        분실물 갱신
         */
        getTodayAllLosts();
//        refreshPrintLosts();

        // 티켓 등록 버튼 클릭 이벤트
        registBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    // 공지사항 데이터 가져오기
    @SuppressLint("CheckResult")
    private void getTodayAllNotice() {
        getAllNoticeService getAllNoticeService = Prop.INSTANCE.getRetrofit().create(getAllNoticeService.class);

        //noinspection ResultOfMethodCallIgnored
        getAllNoticeService.resultRepos()
                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
                .subscribe(item -> { // 통신 결과로 받은 Object
                            if(item != null) {
                                noticesList = item; // 이 방법으로 가능한지?
//                                Log.d(TAG, "--------------- getTodayAllNotice");
                                refreshPrintNotice();
                            }
                        }
                        , e -> {
                            Toast.makeText(getActivity(), "공지사항이 없습니다.", Toast.LENGTH_LONG).show();
                        });
    }

    // 분실물 데이터 가져오기
    @SuppressLint("CheckResult")
    private void getTodayAllLosts() {
        getAllLostsService getAllLostsService = Prop.INSTANCE.getRetrofit().create(getAllLostsService.class);

        //noinspection ResultOfMethodCallIgnored
        getAllLostsService.resultRepos()
                .subscribeOn(Schedulers.io())   // 데이터를 보내는 쓰레드 및 함수
                .observeOn(AndroidSchedulers.mainThread())  // 데이터를 받아서 사용하는 쓰레드 및 함수
                .subscribe(item -> { // 통신 결과로 받은 Object
                            if(item != null) {
                                lostsList = item; // 이 방법으로 가능한지
//                                Log.d(TAG, "--------------- getTodayAllLosts");
                                refreshPrintLosts();
                            }
                        }
                        , e -> {
//                            Toast.makeText(getActivity(), "분실물이 없습니다.", Toast.LENGTH_LONG).show();
                        });
    }

    // 분실물 갱신하는 타이머 포함된 함수
    private void refreshPrintLosts() {
        lostsRefreshTimerTask = new TimerTask() {
            int index = 0;

            @Override
            public void run() {
//                Log.d(Prop.INSTANCE.getTAG(), "index : " + index + " , lostList.size " + lostsList.size());
                if(index >= lostsList.size()) {
//                    getTodayAllLosts();
                    index = 0;
                }
                else {
                    Prop.LostsData data = lostsList.get(index);
                    worldLosts.setText("(" + data.getClassification() + ") " + data.getName() + " 보관중.\n" + data.getLocation() + " 위치에서 습득");
                    index++;
                }
            }
        };

        lostsRefreshTimer = new Timer();
        lostsRefreshTimer.schedule(lostsRefreshTimerTask, 0, 2 * Prop.INSTANCE.getSECOND());
    }

    // 분실물 갱신하는 타이머 포함된 함수
    private void refreshPrintNotice() {
        noticeRefreshTimerTask = new TimerTask() {
            int index = 0;

            @Override
            public void run() {
                if(index >= noticesList.size()) {
//                    getTodayAllNotice();
                    index = 0;
                }
                else {
                    Prop.NoticeData data = noticesList.get(index);
                    worldNotice.setText(data.getTitle());
                    index++;
                }
            }
        };
        noticeRefreshTimer = new Timer();
        noticeRefreshTimer.schedule(noticeRefreshTimerTask, 0, 5 * Prop.INSTANCE.getSECOND());
    }


    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyVIew");
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        lostsRefreshTimer.cancel();
        noticeRefreshTimer.cancel();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
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