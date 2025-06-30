package com.example.prm392_gr5.Ui.admin.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_gr5.R;

import java.util.*;

public class TopPitchAdapter extends RecyclerView.Adapter<TopPitchAdapter.ViewHolder> {

    private List<Map.Entry<String, Integer>> pitchList;

    public TopPitchAdapter(List<Map.Entry<String, Integer>> pitchList) {
        this.pitchList = pitchList;
    }

    public void updateData(Map<String, Integer> data) {
        this.pitchList = new ArrayList<>(data.entrySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopPitchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_pitch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPitchAdapter.ViewHolder holder, int position) {
        Map.Entry<String, Integer> entry = pitchList.get(position);
        holder.tvPitchName.setText(entry.getKey());
        holder.tvBookingCount.setText(entry.getValue() + " lượt đặt");
    }

    @Override
    public int getItemCount() {
        return pitchList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPitchName, tvBookingCount;

        ViewHolder(View view) {
            super(view);
            tvPitchName = view.findViewById(R.id.tvPitchName);
            tvBookingCount = view.findViewById(R.id.tvBookingCount);
        }
    }
}
