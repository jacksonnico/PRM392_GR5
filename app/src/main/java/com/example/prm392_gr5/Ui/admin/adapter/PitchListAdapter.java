package com.example.prm392_gr5.Ui.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.AddEditPitchActivity;
import com.example.prm392_gr5.Ui.admin.ManageBookingsActivity;
import com.example.prm392_gr5.Ui.admin.ViewBookingsActivity;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.function.Consumer;

public class PitchListAdapter extends RecyclerView.Adapter<PitchListAdapter.PitchViewHolder> {

    private Context context;
    private List<Pitch> pitches;
    private PitchRepository pitchRepo;
    private BookingRepository bookingRepo;
    private Runnable onDataChanged;
    private Consumer<Pitch> onPitchClicked;

    public PitchListAdapter(Context context, List<Pitch> pitches, Runnable onDataChanged, Consumer<Pitch> onPitchClicked) {
        this.context = context;
        this.pitches = pitches;
        this.onPitchClicked = onPitchClicked;
        this.onDataChanged = onDataChanged;
        this.pitchRepo = new PitchRepository(context);
        this.bookingRepo = new BookingRepository(context);
    }

    @NonNull
    @Override
    public PitchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pitch_admin, parent, false);
        return new PitchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PitchViewHolder holder, int position) {
        Pitch pitch = pitches.get(position);

        String ownerName = pitchRepo.getOwnerNameById(pitch.getOwnerId());
        int bookingCount = bookingRepo.getBookingCountByPitch(pitch.getId());
        String status = pitch.getStatus() != null ? pitch.getStatus() : "active";

        // Thông tin chính
        holder.tvInfo.setText(pitch.getName() + " - " + ownerName);
        holder.tvDetails.setText(String.format("%,.0f VNĐ - %d booking", pitch.getPrice(), bookingCount));

        // Cập nhật trạng thái cho nút & chip
        if (status.equals("suspended")) {
            holder.btnToggle.setText("Active");
            holder.chipStatus.setText("Tạm ngưng");
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F44336"))); // đỏ
        } else {
            holder.btnToggle.setText("Inactive");
            holder.chipStatus.setText("Hoạt động");
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // xanh lá
        }

        // Xem booking
        holder.btnView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ManageBookingsActivity.class);
            intent.putExtra("pitchId", pitch.getId());
            intent.putExtra("pitchName", pitch.getName());
            context.startActivity(intent);
        });


        // Sửa sân
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditPitchActivity.class);
            intent.putExtra("pitchId", pitch.getId());
            context.startActivity(intent);
        });

        // Toggle trạng thái
        holder.btnToggle.setOnClickListener(v -> {
            if (status.equals("suspended")) {
                pitchRepo.activatePitch(pitch.getId());
                Toast.makeText(context, "Đã kích hoạt sân", Toast.LENGTH_SHORT).show();
            } else {
                pitchRepo.suspendPitch(pitch.getId());
                Toast.makeText(context, "Đã tạm ngưng sân", Toast.LENGTH_SHORT).show();
            }
            onDataChanged.run();
        });

        // Xóa sân
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sân")
                    .setMessage("Bạn có chắc chắn muốn xóa sân này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        pitchRepo.deletePitch(pitch.getId());
                        Toast.makeText(context, "Đã xóa sân", Toast.LENGTH_SHORT).show();
                        onDataChanged.run();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return pitches.size();
    }

    public static class PitchViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo, tvDetails;
        Button btnView, btnEdit, btnToggle, btnDelete;
        Chip chipStatus;

        public PitchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvPitchName);
            tvDetails = itemView.findViewById(R.id.tvPitchDetails);
            btnView = itemView.findViewById(R.id.btnViewBookings);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnToggle = itemView.findViewById(R.id.btnToggleStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
