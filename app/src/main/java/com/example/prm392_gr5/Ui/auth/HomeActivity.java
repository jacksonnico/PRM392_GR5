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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
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

    RecyclerView rvSuggestedPitches;
    PitchAdapter suggestedAdapter;

    private List<Pitch> originalPitchList;
    private List<Pitch> filteredPitchList;
    private PitchRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mappingViews();
        setupSuggestedList();
        setupEvent();
        setupSearchFunction();
    }

    private void mappingViews() {
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        navAccount = findViewById(R.id.navAccount);
        navHome    = findViewById(R.id.navHome);
        navFavorite= findViewById(R.id.navFavorite);
        navNotify  = findViewById(R.id.navNotify);

        rvSuggestedPitches = findViewById(R.id.rvSuggestedPitches);
    }

    private void setupSuggestedList() {
        // 1. LayoutManager
        rvSuggestedPitches.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        // 2. Lấy dữ liệu từ repository
        repo = new PitchRepository(this);
        originalPitchList = repo.getAllPitches();
        filteredPitchList = new ArrayList<>(originalPitchList);

        // 3. Tạo adapter và gán sự kiện click vào item
        suggestedAdapter = new PitchAdapter(filteredPitchList, pitch -> {
            Intent intent = new Intent(HomeActivity.this, PitchDetailActivity.class);
            intent.putExtra("pitchId", pitch.getId());
            intent.putExtra("userId", SharedPreferencesHelper.getUserId(this)); // Thêm truyền userId
            startActivity(intent);
        });
        rvSuggestedPitches.setAdapter(suggestedAdapter);
    }

    private void setupSearchFunction() {
        // Tìm kiếm theo thời gian thực khi người dùng nhập
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi hàm tìm kiếm khi text thay đổi
                searchPitches(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });
    }

    private void searchPitches(String keyword) {
        filteredPitchList.clear();

        if (keyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị tất cả
            filteredPitchList.addAll(originalPitchList);
        } else {
            // Lọc theo từ khóa
            String lowerKeyword = keyword.toLowerCase();
            for (Pitch pitch : originalPitchList) {
                if (matchesSearchCriteria(pitch, lowerKeyword)) {
                    filteredPitchList.add(pitch);
                }
            }
        }

        // Cập nhật RecyclerView
        suggestedAdapter.notifyDataSetChanged();

        // Hiển thị thông báo nếu không tìm thấy kết quả
        if (filteredPitchList.isEmpty() && !keyword.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sân phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesSearchCriteria(Pitch pitch, String keyword) {
        // Tìm kiếm theo tên sân
        if (pitch.getName() != null && pitch.getName().toLowerCase().contains(keyword)) {
            return true;
        }

        // Tìm kiếm theo địa chỉ
        if (pitch.getAddress() != null && pitch.getAddress().toLowerCase().contains(keyword)) {
            return true;
        }



        return false;
    }

    private void setupEvent() {
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                // Thực hiện tìm kiếm (đã được xử lý trong TextWatcher)
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
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
        });
    }

    // Phương thức để làm mới danh sách khi quay lại từ activity khác
    @Override
    protected void onResume() {
        super.onResume();
        refreshPitchList();
    }

    private void refreshPitchList() {
        originalPitchList = repo.getAllPitches();
        String currentKeyword = edtSearch.getText().toString().trim();
        searchPitches(currentKeyword);
    }

    // Phương thức để xóa từ khóa tìm kiếm
    public void clearSearch() {
        edtSearch.setText("");
        searchPitches("");
    }

}