package com.example.prm392_gr5.Ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Payment;
import com.example.prm392_gr5.R;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PVH> {
    private final List<Payment> items;

    public PaymentAdapter(List<Payment> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);
        return new PVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PVH holder, int position) {
        Payment p = items.get(position);
        holder.tvMethod.setText(p.getMethod());
        holder.tvAmount.setText(String.valueOf(p.getAmount()));
        holder.tvStatus.setText(p.getStatus());
        holder.tvTime.setText(p.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class PVH extends RecyclerView.ViewHolder {
        TextView tvMethod;
        TextView tvAmount;
        TextView tvStatus;
        TextView tvTime;

        public PVH(@NonNull View itemView) {
            super(itemView);
            tvMethod = itemView.findViewById(R.id.tvMethod);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime   = itemView.findViewById(R.id.tvTime);
        }
    }
}
