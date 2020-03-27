package com.example.throughpass.Main.fragments.selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.throughpass.Main.fragments.selection.recomRecyclerView.RecomRecyclerViewAdapter;
import com.example.throughpass.R;
import com.example.throughpass.obj.ChangeResvCodeService;
import com.example.throughpass.obj.GetRecomAttrCodeService;
import com.example.throughpass.obj.Prop;
import com.example.throughpass.obj.RecomResvCodeService;
import com.example.throughpass.obj.ResvAttractionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import static com.example.throughpass.obj.Prop.TAG;

public class RecommendPopup extends AppCompatActivity {
    RecyclerView recyclerView;
    RecomRecyclerViewAdapter adapter;
    Button cancelBtn;
    ArrayList<Prop.RecomResvResultData> recommendList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_popup);
        recommendList = new ArrayList<Prop.RecomResvResultData>();

        cancelBtn = findViewById(R.id.cancelBtn);
        recyclerView = findViewById(R.id.recomRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // 예약 추천받아오기
        getReservationAttrCode();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

    // 놀이기구 추천을 위해 예약된 놀이기구들의 고유 코드, 예약 순서를 가져옴
    @SuppressLint("CheckResult")
    public void getReservationAttrCode() {
        ResvAttractionService resvAttractionService = Prop.INSTANCE.getRetrofit().create(ResvAttractionService.class);
        Prop.ResvRideCodeData resvRideCodeData = new Prop.ResvRideCodeData(Prop.INSTANCE.getUser_nfc());
        resvAttractionService.resultRepos(resvRideCodeData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if(item.size() != 0) {
                        recomResvCode(item);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "현재 예약중인 놀이기구가 없습니다. \n예약 후 다시 시도해주세요!", Toast.LENGTH_LONG).show();
//                        finish();
                    }
                }, e->{
                    Log.d(TAG, "getReservationAttrCode : " + e);
                });
    }

    /*
    추천받는 API
     */
    @SuppressLint("CheckResult")
    public void recomResvCode(ArrayList<Prop.ResvRideResultData> list) {
        GetRecomAttrCodeService getRecomAttrCodeService = Prop.INSTANCE.getRetrofit().create(GetRecomAttrCodeService.class);
        Prop.RecomResvData recomResvData = new Prop.RecomResvData(list);
        getRecomAttrCodeService.resultRepos(recomResvData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if(item != null) {
                        Collections.sort(item, new RecomAscending());   // 기대 예상 시간이 짧은 순 정렬
                        recommendList = item;
                        setRecomRecyclerView(item);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "추천 에러!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, e->{
                    Log.d(TAG, "recomResvCode : " + e);
                });
    }

    // RecyclerView Data Insert
    private void setRecomRecyclerView(ArrayList<Prop.RecomResvResultData> list) {
        adapter = new RecomRecyclerViewAdapter(list);
        adapter.resetAll(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setRecyclerViewItemClick(list, adapter);
    }

    // 추천 리스트 예상 예약 시간으로 오름차순 정렬
    class RecomAscending implements Comparator<Prop.RecomResvResultData> {
        public int compare(Prop.RecomResvResultData a, Prop.RecomResvResultData b)
        {
            if(a.getExpect_wait_minute() < b.getExpect_wait_minute()) { // 오름차순 정렬
                return -1;
            }
            else if(a.getExpect_wait_minute() == b.getExpect_wait_minute()) {   // 같은 시간일 때는 예약 순위가 높은 것이 우선
                return a.getReservation_order() < b.getReservation_order() ? -1 : 1;
            }
            else {
                return 1;
            }
        }
    }

    /*
    RecyclerView Item 개별 클릭 리스너 설정하는 함수
     */
    private void setRecyclerViewItemClick(final ArrayList<Prop.RecomResvResultData> list, RecomRecyclerViewAdapter recomRecyclerViewAdapter) {
        recomRecyclerViewAdapter.setItemClick(new RecomRecyclerViewAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                /*
                1. 클릭한 아이템의 인덱스를 통해 저장된 데이터를 가져온다
                2. 해당 인덱스에 존재하는 데이터 삭제
                3. 해당 데이터 첫 번째에 삽입
                4. 예약 순서만 가져오기
                5. 예약 변경 API 호출
                 */
                Prop.RecomResvResultData selectedData = list.get(position);
                Log.d(TAG, "size : " + list.size());
                Log.d(TAG, "data : " + selectedData.getName());
                list.remove(position);
                Log.d(TAG, "size : " + list.size());
                list.add(0, selectedData);
                Log.d(TAG, "size : " + list.size());
                Log.d(TAG, "data : " + list.get(0).getName());

                ArrayList<Integer> resvOrderList = new ArrayList<Integer>();
                for(Prop.RecomResvResultData ele : list) {
                    Log.d(TAG, "ele : " + ele.getName());
                    resvOrderList.add(ele.getReservation_order());
                }

                changeReservation(resvOrderList);
            }
        });
    }

    /*
    선택한 놀이기구 우선 예약으로 변경
     */
    @SuppressLint("CheckResult")
    public void changeReservation(ArrayList<Integer> resvOrderList) {
        ChangeResvCodeService changeResvCodeService = Prop.INSTANCE.getRetrofit().create(ChangeResvCodeService.class);
        Prop.ChangeResvData changeResvData = new Prop.ChangeResvData(resvOrderList, Prop.INSTANCE.getUser_nfc());
        changeResvCodeService.resultRepos(changeResvData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if (item.getResult().equals("success")) {
                        Log.d(TAG, "SelectionFragment_DragAndDropList : success to send attraction code list");
                        Toast.makeText(getApplicationContext(), "놀이기구 예약 1순위가 변경되었습니다.", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }, e->{
                    Log.d(TAG, "changeReservation :  SERVER ERROR : " + e);
                });
    }
}
