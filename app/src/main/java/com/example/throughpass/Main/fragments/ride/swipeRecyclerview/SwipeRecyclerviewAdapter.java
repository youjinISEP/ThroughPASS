package com.example.throughpass.Main.fragments.ride.swipeRecyclerview;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.throughpass.Main.SSLexception.GlideApp;
import com.example.throughpass.R;
import com.google.android.material.internal.CircularBorderDrawable;

import java.util.List;

public class SwipeRecyclerviewAdapter extends RecyclerView.Adapter<SwipeRecyclerviewAdapter.MyViewHolder> {

    private Context mContext;
    private List<ViewItem> itemList;

    public SwipeRecyclerviewAdapter(Context context) {
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

       // holder.statusColor.setBackgroundColor(viewItem.getStatus());
        GlideApp.with(mContext)
                .load(viewItem.getImg_url())
                .centerCrop()
                .into(holder.rideImage);
        holder.rideImage.setImageDrawable(viewItem.getRide_Image());
        holder.rideName.setText(viewItem.getRide_Name());
        holder.restTime.setText(viewItem.getRestTime() + " 분");
        if(!TextUtils.isEmpty(viewItem.getStart_time()) && !TextUtils.isEmpty(viewItem.getEnd_time())) {
            holder.runtime.setText("입장 가능 시간 : " + (viewItem.getStart_time() + " ~ " + viewItem.getEnd_time()));
        }

        holder.waitStatus.setVisibility(View.VISIBLE);
        holder.resvStatus.setVisibility(View.VISIBLE);

        if(viewItem.getWaitStatus() == 4) { // 신청 X
            holder.waitStatus.setVisibility(View.GONE);
        }

        if(viewItem.getResvStatus() == 4) { // 신청 X
            holder.resvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<ViewItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView statusColor;
        private ImageView rideImage;
        private TextView rideName;
        private TextView restTime;
        private TextView waitStatus;
        private TextView resvStatus;
        private TextView runtime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           // statusColor = itemView.findViewById(R.id.img_rcolorStatus);
            rideImage = itemView.findViewById(R.id.img_rImage);
            rideName = itemView.findViewById(R.id.txt_rName); //txt_rName이 중복된다. 꼭!!!!!바꾸기!!!!!!!. layout에서
            restTime = itemView.findViewById(R.id.txt_rrestTime);
            waitStatus = itemView.findViewById(R.id.txt_rWaitStatus);
            resvStatus = itemView.findViewById(R.id.txt_rResvStatus);
            runtime = itemView.findViewById(R.id.txt_runtime);
        }
    }
}

