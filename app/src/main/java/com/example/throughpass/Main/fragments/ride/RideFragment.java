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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerImageTextAdapter;
import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerItem;
import com.example.throughpass.Main.fragments.ride.recyclerview.RecyclerOnItemClick;
import com.example.throughpass.R;

import java.util.ArrayList;


public class RideFragment extends Fragment implements RecyclerOnItemClick  {

    private View view;
    RecyclerView mRecyclerView = null;
    RecyclerImageTextAdapter mAdapter = null;
    ArrayList<RecyclerItem> mList = new ArrayList<>();

    /*
    * recyclerview생성하기 //0303
    * item 선택되면 새로운 Activity 생성하기//0303
    * 페이지가 loading 될 때마다, json으로 DB에서 데이터 받아오기
    *
    */

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
        context.startActivity(intent);
    }


    //Todo LIST
    //페이지 onCreate()시에 DB에서 놀이기구 정보 불러와서 recyclerview에 삽입하기

}