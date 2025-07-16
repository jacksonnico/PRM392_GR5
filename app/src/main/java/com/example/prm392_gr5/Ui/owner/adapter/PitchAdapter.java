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

        if (pitchList == null || position >= pitchList.size() || pitchList.get(position) == null) {
            Log.e("PitchAdapter", "Pitch object is null at position: " + position);

            holder.tvPitchName.setText("");
            holder.tvPitchAddress.setText("");
            holder.tvPitchHours.setText("");
            holder.ivPitchIcon.setImageDrawable(null);
            holder.ivQrCode.setImageDrawable(null);
            holder.itemView.setOnClickListener(null);
            return;
        }

        Pitch pitch = pitchList.get(position);


        String pitchName = pitch.getName();
        if (pitchName != null) {
            holder.tvPitchName.setText(pitchName);
        } else {
            holder.tvPitchName.setText("N/A"); // Provide a default value or handle accordingly
            Log.w("PitchAdapter", "Pitch name is null for pitch ID: " + pitch.getId());
        }

        String pitchAddress = pitch.getAddress();
        if (pitchAddress != null) {
            holder.tvPitchAddress.setText(pitchAddress);
        } else {
            holder.tvPitchAddress.setText("N/A");
            Log.w("PitchAdapter", "Pitch address is null for pitch ID: " + pitch.getId());
        }

        String openTime = pitch.getOpenTime();
        String closeTime = pitch.getCloseTime();
        if (openTime != null && closeTime != null) {
            holder.tvPitchHours.setText("Mở cửa: " + openTime + " - " + closeTime);
        } else {
            holder.tvPitchHours.setText("Giờ mở cửa: N/A");
            Log.w("PitchAdapter", "Pitch open/close time is null for pitch ID: " + pitch.getId());
        }


        String imageUrl = pitch.getImageUrl();
        Log.d("PitchAdapter", "Image URL for " + pitch.getName() + ": " + imageUrl);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.notfound)
                    .error(R.drawable.error)
                    .into(holder.ivPitchIcon);
        } else {
            // Set a default image or clear the existing one if imageUrl is null or empty
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.notfound) // Or any other default image
                    .into(holder.ivPitchIcon);
            Log.w("PitchAdapter", "Image URL is null or empty for pitch ID: " + pitch.getId());
        }


        holder.itemView.setOnClickListener(v -> {
            // Ensure pitch.getId() is not null if you expect an int for the putExtra
            // If getId() returns Integer, it can be null. If int, it will be 0 by default.
            if (pitch != null) { // Additional check for safety, though handled at the top
                Intent intent = new Intent(context, EditPitchActivity.class);
                intent.putExtra("pitchId", pitch.getId()); // Assuming getId() returns a primitive int
                context.startActivity(intent);
            } else {
                Log.e("PitchAdapter", "Cannot open EditPitchActivity, pitch is null for click.");
            }
        });
    }


    @Override
    public int getItemCount() {
        return pitchList != null ? pitchList.size() : 0; // Handle case where pitchList itself might be null
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