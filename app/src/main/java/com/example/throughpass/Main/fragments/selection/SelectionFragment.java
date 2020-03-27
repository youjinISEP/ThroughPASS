package com.example.throughpass.Main.fragments.selection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.DialogInterface;

import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.throughpass.obj.RemvResvCodeService;
import com.example.throughpass.obj.ResvAttractionInfoService;
import com.example.throughpass.obj.WaitAttractionInfoService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.throughpass.obj.Prop.RECOMMEND_CODE;

public class SelectionFragment extends Fragment {

    private View view;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private SelectRecyclerViewAdapter recyclerViewAdapter;
    private SelectRecyclerTouchListener touchListener;
    public List<SelectItem> selectList;
    private SelectItem selectItem;
    private ArrayList<Integer> attrCode;
    private Button waitRideCancel, resRideRecommand;
    Fragment fragment;
    FragmentManager fragmentManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_selection, container, false);

        //toolbar 설정 menu
        toolbar = view.findViewById(R.id.toolbar);
       // toolbar.setTitle("현재 신청 상황");

        recyclerView = view.findViewById(R.id.regist_recyclerview);
        resRideRecommand = view.findViewById(R.id.btn_sRecommand);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new SelectRecyclerViewAdapter(this.getContext());
        selectList = new ArrayList<>();

        fragment = this;

        showResvAttraction(); //예약 신청한 놀이기구 정보
        DragAndDropList();
        registItemClickListener();

        return view;
    }

    public void onResume() {
        super.onResume();
        recyclerView.addOnItemTouchListener(touchListener);
    }

   @SuppressLint("CheckResult")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sRecommand:
                Intent intent = new Intent(getActivity(), RecommendPopup.class);
                startActivityForResult(intent, RECOMMEND_CODE);
                break;
        }
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
                        if (item.size() == 0) {
                            Log.d("@@@@", "SelectionFragment_showResvAttraction : item is null");
                        } else {
                            for (int i = 0; i < item.size(); i++) {
                                selectItem = new SelectItem(item.get(i).getAttr_code(),
                                        item.get(i).getName(),
                                        item.get(i).getImg_url(),
                                        item.get(i).getWait_minute()+"분",
                                        item.get(i).getReservation_order());

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

    public void DragAndDropList() {

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @SuppressLint("CheckResult")
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(selectList, position_dragged, position_target);
                Prop.INSTANCE.setReservationInfoList(selectList);
                recyclerViewAdapter.notifyItemMoved(position_dragged, position_target);

                Prop.INSTANCE.setReservationInfoList(selectList);
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
                                assert getFragmentManager() != null;
                                Func.INSTANCE.refreshFragment(fragment , getFragmentManager());
                            }
                        }, e -> {
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
                    public void onRowClicked(int position) {
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                    }
                })
                .setSwipeOptionViews(R.id.delete_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new SelectRecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.delete_task:

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("대기 신청")
                                        .setMessage("대기 신청 취소하시겠습니까?");

                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @SuppressLint("CheckResult")
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RemvResvCodeService remvResvCodeService = Prop.INSTANCE.getRetrofit().create(RemvResvCodeService.class);
                                        Prop.RemResvData remResvData = new Prop.RemResvData(Prop.INSTANCE.getUser_nfc(), selectList.get(position).getReservationOrder());
                                        remvResvCodeService.resultRepos(remResvData)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(item -> {
                                                    if (item.getResult().equals("success")) {

                                                        assert getFragmentManager() != null;
                                                        Func.INSTANCE.refreshFragment(fragment , getFragmentManager());
                                                        Log.d("@@@@", "SelectionFragment_registItemClickListener : success to delete reserved Attraction");
                                                    } else {
                                                        Log.d("@@@@", "SelectionFragment_resgistItemClickListener : Unable to delete reserved Attraction");
                                                    }
                                                }, e -> {
                                                    Log.d("@@@@", "SelectionFragment_checkStatusOfRide : SERVER ERROR " + e);
                                                });

                                    }
                                });

                                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                                //8. 예약 취소버튼

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
