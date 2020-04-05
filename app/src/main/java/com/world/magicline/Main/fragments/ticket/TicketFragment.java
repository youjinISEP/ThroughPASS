package com.world.magicline.Main.fragments.ticket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.world.magicline.Main.NFC.nfcActivity;
import com.world.magicline.R;
import com.world.magicline.obj.Func;
import com.world.magicline.obj.Prop;
import com.world.magicline.obj.RemoveWaitCodeService;
import com.world.magicline.obj.WaitAttractionInfoService;
import com.world.magicline.obj.WaitAttractionWaitMinuteService;
import com.world.magicline.obj.getAllLostsService;
import com.world.magicline.obj.getAllNoticeService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.world.magicline.obj.Prop.NFC_TAGGING;
import static com.world.magicline.obj.Prop.TAG;

public class TicketFragment extends Fragment {

    public TicketFragment() {}
    private View view;
    ArrayList<Prop.LostsData> lostsList = new ArrayList<Prop.LostsData>();
    ArrayList<Prop.NoticeData> noticesList = new ArrayList<Prop.NoticeData>();
    TimerTask noticeRefreshTimerTask;
    TimerTask lostsRefreshTimerTask;
    Timer noticeRefreshTimer;
    Timer lostsRefreshTimer;

    ViewPager eventViewPager;
    TextView ticketStatus, rideName, rideLocation, restTime, worldNotice, worldLosts, waitCount;
    Button cancelRide, nfcTagging;
    ProgressBar mainProgressbar;
    Fragment fragment;

    int waitAttrCode = 0;   // Default
    int pStatus = 0;
    private Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketStatus = view.findViewById(R.id.txt_tStatus);
//        worldNotice = view.findViewById(R.id.txt_tWorldNotice);
        worldLosts = view.findViewById(R.id.txt_tWorldLosts);
        eventViewPager = view.findViewById(R.id.eventViewPager);

        restTime = view.findViewById(R.id.txt_restTime);
        mainProgressbar = view.findViewById(R.id.mainprogressbar);
        mainProgressbar.setProgress(0);
        mainProgressbar.setSecondaryProgress(100);
        mainProgressbar.setMax(100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(pStatus < 100){
                    pStatus += 10;

                    handler.post(new Runnable(){

                        @Override
                        public void run() {
                            mainProgressbar.setProgress(pStatus);
                        }
                    });
                    try{
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        waitCount = view.findViewById(R.id.txt_waitCount);

        rideName = view.findViewById(R.id.txt_tRideName);
        rideLocation = view.findViewById(R.id.txt_tRideLoc);
        cancelRide = view.findViewById(R.id.btn_tCancel);
        nfcTagging = view.findViewById(R.id.btn_nfc);
        nfcTagging.setOnClickListener(this::onClick);

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
                            cancelRide.setOnClickListener(this::onClick);
                            startProgressbar();

                            getWaitMinuteOfWaitAttr(waitAttrCode);
                        }
                    }, e -> {
                        Log.d(TAG, "checkStatusOfRide " + e);
                    });
        }
    }

    public void startProgressbar(){

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tCancel:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("탑승 신청 취소")
                        .setMessage("탑승 신청을 취소하시겠습니까?");

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
                                                    Toast.makeText(getActivity(), "탑승 신청을 취소했습니다.", Toast.LENGTH_LONG).show();
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
            case R.id.btn_nfc:
                if(Func.INSTANCE.checkRegistTicket()) {
                    Intent intent = new Intent(getActivity(), nfcActivity.class);
                    //intent.putExtra("select",rideName);
                    startActivityForResult(intent, NFC_TAGGING);
                }
                else {
                    Toast.makeText(getActivity(), "탑승을 위해서는 먼저 티켓 등록이 필요합니다.", Toast.LENGTH_LONG).show();
                }
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
                                noticesList = (ArrayList<Prop.NoticeData>) item.clone(); // 이 방법으로 가능한지?

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
                                lostsList = (ArrayList<Prop.LostsData>) item.clone(); // 이 방법으로 가능한지
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
        eventViewPager.setAdapter(new EventViewPagerAdapter(getContext(), noticesList.size(), noticesList));
        noticeRefreshTimerTask = new TimerTask() {
            int index = 0;

            @Override
            public void run() {

                if(index >= noticesList.size()) {
//                    getTodayAllNotice();

                    index = 0;
                } else {

                    Prop.NoticeData data = noticesList.get(index);
                    eventViewPager.setCurrentItem(index);
//                    worldNotice.setText(data.getTitle());
                    index++;
                }
            }
        };
        noticeRefreshTimer = new Timer();
        noticeRefreshTimer.schedule(noticeRefreshTimerTask, 0, 5 * Prop.INSTANCE.getSECOND());
    }

//    ViewPager.OnPageChangeListener eventViewPagerChangeListener = new ViewPager.OnPageChangeListener() {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NFC_TAGGING) {
            if (resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                Func.INSTANCE.refreshFragment(fragment , getFragmentManager());
            }
        }
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