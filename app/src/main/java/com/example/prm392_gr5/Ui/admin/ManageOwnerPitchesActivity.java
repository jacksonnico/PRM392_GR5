package com.example.prm392_gr5.Ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Ui.admin.adapter.PitchListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManageOwnerPitchesActivity extends AppCompatActivity {
    private PitchRepository pitchRepo;
    private BookingRepository bookingRepo;
    private RecyclerView rvPitches;
    private AutoCompleteTextView spFilterStatus;
    private View btnAdd;

    private List<Pitch> pitchList;
    private int ownerId = -1; // -1 mặc định là xem tất cả sân
    private String ownerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_owner_pitches);

        pitchRepo = new PitchRepository(this);
        bookingRepo = new BookingRepository(this);

        rvPitches = findViewById(R.id.rvPitches);
        spFilterStatus = findViewById(R.id.spFilterStatus);
        btnAdd = findViewById(R.id.btnAddPitch);

        // Nhận dữ liệu từ Intent
        ownerId = getIntent().getIntExtra("ownerId", -1);
        ownerName = getIntent().getStringExtra("ownerName");

        // Set tiêu đề và ẩn nút thêm nếu chỉ xem sân của một chủ sân
        if (ownerId != -1) {
            setTitle("Sân của: " + ownerName);
            btnAdd.setVisibility(View.GONE);
        } else {
            setTitle("Tất cả sân");
            btnAdd.setVisibility(View.VISIBLE);
        }

        rvPitches.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"Tất cả", "Đang hoạt động", "Tạm ngưng"});
        spFilterStatus.setAdapter(filterAdapter);
        spFilterStatus.setText("Tất cả", false);

        spFilterStatus.setOnItemClickListener((parent, view, position, id) -> loadPitches());

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditPitchActivity.class);
            startActivity(intent);
        });

        loadPitches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPitches();
    }

    private void loadPitches() {
        try {
            String filter = spFilterStatus.getText().toString();
            List<Pitch> all = (ownerId != -1)
                    ? pitchRepo.getPitchesByOwner(ownerId)
                    : pitchRepo.getAllPitches();

            List<Pitch> filtered = new ArrayList<>();

            for (Pitch pitch : all) {
                String status = pitch.getStatus() != null ? pitch.getStatus() : "active";
                if (filter.equals("Đang hoạt động") && !status.equals("active")) continue;
                if (filter.equals("Tạm ngưng") && !status.equals("suspended")) continue;
                filtered.add(pitch);
            }

            pitchList = filtered;

            PitchListAdapter adapter = new PitchListAdapter(
                    this,
                    pitchList,
                    this::loadPitches,
                    this::showPitchOptionsDialog
            );

            rvPitches.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải danh sách sân", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPitchOptionsDialog(Pitch pitch) {
        int bookingCount = bookingRepo.getBookingCountByPitch(pitch.getId());
        String status = pitch.getStatus() != null ? pitch.getStatus() : "active";

        String[] options = {"Booking", "Edit", status.equals("suspended") ? "Active" : "InActive", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle(pitch.getName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(this, ManageBookingsActivity.class);
                            intent.putExtra("pitchId", pitch.getId()); // truyền để lọc theo sân
                            intent.putExtra("pitchName", pitch.getName());
                            startActivity(intent);
                            break;
                        case 1:
                            Intent editIntent = new Intent(this, AddEditPitchActivity.class);
                            editIntent.putExtra("pitchId", pitch.getId());
                            startActivity(editIntent);
                            break;
                        case 2:
                            if (status.equals("suspended")) {
                                pitchRepo.activatePitch(pitch.getId());
                                Toast.makeText(this, "Đã kích hoạt sân", Toast.LENGTH_SHORT).show();
                            } else {
                                pitchRepo.suspendPitch(pitch.getId());
                                Toast.makeText(this, "Đã tạm ngưng sân", Toast.LENGTH_SHORT).show();
                            }
                            loadPitches();
                            break;
                        case 3:
                            new AlertDialog.Builder(this)
                                    .setTitle("Xác nhận xóa")
                                    .setMessage("Bạn có chắc chắn muốn xóa sân này?")
                                    .setPositiveButton("Xóa", (d, w) -> {
                                        pitchRepo.deletePitch(pitch.getId());
                                        Toast.makeText(this, "Đã xóa sân", Toast.LENGTH_SHORT).show();
                                        loadPitches();
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                            break;
                    }
                })
                .show();
    }
}
