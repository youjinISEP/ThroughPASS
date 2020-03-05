package com.example.throughpass.Main.fragments.ride.swipeRecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.throughpass.R;

import java.util.ArrayList;
import java.util.List;

public class SwipeRecyclerviewAdapter extends RecyclerView.Adapter<SwipeRecyclerviewAdapter.MyViewHolder> {

    private Context mContext;
    private List<ViewItem> itemList;

    public SwipeRecyclerviewAdapter(Context context){
        mContext = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.ride_item, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeRecyclerviewAdapter.MyViewHolder holder, int position) {
        ViewItem viewItem = itemList.get(position);

        holder.statusColor.setBackgroundColor(viewItem.getStatus());
        holder.rideImage.setImageDrawable(viewItem.getRide_Image());
        holder.rideName.setText(viewItem.getRide_Name());
        holder.restTime.setText(viewItem.getRestTime());
    }

    @Override
    public int getItemCount() {
       return itemList.size();
    }

    public void setItemList(List<ViewItem> itemList){
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView statusColor;
        private ImageView rideImage;
        private TextView rideName;
        private TextView restTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            statusColor = itemView.findViewById(R.id.img_rcolorStatus);
            rideImage = itemView.findViewById(R.id.img_rImage);
            rideName = itemView.findViewById(R.id.txt_rName); //txt_rName이 중복된다. 꼭!!!!!바꾸기!!!!!!!. layout에서
            restTime = itemView.findViewById(R.id.txt_rrestTime);
        }
    }
}
