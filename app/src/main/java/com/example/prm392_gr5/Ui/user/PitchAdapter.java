package com.example.prm392_gr5.Ui.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.R;

import java.util.List;

public class PitchAdapter extends RecyclerView.Adapter<PitchAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Pitch pitch);
    }

    private final List<Pitch> items;
    private final OnItemClickListener listener;

    public PitchAdapter(List<Pitch> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pitch, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvAddress;

        ViewHolder(View itemView) {
            super(itemView);
            ivThumb    = itemView.findViewById(R.id.ivPitchThumb);
            tvName     = itemView.findViewById(R.id.tvItemName);
            tvAddress  = itemView.findViewById(R.id.tvItemAddress);
        }

        void bind(final Pitch pitch, final OnItemClickListener listener) {
            tvName.setText(pitch.getName());
            tvAddress.setText(pitch.getAddress());
            if (pitch.getImageUrl() != null && !pitch.getImageUrl().isEmpty()) {
                Glide.with(ivThumb.getContext())
                        .load(pitch.getImageUrl())
                        .centerCrop()
                        .into(ivThumb);
            }
            itemView.setOnClickListener(v -> listener.onItemClick(pitch));
        }
    }
}
