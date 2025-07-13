package com.example.prm392_gr5.Ui.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.ManageOwnerPitchesActivity;
import com.example.prm392_gr5.Ui.admin.ManageOwnersActivity;

import java.util.List;

public class OwnerListAdapter extends BaseAdapter {
    private Context context;
    private List<User> ownerList;
    private PitchRepository pitchRepo;
    private UserRepository userRepo;

    public OwnerListAdapter(Context context, List<User> ownerList, PitchRepository pitchRepo) {
        this.context = context;
        this.ownerList = ownerList;
        this.pitchRepo = pitchRepo;
        this.userRepo = new UserRepository(context);
    }

    @Override
    public int getCount() {
        return ownerList.size();
    }

    @Override
    public Object getItem(int position) {
        return ownerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_owner_row, parent, false);
            holder = new ViewHolder();
            holder.imgAvatar = convertView.findViewById(R.id.imgAvatar);
            holder.tvOwnerName = convertView.findViewById(R.id.tvOwnerName);
            holder.tvPitchCount = convertView.findViewById(R.id.tvPitchCount);
            holder.tvOwnerStatus = convertView.findViewById(R.id.tvOwnerStatus);
            holder.vStatusIndicator = convertView.findViewById(R.id.vStatusIndicator);
            holder.btnViewPitches = convertView.findViewById(R.id.btnViewPitches);
            holder.btnToggleStatus = convertView.findViewById(R.id.btnToggleStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User owner = ownerList.get(position);
        int pitchCount = pitchRepo.getPitchCountByOwner(owner.getId());

        // Set thông tin cơ bản
        holder.tvOwnerName.setText(owner.getFullName());
        holder.tvPitchCount.setText(pitchCount + " sân");

        // Set trạng thái và màu sắc
        boolean isActive = "owner".equals(owner.getRole());
        if (isActive) {
            holder.tvOwnerStatus.setText("Đang hoạt động");
            holder.tvOwnerStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.vStatusIndicator.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_green_dark));
        } else {
            holder.tvOwnerStatus.setText("Bị chặn");
            holder.tvOwnerStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.vStatusIndicator.setBackgroundTintList(
                    context.getResources().getColorStateList(android.R.color.holo_red_dark));
        }

        // Set avatar placeholder với gradient
        setGradientBackground(holder.imgAvatar, isActive);

        // Xử lý nút xem sân
        holder.btnViewPitches.setOnClickListener(v -> {
            if (pitchCount > 0) {
                // TODO: Mở activity xem danh sách sân của chủ sân
                Intent intent = new Intent(context, ManageOwnerPitchesActivity.class);
                intent.putExtra("ownerId", owner.getId());
                intent.putExtra("ownerName", owner.getFullName());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Chủ sân này chưa có sân nào", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút chặn/mở chặn
        holder.btnToggleStatus.setOnClickListener(v -> {
            showToggleStatusDialog(owner, isActive);
        });

        return convertView;
    }

    private void setGradientBackground(ImageView imageView, boolean isActive) {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setShape(GradientDrawable.OVAL);

        if (isActive) {
            gradient.setColors(new int[]{
                    context.getResources().getColor(android.R.color.holo_green_light),
                    context.getResources().getColor(android.R.color.holo_green_dark)
            });
        } else {
            gradient.setColors(new int[]{
                    context.getResources().getColor(android.R.color.darker_gray),
                    context.getResources().getColor(android.R.color.black)
            });
        }

        imageView.setBackground(gradient);
    }

    private void showToggleStatusDialog(User owner, boolean isActive) {
        String title = isActive ? "Chặn chủ sân" : "Mở chặn chủ sân";
        String message = isActive ?
                "Bạn có chắc chắn muốn chặn chủ sân " + owner.getFullName() + "?" :
                "Bạn có chắc chắn muốn mở chặn chủ sân " + owner.getFullName() + "?";

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    toggleOwnerStatus(owner, isActive);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void toggleOwnerStatus(User owner, boolean isActive) {
        try {
            String newRole = isActive ? "inactive" : "owner";
            owner.setRole(newRole);

            boolean success = userRepo.updateUser(owner) > 0;

            if (success) {
                // ✅ THÊM LOGIC: Khi chặn/mở chặn chủ sân, cập nhật tất cả sân của họ
                if (isActive) {
                    // Chặn chủ sân → Chặn tất cả sân của họ
                    pitchRepo.suspendPitchesByOwner(owner.getId());
                    Toast.makeText(context, "Đã chặn chủ sân " + owner.getFullName() + " và tất cả sân của họ", Toast.LENGTH_SHORT).show();
                } else {
                    // Mở chặn chủ sân → Kích hoạt tất cả sân của họ
                    pitchRepo.activatePitchesByOwner(owner.getId());
                    Toast.makeText(context, "Đã mở chặn chủ sân " + owner.getFullName() + " và tất cả sân của họ", Toast.LENGTH_SHORT).show();
                }

                // Refresh danh sách
                if (context instanceof ManageOwnersActivity) {
                    ((ManageOwnersActivity) context).refreshOwnerList();
                }
            } else {
                Toast.makeText(context, "Có lỗi xảy ra. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder {
        ImageView imgAvatar;
        TextView tvOwnerName;
        TextView tvPitchCount;
        TextView tvOwnerStatus;
        View vStatusIndicator;
        ImageButton btnViewPitches;
        ImageButton btnToggleStatus;
    }
}