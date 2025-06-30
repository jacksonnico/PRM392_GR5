package com.example.prm392_gr5.Ui.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.ManageOwnerPitchesActivity;

import java.util.List;

public class OwnerListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> owners;
    private PitchRepository pitchRepo;
    private UserRepository userRepo;

    public OwnerListAdapter(Context context, List<User> owners, PitchRepository pitchRepo) {
        super(context, 0, owners);
        this.context = context;
        this.owners = owners;
        this.pitchRepo = pitchRepo;
        this.userRepo = new UserRepository(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_owner_row, parent, false);

        User user = owners.get(position);

        // View binding
        TextView tvName = convertView.findViewById(R.id.tvOwnerName);
        TextView tvCount = convertView.findViewById(R.id.tvPitchCount);
        TextView tvStatus = convertView.findViewById(R.id.tvOwnerStatus);
        ImageView imgAvatar = convertView.findViewById(R.id.imgAvatar);
        ImageButton btnViewPitches = convertView.findViewById(R.id.btnViewPitches);
        ImageButton btnToggleStatus = convertView.findViewById(R.id.btnToggleStatus);

        // Hiển thị tên và avatar
        tvName.setText(user.getFullName());
        imgAvatar.setImageResource(R.drawable.ic_user_avatar); // Avatar mặc định

        // Đếm số sân
        int pitchCount = pitchRepo.getPitchCountByOwner(user.getId());
        tvCount.setText(pitchCount + " sân");

        // Trạng thái
        if ("inactive".equals(user.getRole())) {
            tvStatus.setText("Bị chặn");
            tvStatus.setTextColor(Color.RED);
        } else {
            tvStatus.setText("Hoạt động");
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Sự kiện nút "Xem sân"
        btnViewPitches.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageOwnerPitchesActivity.class);
            intent.putExtra("ownerId", user.getId());
            intent.putExtra("ownerName", user.getFullName());
            context.startActivity(intent);
        });

        // Sự kiện nút "Chặn/Mở chặn"
        btnToggleStatus.setOnClickListener(v -> {
            if ("inactive".equals(user.getRole())) {
                userRepo.setUserActive(user.getId());
                pitchRepo.activatePitchesByOwner(user.getId());
                Toast.makeText(context, "Đã mở chặn " + user.getFullName(), Toast.LENGTH_SHORT).show();
                user.setRole("owner");
            } else {
                userRepo.setUserInactive(user.getId());
                pitchRepo.suspendPitchesByOwner(user.getId());
                Toast.makeText(context, "Đã chặn " + user.getFullName(), Toast.LENGTH_SHORT).show();
                user.setRole("inactive");
            }
            notifyDataSetChanged();
        });

        return convertView;
    }
}
