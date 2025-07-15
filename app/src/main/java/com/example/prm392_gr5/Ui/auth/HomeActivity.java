package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.NotificationManagerRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.user.NotificationActivity;
import com.example.prm392_gr5.Ui.user.PitchAdapter;
import com.example.prm392_gr5.Ui.user.PitchDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {

    EditText edtSearch;
    ImageView btnSearch;
    LinearLayout navAccount, navHome, navFavorite, navNotify;
    TextView tvBadge; // 👈 Badge hiển thị số thông báo

    RecyclerView rvSuggestedPitches;
    PitchAdapter suggestedAdapter;

    private List<Pitch> originalPitchList;
    private List<Pitch> filteredPitchList;
    private PitchRepository repo;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mappingViews();
        setupSuggestedList();
        setupEvent();
        setupSearchFunction();

        userId = SharedPreferencesHelper.getUserId(this); // Lấy userId hiện tại
        if (userId != -1) {
            updateBadge(); // 👈 Cập nhật badge ngay khi mở app
        }
    }

    private void mappingViews() {
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        navAccount = findViewById(R.id.navAccount);
        navHome    = findViewById(R.id.navHome);
        navFavorite= findViewById(R.id.navFavorite);
        navNotify  = findViewById(R.id.navNotify);
        tvBadge    = findViewById(R.id.tvBadge); // 👈 Badge số thông báo

        rvSuggestedPitches = findViewById(R.id.rvSuggestedPitches);
    }

    private void setupSuggestedList() {
        rvSuggestedPitches.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        repo = new PitchRepository(this);
        originalPitchList = repo.getAllPitches();
        filteredPitchList = new ArrayList<>(originalPitchList);

        suggestedAdapter = new PitchAdapter(filteredPitchList, pitch -> {
            Intent intent = new Intent(HomeActivity.this, PitchDetailActivity.class);
            intent.putExtra("pitchId", pitch.getId());
            intent.putExtra("userId", userId); // Truyền userId
            startActivity(intent);
        });
        rvSuggestedPitches.setAdapter(suggestedAdapter);
    }

    private void setupSearchFunction() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPitches(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchPitches(String keyword) {
        filteredPitchList.clear();
        if (keyword.isEmpty()) {
            filteredPitchList.addAll(originalPitchList);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            for (Pitch pitch : originalPitchList) {
                if (matchesSearchCriteria(pitch, lowerKeyword)) {
                    filteredPitchList.add(pitch);
                }
            }
        }
        suggestedAdapter.notifyDataSetChanged();

        if (filteredPitchList.isEmpty() && !keyword.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sân phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesSearchCriteria(Pitch pitch, String keyword) {
        if (pitch.getName() != null && pitch.getName().toLowerCase().contains(keyword)) return true;
        if (pitch.getAddress() != null && pitch.getAddress().toLowerCase().contains(keyword)) return true;
        return false;
    }

    private void setupEvent() {
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                searchPitches(keyword);
                Toast.makeText(this, "Tìm kiếm: " + keyword, Toast.LENGTH_SHORT).show();
            }
        });

        navAccount.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AccountActivity.class));
        });

        navHome.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đang ở trang chủ", Toast.LENGTH_SHORT).show();
        });

        navFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
        });

        navNotify.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);

            // 👇 Khi người dùng mở NotificationActivity => reset badge về 0
            clearBadge();
        });
    }

    private void refreshPitchList() {
        originalPitchList = repo.getAllPitches();
        String currentKeyword = edtSearch.getText().toString().trim();
        searchPitches(currentKeyword);
    }

    private void updateBadge() {
        NotificationManagerRepository repo = new NotificationManagerRepository(this);
        int unreadCount = repo.getUnreadCount(userId, "user");

        if (unreadCount > 0) {
            tvBadge.setText(String.valueOf(unreadCount));
            tvBadge.setVisibility(View.VISIBLE);
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }

    private void clearBadge() {
        tvBadge.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPitchList();
        updateBadge(); // 👈 Refresh badge khi quay lại Home
    }

    public void clearSearch() {
        edtSearch.setText("");
        searchPitches("");
    }
}
