package com.world.magicline.Main.fragments.ride;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.world.magicline.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerTouchListener;
import com.world.magicline.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerviewAdapter;
import com.world.magicline.Main.fragments.ride.swipeRecyclerview.ViewItem;
import com.world.magicline.Main.fragments.selection.selectRecyclerview.SelectItem;
import com.world.magicline.R;
import com.world.magicline.obj.AddResvCodeService;
import com.world.magicline.obj.AddWaitCodeService;
import com.world.magicline.obj.Func;
import com.world.magicline.obj.GetAllRideData;
import com.world.magicline.obj.Prop;
import com.world.magicline.obj.ResvAttractionService;
import com.world.magicline.obj.WaitAttractionService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.world.magicline.obj.Prop.ADD_RESERVATION_CODE;
import static com.world.magicline.obj.Prop.ADD_WAIT_CODE;
import static com.world.magicline.obj.Prop.TAG;

public class RideFragment extends Fragment {

    public RideFragment()   {};
    private View view;

    private RecyclerView recyclerView;
    private SwipeRecyclerviewAdapter recyclerviewAdapter;
    private SwipeRecyclerTouchListener touchListener;

    private List<ViewItem> vList;
    private List<Integer> resvList;
    private SelectItem selectItem;
    private ViewItem viewItem;
    private int rideSize;

    TextView waitCnt, resvCnt, resvTotal;
    Toolbar toolbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ride, container, false);

        //toolbar 설정 menu
      //  toolbar.setTitle("놀이기구 현황");


        //놀이기구 현황 숫자로 표현
        waitCnt = view.findViewById(R.id.txt_waitCnt);
        resvCnt = view.findViewById(R.id.txt_resvCnt);
        resvTotal = view.findViewById(R.id.txt_resvTotal);

        //recyclerview 표현
        recyclerView = view.findViewById(R.id.recycler_ridelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewAdapter = new SwipeRecyclerviewAdapter(this.getContext());
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        vList = new ArrayList<>();
        resvList = new ArrayList<>();

        checkStatusOfRide(); // 놀이기구 대기신청, 예약신청 상태 체크 -> showRideList(전체 놀이기구 리스트 보여주기)
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
                        if (item.getAttr_code() == 0) {
                            Log.d("@@@@@", "RideFragment_checkStatusOfRide : no item included in waitAttraction");
                            waitCnt.setText(0+"");
                            Prop.INSTANCE.setWait_attr_code(0);
                        } else {
                            Prop.INSTANCE.setWait_attr_code(item.getAttr_code());
                            waitCnt.setText(1+"");
                        }

                    }, e -> {
                        Log.d("@@@@", "RideFragment_checkStatusOfRide : SERVER ERROR " + e);
                    });

            //4. 예약 신청한 놀이기구 정보
            ResvAttractionService resvAttractionService = Prop.INSTANCE.getRetrofit().create(ResvAttractionService.class);
            Prop.ResvRideCodeData resvRideCodeData = new Prop.ResvRideCodeData(Prop.INSTANCE.getUser_nfc());

            resvAttractionService.resultRepos(resvRideCodeData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(item -> {
                        if (item.size() == 0) {
                            Log.d("@@@@@", "RideFragment_checkStatusOfRide : no item included in resvAttraction");
                            resvCnt.setText(0+"");
                            Prop.INSTANCE.setResvAttractionSize(0);
                        } else {
                            Prop.INSTANCE.setResvAttractionSize(item.size());
                            resvCnt.setText(item.size()+"");
                            for (int i = 0; i < item.size(); i++) {
                                resvList.add(item.get(i).getAttr_code());
                            }
                        }
                        Prop.INSTANCE.setReservationList(resvList);   //TODO : if-else 문 밖으로 뺐을때도 가능???
                        showRideList();  //모든 놀이기구 정보 받아오기
                    }, e -> {
                        Log.d("@@@@@", "RideFragment_checkStatusOfRide : SERVER ERROR " + e);
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
                    Prop.INSTANCE.setTotalSize(rideSize);
                    resvTotal.setText(rideSize+"");

                    for (int i = 0; i < rideSize; i++) {

                        viewItem = new ViewItem(
                                ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp),
                                item.get(i).getName(),
                                String.valueOf(item.get(i).getWait_minute())
                        );
                        viewItem.setWaitStatus(4);
                        viewItem.setResvStatus(4);
                        if (Prop.INSTANCE.getWait_attr_code() != null && Prop.INSTANCE.getWait_attr_code() == item.get(i).getAttr_code()) {
                            viewItem.setWaitStatus(0);
                        } else {
                            viewItem.setWaitStatus(4);
                        }
                        if (Prop.INSTANCE.getReservationList().size() > 0) {
                            for (int j = 0; j < Prop.INSTANCE.getReservationList().size(); j++) {
                                if (Prop.INSTANCE.getReservationList().get(j) == item.get(i).getAttr_code()) {
                                    viewItem.setResvStatus(0);
                                }
                            }
                        }
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
                        ViewItem data = vList.get(position);
                        intent.putExtra("name", data.getRide_Name());
                        intent.putExtra("img", data.getImg_url());
                        intent.putExtra("startTime", data.getStart_time());
                        intent.putExtra("location", data.getLocation());
                        intent.putExtra("endTime", data.getEnd_time());
                        intent.putExtra("personnel", data.getPersonnel());
                        intent.putExtra("info", data.getInfo());

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
                                    if (Prop.INSTANCE.getWait_attr_code() == 0) { //이미 대기 신청된 놀이기구가 없을 때
                                        /* notice test */
                                        Intent intent = new Intent(getActivity(), NoticePopup.class);
                                        intent.putExtra("type", "wait");
                                        intent.putExtra("attrCode", vList.get(position).getAttr_code());
                                        startActivityForResult(intent, ADD_WAIT_CODE);

                                        //5. 대기 신청 버튼 클릭이벤트//
                                    }

                                break;
                            case R.id.second_task:
                                // Toast.makeText(getApplicationContext(),"Edit Not Available",Toast.LENGTH_SHORT).show();
                                    /* notice test */
                                    Intent intent = new Intent(getActivity(), NoticePopup.class);
                                    intent.putExtra("type", "reservation");
                                    intent.putExtra("attrCode", vList.get(position).getAttr_code());
                                    startActivityForResult(intent, ADD_RESERVATION_CODE);

                                    //6. 예약 신청 버튼 클릭이벤트
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
                        Log.d(TAG, "RideFragment_addWait : success!! ADD WAIT ATTRACTION");
                        Toast.makeText(getActivity(), "해당 놀이기구 탑승을 신청했습니다.", Toast.LENGTH_LONG).show();
                        Func.INSTANCE.refreshFragment(this, this.getFragmentManager());
                    } else {
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
                    if (item.getResult().equals("success")) {
                        Log.d(TAG, "RideFragment_addReservation : success!!  ADD RESERVATION ATTRACTION");

                        Func.INSTANCE.refreshFragment(this, this.getFragmentManager());
                    } else {
                        Log.d(TAG, "RideFragment_addReservation : NOT INSERTED");
                    }
                }, e -> {
                    Toast.makeText(getActivity(), "서버 오류 입니다.", Toast.LENGTH_LONG).show();
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refreshDrawableState();
        //checkStatusOfRide();
        recyclerView.addOnItemTouchListener(touchListener);
    }



    public void onStart(){
        super.onStart();
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerviewAdapter.setItemList(vList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int attrCode = data.getIntExtra("attrCode", -1);
        if (attrCode != -1) {
            if (requestCode == ADD_WAIT_CODE) {
                if (resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                    addWait(attrCode);
                    Toast.makeText(getActivity(), "해당 놀이기구 탑승을 신청했습니다.", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == ADD_RESERVATION_CODE) {
                if (resultCode == Activity.RESULT_OK) {  // 티켓 등록 완료
                    addReservation(attrCode);
                    Toast.makeText(getActivity(), "예약이 추가되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}