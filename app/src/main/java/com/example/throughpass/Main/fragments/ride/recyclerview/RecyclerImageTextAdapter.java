package com.example.throughpass.Main.fragments.ride.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.R;

import java.util.ArrayList;

//Recyclerview에 표시될 아이템 뷰를 생성하는 역할.
public class RecyclerImageTextAdapter extends  RecyclerView.Adapter<RecyclerImageTextAdapter.ViewHolder> {
    public ArrayList<RecyclerItem> mData;
    public RecyclerOnItemClick mCallback;
    Context context;

    //instructor 생성. 데이터 리스트 객체를 전달받음
    public RecyclerImageTextAdapter(Context context, ArrayList<RecyclerItem> list, RecyclerOnItemClick onItemClick){
        this.context = context;
        this.mData = list;
        this.mCallback = onItemClick;
    }

    //화면에 표시될 아이템 뷰를 저장하는 객체
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rideImage;
        TextView rideName;
        TextView rideRestTime;
        TextView rideLocation;

        ViewHolder(View itemView){
            super(itemView);
            rideImage = itemView.findViewById(R.id.img_rImage);
            rideName = itemView.findViewById(R.id.txt_rName);
            rideRestTime = itemView.findViewById(R.id.txt_rrestTime);

            //recyclerview item 클릭 이벤트 정의
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    RecyclerItem selectitem = mData.get(position);
                    mCallback.onClick(v, selectitem.getRideName());
                }
            });
        }

    }

    // viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성
    @Override
    public RecyclerImageTextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext() ;

        View view = LayoutInflater.from(context).inflate(R.layout.select_item, parent, false) ;
        RecyclerImageTextAdapter.ViewHolder vh = new RecyclerImageTextAdapter.ViewHolder(view) ;
        return vh;
    }

    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(RecyclerImageTextAdapter.ViewHolder holder, int position){
        RecyclerItem item = mData.get(position);
        holder.rideImage.setImageDrawable(item.getRideImage());
        holder.rideName.setText(item.getRideName());
        holder.rideRestTime.setText(item.getRideRestTime());
        holder.rideLocation.setText(item.getRideLocation());
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

}


