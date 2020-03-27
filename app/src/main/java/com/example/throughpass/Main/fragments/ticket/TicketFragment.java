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
import com.example.throughpass.obj.WaitAttractionWaitMinuteService;
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

    TextView ticketStatus, rideName, rideLocation, restTime, worldNotice, worldLosts, waitCount;
    Button cancelRide;
    ProgressBar mainProgressbar;
    Fragment fragment;

    int waitAttrCode = 0;   // Default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketStatus = view.findViewById(R.id.txt_tStatus);
        worldNotice = view.findViewById(R.id.txt_tWorldNotice);
        worldLosts = view.findViewById(R.id.txt_tWorldLosts);

        restTime = view.findViewById(R.id.txt_restTime);
        mainProgressbar = view.findViewById(R.id.mainprogressbar);
        waitCount = view.findViewById(R.id.txt_waitCount);

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
                            Log.d(TAG, "일로오냐");
                            // rideName.setText("대기신청된 놀이기구가 없습니다.");
                            // rideLocation.setText("놀이기구를 대기 신청해주세요.");
                            //  waitMinute = 0;
                        } else {
                            waitAttrCode = item.getAttr_code();
                            rideName.setText(item.getName());
                            rideLocation.setText(item.getLocation());
                           // waitMinute = item.getWait_minute();
                            cancelRide.setOnClickListener(this::onClickCancel);

                            getWaitMinuteOfWaitAttr(waitAttrCode);
                        }
                    }, e -> {
                        Log.d(TAG, "checkStatusOfRide " + e);
                    });
        }
    }

//    WaitAttractionWaitMinuteService{
//        @POST("/attr/getWaitMinuteOfWaitAttr")
//        fun resultRepos(@Body waitMinuteOfWaitAttrData: Prop.WaitMinuteOfWaitAttrData ) : Single<Prop.WaitMinuteInfoData>
//    }
    // 잔여 대기 시간, 몇 번째인지 가져오기
    @SuppressLint("CheckResult")
    public void getWaitMinuteOfWaitAttr(int attrCode) {
        WaitAttractionWaitMinuteService waitAttractionWaitMinuteService = Prop.INSTANCE.getRetrofit().create(WaitAttractionWaitMinuteService.class);
        Prop.WaitMinuteOfWaitAttrData waitMinuteOfWaitAttrData = new Prop.WaitMinuteOfWaitAttrData(Prop.INSTANCE.getUser_nfc(), attrCode);

        waitAttractionWaitMinuteService.resultRepos(waitMinuteOfWaitAttrData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if (item != null) {
                        waitCount.setText(String.valueOf(item.getCount()));
                        restTime.setText(String.valueOf(item.getWait_minute()));
                    } else {
                        waitCount.setText("-");
                        restTime.setText("-");
                    }
                }, e -> {
                    Log.d(TAG, "getWaitMinuteOfWaitAttr " + e);
                });

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
        lostsRefreshTimer.schedule(lostsRefreshTimerTask, 0, 3 * Prop.INSTANCE.getSECOND());
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