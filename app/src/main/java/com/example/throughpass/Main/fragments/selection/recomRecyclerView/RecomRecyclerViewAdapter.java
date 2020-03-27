package com.example.throughpass.Main.fragments.selection.recomRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.R;
import com.example.throughpass.obj.Prop;

import java.util.ArrayList;


/*
SearchActivity RecyclerView의 Adapter
 */
public class RecomRecyclerViewAdapter extends RecyclerView.Adapter<RecomRecyclerViewAdapter.ItemViewHolder> {

    // adapter에 들어갈 list
    private ArrayList<Prop.RecomResvResultData> recommendList;
    private Context context;

    //아이템 클릭시 실행 함수
    private ItemClick itemClick;

    public interface ItemClick {
        public void onClick(View view,int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public RecomRecyclerViewAdapter(ArrayList<Prop.RecomResvResultData> arrayList) {
        recommendList = arrayList;
    }

    public void resetAll(ArrayList<Prop.RecomResvResultData> newArrayList) { ;

        this.recommendList = null;
        this.recommendList = newArrayList;
    }


    // RecyclerView의 핵심인 ViewHolder
    // 여기서 subView를 setting
    class ItemViewHolder extends RecyclerView.ViewHolder {
        View view;
        private TextView attrNameText;
        private TextView rankText;
        private TextView respectWaitMinuteText;

        ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            attrNameText = itemView.findViewById(R.id.attrNameText);
            rankText = itemView.findViewById(R.id.rankText);
            respectWaitMinuteText = itemView.findViewById(R.id.respectWaitMinuteText);
        }

        void onBind(Prop.RecomResvResultData data) {
            attrNameText.setText(data.getName());
            respectWaitMinuteText.setText(String.valueOf(data.getExpect_wait_minute()) + " 분");
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate.
        // return 인자는 ViewHolder.
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        // Item을 하나, 하나 보여주는(bind 되는)
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null) {
                    itemClick.onClick(v, position);
                }
            }
        });
        int rank = position + 1;
        holder.rankText.setText(rank + "순위");  // 순위만 여기서 setText
        holder.onBind(recommendList.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수
        return recommendList.size();
    }

    void addItem(Prop.RecomResvResultData recomResvResultData) {
        // 외부에서 item을 추가시킬 함수
        recommendList.add(recomResvResultData);
    }
}
