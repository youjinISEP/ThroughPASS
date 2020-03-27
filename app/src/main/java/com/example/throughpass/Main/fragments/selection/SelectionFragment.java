package com.example.throughpass.Main.fragments.selection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectItem;
import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectRecyclerTouchListener;
import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectRecyclerViewAdapter;
import com.example.throughpass.R;
import com.example.throughpass.obj.ChangeResvCodeService;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RemoveWaitCodeService;
import com.example.throughpass.obj.RemvResvCodeService;
import com.example.throughpass.obj.ResvAttractionInfoService;
import com.example.throughpass.obj.WaitAttractionInfoService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.throughpass.obj.Prop.RECOMMEND_CODE;

public class SelectionFragment extends Fragment {

    private View view;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private FrameLayout waitFrame, registFrame;

    private RecyclerView recyclerView;
    private SelectRecyclerViewAdapter recyclerViewAdapter;
    private SelectRecyclerTouchListener touchListener;
    public List<SelectItem> selectList;
    private SelectItem selectItem;

    private long timeCountInMilliSeconds = 1 * 60000;
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TextView rideName, rideLocation, textViewTime;
    private Button waitRideCancel, resRideRecommand;
    private ProgressBar progressBarCircle;
    private CountDownTimer countDownTimer;
    private Integer waitMinute;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_selection, container, false);

        //tab 클릭 이벤트
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    waitFrame.setVisibility(View.VISIBLE);
                    registFrame.setVisibility(View.INVISIBLE);
                } else if (tab.getPosition() == 1) {
                    registFrame.setVisibility(View.VISIBLE);
                    waitFrame.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    registFrame.setVisibility(View.INVISIBLE);
                    waitFrame.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    waitFrame.setVisibility(View.INVISIBLE);
                    registFrame.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //toolbar 설정 menu
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("현재 신청 상황");

        //waitFrame
        waitFrame = view.findViewById(R.id.waitFrame);
        rideName = view.findViewById(R.id.txt_sWaitRideName);
        rideLocation = view.findViewById(R.id.txt_sWaitRideLocat);
        textViewTime = view.findViewById(R.id.txt_sWaitRide);
        progressBarCircle = view.findViewById(R.id.progressBar);
        waitRideCancel = view.findViewById(R.id.btn_sCancelWait);

        //registFrame
        registFrame = view.findViewById(R.id.registFrame);
        resRideRecommand = view.findViewById(R.id.btn_sRecommand);

        recyclerView = view.findViewById(R.id.regist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new SelectRecyclerViewAdapter(this.getContext());
        selectList = new ArrayList<>();


        checkStatusOfRide();  //대기 신청한 놀이기구 정보
        showResvAttraction(); //예약 신청한 놀이기구 정보
        DragAndDropList();
        registItemClickListener();

        return view;
    }

    public void onResume() {
        super.onResume();

        recyclerView.addOnItemTouchListener(touchListener);

        //startStop();

    }


    /**
     * 대기신청 TAB
     */
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
                        if (item == null) {
                            Log.d("@@@@@", "SelectionFragment_checkStatusOfRide : no item included in waitAttraction");
                           // rideName.setText("대기신청된 놀이기구가 없습니다.");
                           // rideLocation.setText("놀이기구를 대기 신청해주세요.");
                          //  waitMinute = 0;
                        } else {
                            rideName.setText(item.getName());
                            rideLocation.setText(item.getLocation());
                            waitRideCancel.setOnClickListener(this::onClick);
                        }
                    }, e -> {
                        Log.d("@@@@", "aaa " + e);
                    });
        }
    }

    @SuppressLint("CheckResult")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sCancelWait:

                if (Prop.INSTANCE.getUser_nfc() != null) {
                    RemoveWaitCodeService removeWaitCodeService = Prop.INSTANCE.getRetrofit().create(RemoveWaitCodeService.class);
                    Prop.RemWaitData remWaitData = new Prop.RemWaitData(Prop.INSTANCE.getUser_nfc());

                    removeWaitCodeService.resultRepos(remWaitData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(item -> {
                                        if (item.getResult().equals("success")) {
                                            Log.d("@@@@", "SelectionFragment_onClick : success to delete Waited Attraction");
                                            Func.INSTANCE.refreshFragment(this, this.getFragmentManager());
                                        }
                                    }, e -> {
                                        Log.d("@@@@", "SelectionFragment_onClick : SERVER ERROR " + e);
                                    }
                            );
                }
                break;
            case R.id.btn_sRecommand:
                Intent intent = new Intent(getActivity(), RecommendPopup.class);
                startActivityForResult(intent, RECOMMEND_CODE);
                break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            // call to initialize the timer values
            setTimerValues(waitMinute);
            // call to initialize the progress bar values
            setProgressBarValues();
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues(int time) {
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }
        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    /**
     * 예약신청 TAB
     */

    @SuppressLint("CheckResult")
    public void showResvAttraction() {
        //4. 예약 신청한 놀이기구 정보

        if (Prop.INSTANCE.getUser_nfc() != null) {
            ResvAttractionInfoService resvAttractionInfoService = Prop.INSTANCE.getRetrofit().create(ResvAttractionInfoService.class);
            Prop.ResvRideInfoCodeData resvRideInfoCodeData = new Prop.ResvRideInfoCodeData(Prop.INSTANCE.getUser_nfc());

            resvAttractionInfoService.resultRepos(resvRideInfoCodeData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item == null) {
                            Log.d("@@@@", "SelectionFragment_showResvAttraction : item is null");
                        } else {
                            for (int i = 0; i < item.size(); i++) {
                                selectItem = new SelectItem(item.get(i).getAttr_code());
                                selectItem.setRide_Name(item.get(i).getName());
                                selectItem.setRestTime(item.get(i).getWait_minute() + "분");
                                selectItem.setRide_Image(item.get(i).getImg_url());
                                selectList.add(selectItem);
                                Prop.INSTANCE.setReservationInfoList(selectList);
                            }
                        }
                        recyclerViewAdapter.setItemList(selectList);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        resRideRecommand.setOnClickListener(this::onClick);
                    }, e -> {
                        Log.d("@@@@", "SelectionFragment_showResvAttraction : SERVER ERROR ");
                    });
        }
    }

    ArrayList<Integer> attrCode;

    public void DragAndDropList() {

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @SuppressLint("CheckResult")
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(selectList, position_dragged, position_target);
                Prop.INSTANCE.setReservationList(selectList);
                recyclerViewAdapter.notifyItemMoved(position_dragged, position_target);

                Prop.INSTANCE.setReservationList(selectList);
                attrCode = new ArrayList<>();
                for (int i = 0; i < selectList.size(); i++) {
                    attrCode.add(selectList.get(i).getRide_Code());
                }

                    //9. 예약 신청된 놀이기구 drag & drop
                    ChangeResvCodeService changeResvCodeService = Prop.INSTANCE.getRetrofit().create(ChangeResvCodeService.class);
                    Prop.ChangeResvData changeResvData = new Prop.ChangeResvData(attrCode, Prop.INSTANCE.getUser_nfc());
                    changeResvCodeService.resultRepos(changeResvData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(item -> {
                                if (item.getResult().equals("success")) {
                                    Log.d("@@@@", "SelectionFragment_DragAndDropList : success to send attraction code list");
                                }
                            }, e->{
                                Log.d("@@@@", "SelectionFragment_DragAndDropList :  SERVER ERROR");
                            });

                return false;
            }



            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);



    }

    public void registItemClickListener() {
        touchListener = new SelectRecyclerTouchListener(this, recyclerView);
        touchListener
                .setClickable(new SelectRecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) { }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) { }
                })
                .setSwipeOptionViews(R.id.delete_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new SelectRecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.delete_task:
                                //8. 예약 취소버튼
                                RemvResvCodeService remvResvCodeService = Prop.INSTANCE.getRetrofit().create(RemvResvCodeService.class);
                                Prop.RemResvData remResvData = new Prop.RemResvData(Prop.INSTANCE.getUser_nfc(), Prop.INSTANCE.getReservationInfoList().get(position).getRide_Code());
                                remvResvCodeService.resultRepos(remResvData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(item -> {
                                            if (item.getResult().equals("success")) {
                                                Log.d("@@@@", "SelectionFragment_registItemClickListener : success to delete reserved Attraction");
                                            } else {
                                                Log.d("@@@@", "SelectionFragment_resgistItemClickListener : Unable to delete reserved Attraction");
                                            }
                                        });
                                break;
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RECOMMEND_CODE) {
            if(resultCode == Activity.RESULT_OK) {  // 추천 종료
                Func.INSTANCE.refreshFragment(this, getFragmentManager());
            }
        }
    }
}
