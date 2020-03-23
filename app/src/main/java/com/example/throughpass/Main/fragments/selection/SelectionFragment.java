package com.example.throughpass.Main.fragments.selection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectItem;
import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectRecyclerTouchListener;
import com.example.throughpass.Main.fragments.selection.selectRecyclerview.SelectRecyclerViewAdapter;
import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RemoveWaitCodeService;
import com.example.throughpass.obj.RemvResvCodeService;
import com.example.throughpass.obj.ResvAttractionService;
import com.example.throughpass.obj.WaitAttractionInfoService;
import com.example.throughpass.obj.WaitAttractionService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SelectionFragment extends Fragment {

   private View view;
   private TabLayout tabLayout;
   private FrameLayout waitFrame, registFrame;
   private RecyclerView recyclerView;
   private SelectRecyclerViewAdapter recyclerViewAdapter;
   private SelectRecyclerTouchListener touchListener;
   private Toolbar toolbar;
   public List<SelectItem> selectList;
    private List<Integer> resvList;
   TextView rideName, rideLocation;
   ImageView rideImage;
   Button waitRideCancel, resRideRecommand;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_selection, container, false);

        //tab 클릭 이벤트
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    waitFrame.setVisibility(View.VISIBLE);
                    registFrame.setVisibility(View.INVISIBLE);
                }else if(tab.getPosition() == 1){
                    registFrame.setVisibility(View.VISIBLE);
                    waitFrame.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    registFrame.setVisibility(View.INVISIBLE);
                    waitFrame.setVisibility(View.VISIBLE);
                }else if(tab.getPosition() == 1){
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
        rideImage = view.findViewById(R.id.img_sWaitRide);
        waitRideCancel = view.findViewById(R.id.btn_sCancelWait);

        //registFrame
        registFrame = view.findViewById(R.id.registFrame);

        recyclerView = view.findViewById(R.id.regist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new SelectRecyclerViewAdapter(this.getContext());
        selectList = new ArrayList<>();

        SelectItem viewItem = new SelectItem(
                ContextCompat.getDrawable(getContext(),R.drawable.ic_dashboard_black_24dp),
                "자이로스핀",
                "20:31:12");
        selectList.add(viewItem);
        selectList.add(viewItem);
        selectList.add(viewItem);
        selectList.add(viewItem);
        selectList.add(viewItem);
        selectList.add(viewItem);


        recyclerViewAdapter.setItemList(selectList);
        recyclerView.setAdapter(recyclerViewAdapter);

        registItemClickListener();

        checkStatusOfRide();
        return view;
    }

    public void onResume(){
        super.onResume();
        recyclerView.addOnItemTouchListener(touchListener);
        DragAndDropList();

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_sCancelWait:
                RemoveWaitCodeService removeWaitCodeService = Prop.INSTANCE.getRetrofit().create(RemoveWaitCodeService.class);
                Prop.RemWaitData remWaitData = new Prop.RemWaitData(Prop.INSTANCE.getUser_nfc());

                break;
        }
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
                        if(item == null){

                        }else{
                            waitRideCancel.setOnClickListener(this::onClick);
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




    /**
     * 예약신청 TAB
     */

    public void showResvAttraction(){

    }
    public void DragAndDropList(){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target =  target.getAdapterPosition();

                Collections.swap(selectList,position_dragged,position_target);
                recyclerViewAdapter.notifyItemMoved(position_dragged, position_target);

                //9. 예약 신청된 놀이기구 drag & drop

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    public void registItemClickListener(){
        touchListener = new SelectRecyclerTouchListener(this,recyclerView);
        touchListener
                .setClickable(new SelectRecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        Log.d("@@@@@@@@", "click");
                        //recyclerview item 클릭시!
                        //Toast.makeText(getApplicationContext(),taskList.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) { }
                })
                .setSwipeOptionViews(R.id.delete_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new SelectRecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.delete_task:
                                //taskList.remove(position);
                                //recyclerviewAdapter.setTaskList(taskList);
                                //8. 예약 취소버튼
                                RemvResvCodeService remvResvCodeService = Prop.INSTANCE.getRetrofit().create(RemvResvCodeService.class);
                                Prop.RemResvData remResvData = new Prop.RemResvData(Prop.INSTANCE.getUser_nfc(), position);
                                remvResvCodeService.resultRepos(remResvData);
                                break;
                        }
                    }
                });
    }




}
