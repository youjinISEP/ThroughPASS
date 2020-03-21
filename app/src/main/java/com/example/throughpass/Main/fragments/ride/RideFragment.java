package com.example.throughpass.Main.fragments.ride;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerTouchListener;
import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerviewAdapter;
import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.ViewItem;
import com.example.throughpass.R;
import com.example.throughpass.obj.AddResvCodeService;
import com.example.throughpass.obj.AddWaitCodeService;
import com.example.throughpass.obj.Func;
import com.example.throughpass.obj.GetAllRideData;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.ResvAttractionService;
import com.example.throughpass.obj.WaitAttractionService;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.throughpass.obj.Prop.ADD_RESERVATION_CODE;
import static com.example.throughpass.obj.Prop.ADD_WAIT_CODE;
import static com.example.throughpass.obj.Prop.TAG;
import static com.example.throughpass.obj.Prop.TICKET_POPUP_CODE;


public class RideFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRecyclerviewAdapter recyclerviewAdapter;
    private SwipeRecyclerTouchListener touchListener;
    private View view;
    private List<ViewItem> vList;
    private List<Integer> resvList;

    private int rideSize;
    private ViewItem viewItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ride, container, false);
        recyclerView = view.findViewById(R.id.recycler_ridelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        recyclerviewAdapter = new SwipeRecyclerviewAdapter(this.getContext());
        vList = new ArrayList<>();
        resvList = new ArrayList<>();


        checkStatusOfRide(); //대기신청 or 예약신청 된 놀이기구의 정보를 받아오기
        showRideList(); //전체적인 놀이기구의 종류를 받아와서 LIST로 보여주기
        recyclerviewClickeventAction(); // 놀이기구 상세페이지, 대기신청, 예약신청
        return view;
    }

    @SuppressLint("CheckResult")
    public void checkStatusOfRide() {

        if (Prop.INSTANCE.getUser_nfc() != null) {
            //3. 대기 신청한 놀이기구 정보
            WaitAttractionService waitAttractionService = Prop.INSTANCE.getRetrofit().create(WaitAttractionService.class);
            Prop.WaitRideCodeData waitRideCodeData = new Prop.WaitRideCodeData(Prop.INSTANCE.getUser_nfc());

            waitAttractionService.resultRepos(waitRideCodeData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item == null) {
                            Log.d("@@@@@", "error! no item included");
                        } else {
                            Prop.INSTANCE.setWait_attr_code(item.getAttr_code());
                            Log.d("@@@@@", "item.getAttr_code: " + item.getAttr_code());
                        }

                    });

            //4. 예약 신청한 놀이기구 정보
            ResvAttractionService resvAttractionService = Prop.INSTANCE.getRetrofit().create(ResvAttractionService.class);
            Prop.ResvRideCodeData resvRideCodeData = new Prop.ResvRideCodeData(Prop.INSTANCE.getUser_nfc());

            resvAttractionService.resultRepos(resvRideCodeData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item == null) {
                            Log.d("@@@@@", "item is null");
                        } else {
                            for (int i = 0; i < item.size(); i++) {
                                resvList.add(item.get(i).getAttr_code());
                                Log.d("@@@@@@", "att_order" + item.get(i).getReservation_order());
                            }
                        }

                    }, e -> {
                        Log.d("@@@@@@@@@", "error: ");
                    });
        }
    }

    @SuppressLint("CheckResult")
    public void showRideList() {
        //2. 모든 놀이기구 정보 받아오기
        GetAllRideData getAllRideData = Prop.INSTANCE.getRetrofit().create(GetAllRideData.class);
        getAllRideData.resultRepos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    rideSize = item.size();

                    for (int i = 0; i < rideSize; i++) {

                        viewItem = new ViewItem(
                                ContextCompat.getColor(getContext(), R.color.colorLotteLogo),
                                ContextCompat.getDrawable(getContext(), R.drawable.ic_dashboard_black_24dp),
                                item.get(i).getName(),
                                String.valueOf(item.get(i).getWait_minute())
                        );

                        viewItem.setAttr_code(item.get(i).getAttr_code());
                        viewItem.setName(item.get(i).getName());
                        viewItem.setPersonnel(item.get(i).getPersonnel());
                        viewItem.setRun_time(item.get(i).getRun_time());
                        viewItem.setStart_time(item.get(i).getStart_time());
                        viewItem.setEnd_time(item.get(i).getEnd_time());
                        viewItem.setLocation(item.get(i).getLocation());
                        viewItem.setCoordinate(item.get(i).getCoordinate());
                        viewItem.setImg_url(item.get(i).getImg_url());
                        viewItem.setInfo(item.get(i).getInfo());
                        viewItem.setWait_minute(item.get(i).getWait_minute());
                        vList.add(viewItem);
                    }

                    recyclerView.setAdapter(recyclerviewAdapter);
                    recyclerviewAdapter.setItemList(vList);
                });
    }

    public void recyclerviewClickeventAction() {
        touchListener = new SwipeRecyclerTouchListener(this, recyclerView);
        touchListener
                .setClickable(new SwipeRecyclerTouchListener.OnRowClickListener() {

                    @Override
                    public void onRowClicked(int position) {
                        Intent intent = new Intent(view.getContext(), RideDetailActivity.class);
                        intent.putExtra("name", vList.get(position).getRide_Name());
                        intent.putExtra("img", vList.get(position).getImg_url());
                        Log.d(TAG, "this : " + vList.get(position).getImg_url());
                        intent.putExtra("info", vList.get(position).getInfo());

                        // 놀이기구 이미지, 상세 설명 보내기
                        view.getContext().startActivity(intent);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.first_task, R.id.second_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new SwipeRecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.first_task:
                                //taskList.remove(position);
                                //recyclerviewAdapter.setTaskList(taskList);

                                if (Func.INSTANCE.checkRegistTicket()) {   // 티켓 등록 되어있을 떄
                                    if (Prop.INSTANCE.getWait_attr_code() != null) { //이미 대기 신청된 놀이기구가 없을 때
                                        /* notice test */
                                        Intent intent = new Intent(getActivity(), NoticePopup.class);
                                        intent.putExtra("type", "wait");
                                        intent.putExtra("attrCode", vList.get(position).getAttr_code());
                                        startActivityForResult(intent, ADD_WAIT_CODE);

                                        //5. 대기 신청 버튼 클릭이벤트
//
                                    }
                                }
                                break;
                            case R.id.second_task:
                                // Toast.makeText(getApplicationContext(),"Edit Not Available",Toast.LENGTH_SHORT).show();
                                if(Func.INSTANCE.checkRegistTicket()){
                                    /* notice test */
                                    Intent intent = new Intent(getActivity(), NoticePopup.class);
                                    intent.putExtra("type", "reservation");
                                    intent.putExtra("attrCode", vList.get(position).getAttr_code());
                                    startActivityForResult(intent, ADD_RESERVATION_CODE);

                                    //6. 예약 신청 버튼 클릭이벤트

                                 }
                                break;

                            default:
                                break;


                        }
                    }
                });

    }

    @SuppressLint("CheckResult")
    public void addWait(int attrCode) {
        AddWaitCodeService addWaitCodeService = Prop.INSTANCE.getRetrofit().create(AddWaitCodeService.class);
        Prop.AddWaitData addWaitData = new Prop.AddWaitData(Prop.INSTANCE.getUser_nfc(), attrCode);
        addWaitCodeService.resultRepos(addWaitData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if (item.getResult().equals("success")) {
                        Log.d(TAG, "success!! : ADD WAIT ATTRACTION");
                        Func.INSTANCE.refreshFragment(this, this.getFragmentManager());
                    }
                    else {
                        Toast.makeText(getActivity(), item.getResult(), Toast.LENGTH_LONG).show();
                    }
                }, e -> {
                    Toast.makeText(getActivity(), "서버 오류 입니다.", Toast.LENGTH_LONG).show();
                });
    }

    @SuppressLint("CheckResult")
    public void addReservation(int attrCode) {
        AddResvCodeService addResvCodeService = Prop.INSTANCE.getRetrofit().create(AddResvCodeService.class);
        Prop.AddResvData addResvData = new Prop.AddResvData(Prop.INSTANCE.getUser_nfc(), attrCode);
        addResvCodeService.resultRepos(addResvData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if(item.getResult().equals("success")) {
                        Log.d(TAG, "success!! : ADD RESERVATION ATTRACTION");
                        Func.INSTANCE.refreshFragment(this, this.getFragmentManager());
                    }
                    else {
                        Log.d(TAG, "NOT INSERTED");
                    }
                }, e -> {
                    Toast.makeText(getActivity(), "서버 오류 입니다.", Toast.LENGTH_LONG).show();
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addOnItemTouchListener(touchListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int attrCode = data.getIntExtra("attrCode", -1);
        if(attrCode != -1) {
            if(requestCode == ADD_WAIT_CODE) {
                if(resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                    addWait(attrCode);
//                    Func.INSTANCE.refreshFragment(this, getFragmentManager());
                }
            }

            if(requestCode == ADD_RESERVATION_CODE) {
                if(resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                    addReservation(attrCode);
//                    Func.INSTANCE.refreshFragment(this, getFragmentManager());
                }
            }
        }

    }
}