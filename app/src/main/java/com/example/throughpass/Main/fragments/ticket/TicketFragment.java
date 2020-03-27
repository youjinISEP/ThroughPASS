package com.example.throughpass.Main.fragments.ticket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.throughpass.R;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RegisteredTodayTicketService;
import com.example.throughpass.obj.RemoveWaitCodeService;
import com.example.throughpass.obj.WaitAttractionInfoService;
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

    private View view;
    ArrayList<Prop.LostsData> lostsList = new ArrayList<Prop.LostsData>();
    ArrayList<Prop.NoticeData> noticesList = new ArrayList<Prop.NoticeData>();
    TimerTask noticeRefreshTimerTask;
    TimerTask lostsRefreshTimerTask;
    Timer noticeRefreshTimer;
    Timer lostsRefreshTimer;

    TextView ticketStatus, rideName, rideLocation, restTime, worldNotice, worldLosts;
    Button cancelRide;
    ProgressBar mainProgressbar;
    Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketStatus = view.findViewById(R.id.txt_tStatus);
        worldNotice = view.findViewById(R.id.txt_tWorldNotice);
        worldLosts = view.findViewById(R.id.txt_tWorldLosts);

        restTime = view.findViewById(R.id.txt_tResttime);
        mainProgressbar = view.findViewById(R.id.mainprogressbar);

        rideName = view.findViewById(R.id.txt_tRideName);
        rideLocation = view.findViewById(R.id.txt_tRideLoc);
        cancelRide = view.findViewById(R.id.btn_tCancel);

        fragment = this;

        checkStatusOfRide();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



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


    }

    //대기 신청 현황 데이터 가져오기
    @SuppressLint("CheckResult")
    public void checkStatusOfRide() {

        if (Prop.INSTANCE.getUser_nfc() != null) {
            //3. 대기 신청한 놀이기구 정보
            WaitAttractionInfoService waitAttractionInfoService = Prop.INSTANCE.getRetrofit().create(WaitAttractionInfoService.class);
            Prop.WaitRideInfoCodeData waitRideInfoCodeData = new Prop.WaitRideInfoCodeData(Prop.INSTANCE.getUser_nfc());

            waitAttractionInfoService.resultRepos(waitRideInfoCodeData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item.getAttr_code() == 0) {
                            Log.d("@@@@@", "TicketFragment_checkStatusOfRide : no item included in waitAttraction");
                            // rideName.setText("대기신청된 놀이기구가 없습니다.");
                            // rideLocation.setText("놀이기구를 대기 신청해주세요.");
                            //  waitMinute = 0;
                        } else {
                            rideName.setText(item.getName());
                            rideLocation.setText(item.getLocation());
                           // waitMinute = item.getWait_minute();
                            cancelRide.setOnClickListener(this::onClickCancel);
                        }
                    }, e -> {
                        Log.d("@@@@", "TicketFragment " + e);
                    });
        }
    }


    @SuppressLint("CheckResult")
    public void onClickCancel(View view) {
        switch (view.getId()) {
            case R.id.btn_tCancel:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("대기 신청")
                        .setMessage("대기 신청 취소하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Prop.INSTANCE.getUser_nfc() != null) {
                            RemoveWaitCodeService removeWaitCodeService = Prop.INSTANCE.getRetrofit().create(RemoveWaitCodeService.class);
                            Prop.RemWaitData remWaitData = new Prop.RemWaitData(Prop.INSTANCE.getUser_nfc());

                            removeWaitCodeService.resultRepos(remWaitData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(item -> {
                                                if (item.getResult().equals("success")) {
                                                    Log.d("@@@@", "TicketFragment_onClick : success to delete Waited Attraction");
                                                    assert getParentFragment() != null;
                                                    Func.INSTANCE.refreshFragment(fragment , getFragmentManager());
                                                }
                                            }, e -> {
                                                Log.d("@@@@", "TicketFragment_onClick : SERVER ERROR " + e);
                                            }
                                    );
                        }

                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


               // Func.INSTANCE.refreshFragment(this , getFragmentManager());
                break;
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
                            if (item != null) {
                                noticesList = item; // 이 방법으로 가능한지?

                                refreshPrintNotice();

                            }
                        }
                        , e -> {
                            //Toast.makeText(getActivity(), "공지사항이 없습니다.", Toast.LENGTH_LONG).show();
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
                            if (item != null) {
                                lostsList = item; // 이 방법으로 가능한지
                                refreshPrintLosts();
                            }
                        }
                        , e -> {
                            Toast.makeText(getActivity(), "분실물이 없습니다.", Toast.LENGTH_LONG).show();
                        });
    }

    // 분실물 갱신하는 타이머 포함된 함수
    private void refreshPrintLosts() {
        lostsRefreshTimerTask = new TimerTask() {
            int index = 0;

            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
//                Log.d(Prop.INSTANCE.getTAG(), "index : " + index + " , lostList.size " + lostsList.size());

                if(index >= lostsList.size()) {
//                    getTodayAllLosts();

                    index = 0;
                } else {
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
                } else {
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


}


/*
$ 이전 코드 백업

  // 티켓 등록 버튼 클릭 이벤트
/*        registBtn.setOnClickListener(new Button.OnClickListener() {
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
    }*/
/*
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
//            name.setText(" - ");
  //          registTime.setText(" - ");
        }
    }
*/
// 티켓 오늘 등록했었는지 확인, 값 저장하는 함수
 /*   @SuppressLint("CheckResult")
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
                            //setTicketInfo();
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
 */