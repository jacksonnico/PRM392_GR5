package com.example.prm392_gr5.Ui.owner.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.owner.EditPitchActivity;

import java.util.List;

public class PitchAdapter extends RecyclerView.Adapter<PitchAdapter.ViewHolder> {

    private final Context context;
    private final List<Pitch> pitchList;
    private final int ownerId;

    public PitchAdapter(Context context, List<Pitch> pitchList, int ownerId) {
        this.context = context;
        this.pitchList = pitchList;
        this.ownerId = ownerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pitch_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pitch pitch = pitchList.get(position);

        holder.tvPitchName.setText(pitch.getName());
        holder.tvPitchAddress.setText(pitch.getAddress());
        holder.tvPitchHours.setText("Mở cửa: " + pitch.getOpenTime() + " - " + pitch.getCloseTime());
        Log.d("PitchAdapter", "Image URL: " + pitch.getImageUrl());
        Glide.with(holder.itemView.getContext())
                .load(pitch.getImageUrl())
                .placeholder(R.drawable.notfound)
                .error(R.drawable.error)
                .into(holder.ivPitchIcon);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPitchActivity.class);
            intent.putExtra("pitchId", pitch.getId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return pitchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPitchName, tvPitchAddress, tvPitchHours;
        ImageView ivPitchIcon, ivQrCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPitchName = itemView.findViewById(R.id.tv_pitch_name);
            tvPitchAddress = itemView.findViewById(R.id.tv_pitch_address);
            tvPitchHours = itemView.findViewById(R.id.tv_pitch_hours);
            ivPitchIcon = itemView.findViewById(R.id.iv_pitch_icon);
            ivQrCode = itemView.findViewById(R.id.iv_qr_code);
        }
    }
}
