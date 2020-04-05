package com.world.magicline.Main.fragments.selection.selectRecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.world.magicline.R;

import java.util.List;

public class SelectRecyclerViewAdapter extends RecyclerView.Adapter<SelectRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<SelectItem> itemList;

    public SelectRecyclerViewAdapter(Context context){mContext = context;}


    @NonNull
    @Override
    public SelectRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_item, parent, false);
        SelectRecyclerViewAdapter.MyViewHolder vh = new SelectRecyclerViewAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectRecyclerViewAdapter.MyViewHolder holder, int position) {
        SelectItem selectItem = itemList.get(position);
        Glide.with(mContext)
                .load(selectItem.getRide_Image())
                .dontTransform()
                .centerCrop()
                .into(holder.rideImage);
        holder.rideName.setText(selectItem.getRide_Name());
        holder.restTime.setText(selectItem.getRestTime());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<SelectItem> itemList){
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView rideImage;
        private TextView rideName;
        private TextView restTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rideImage = itemView.findViewById(R.id.img_rImage);
            rideName = itemView.findViewById(R.id.txt_rName);
            restTime = itemView.findViewById(R.id.txt_rrestTime);
        }
    }
}
