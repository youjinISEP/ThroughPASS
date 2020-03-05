package com.example.throughpass.Main.fragments.ride;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerImageTextAdapter;
import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerItem;
import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerOnItemClick;
import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerTouchListener;
import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.SwipeRecyclerviewAdapter;
import com.example.throughpass.Main.fragments.ride.swipeRecyclerview.ViewItem;
import com.example.throughpass.R;

import java.util.ArrayList;
import java.util.List;

/*
 * recyclerview생성하기 //0303
 * item 선택되면 새로운 Activity 생성하기//0303
 * 페이지가 loading 될 때마다, json으로 DB에서 데이터 받아오기
 *
 */

public class RideFragment extends Fragment  {

    private RecyclerView recyclerView;
    private SwipeRecyclerviewAdapter recyclerviewAdapter;
    private SwipeRecyclerTouchListener touchListener;
    private View view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ride, container,false);
        recyclerView = view.findViewById(R.id.recycler_ridelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));

        recyclerviewAdapter = new SwipeRecyclerviewAdapter(this.getContext());
        final List<ViewItem> vList = new ArrayList<>();

        ViewItem viewItem = new ViewItem(
                ContextCompat.getColor(getContext(), R.color.colorLotteLogo),
                ContextCompat.getDrawable(getContext(),R.drawable.ic_dashboard_black_24dp),
                "자이로스핀",
                "20:31:12");
        vList.add(viewItem);

        recyclerviewAdapter.setItemList(vList);
        recyclerView.setAdapter(recyclerviewAdapter);

        touchListener = new SwipeRecyclerTouchListener(this,recyclerView);
        touchListener
                .setClickable(new SwipeRecyclerTouchListener.OnRowClickListener() {

                    @Override
                    public void onRowClicked(int position) {
                        Log.d("@@@@@@@@", "click");
                        //recyclerview item 클릭시!
                        //Toast.makeText(getApplicationContext(),taskList.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.first_task,R.id.second_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new SwipeRecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID){
                            case R.id.first_task:
                                //taskList.remove(position);
                                //recyclerviewAdapter.setTaskList(taskList);
                                break;
                            case R.id.second_task:
                               // Toast.makeText(getApplicationContext(),"Edit Not Available",Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addOnItemTouchListener(touchListener);
    }


    /*

    implements RecyclerOnItemClick
     private View view;
    RecyclerView mRecyclerView = null;
    RecyclerImageTextAdapter mAdapter = null;
    ArrayList<RecyclerItem> mList = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ride, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_ridelist);
        mAdapter = new RecyclerImageTextAdapter(getContext(),mList,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addItem();
        mAdapter.notifyDataSetChanged();
        return view;
    }

    //recycler view에 item 추가하기
    public void addItem(){
        RecyclerItem item = new RecyclerItem();

        int dataSize = 10; //db테이블의 data의 수

        for(int i=0; i<dataSize; i++){
            item.setRideImage(ContextCompat.getDrawable(getContext(),R.drawable.ic_add_circle_outline_24px));
            item.setRideName("자이로스핀");
            item.setRideRestTime("50");
            item.setRideLocation("매직아일랜드 회전그네 옆");

            mList.add(item);
        }
    }

    //recyclerview의 아이템 클릭시, 그 놀이기구에 대한 상세페이지(activity)로 넘어감
    @Override
    public void onClick(View v, String rideName) {
        Context context = v.getContext();
        Intent intent = new Intent(context, RideDetailActivity.class);
        intent.putExtra("select",rideName);
        context.startActivity(intent);
    }

     */


    //Todo LIST
    //페이지 onCreate()시에 DB에서 놀이기구 정보 불러와서 recyclerview에 삽입하기

}