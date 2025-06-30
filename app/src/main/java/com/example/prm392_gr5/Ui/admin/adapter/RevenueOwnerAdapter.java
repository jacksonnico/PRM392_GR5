package com.example.prm392_gr5.Ui.admin.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_gr5.R;

import java.util.*;

public class RevenueOwnerAdapter extends RecyclerView.Adapter<RevenueOwnerAdapter.ViewHolder> {

    private List<Map.Entry<String, Double>> ownerList;

    public RevenueOwnerAdapter(List<Map.Entry<String, Double>> ownerList) {
        this.ownerList = ownerList;
    }

    public void updateData(Map<String, Double> data) {
        this.ownerList = new ArrayList<>(data.entrySet());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RevenueOwnerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revenue_owner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueOwnerAdapter.ViewHolder holder, int position) {
        Map.Entry<String, Double> entry = ownerList.get(position);
        holder.tvOwnerName.setText(entry.getKey());
        holder.tvRevenue.setText(String.format("%,.0f VNĐ", entry.getValue()));
    }

    @Override
    public int getItemCount() {
        return ownerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOwnerName, tvRevenue;

        ViewHolder(View view) {
            super(view);
            tvOwnerName = view.findViewById(R.id.tvOwnerName);
            tvRevenue = view.findViewById(R.id.tvRevenue);
        }
    }
}
