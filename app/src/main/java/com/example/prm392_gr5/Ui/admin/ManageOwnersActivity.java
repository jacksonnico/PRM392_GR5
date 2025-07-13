package com.example.prm392_gr5.Ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.adapter.OwnerListAdapter;

import java.util.*;

public class ManageOwnersActivity extends AppCompatActivity {
    private UserRepository userRepo;
    private PitchRepository pitchRepo;

    private SearchView svSearchOwner;
    private Spinner spFilter;
    private FloatingActionButton btnAddOwner;
    private ListView lvOwners;
    private TextView tvTotalOwners;
    private TextView tvActiveOwners;
    private TextView tvResultCount;

    private List<User> fullList = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();
    private OwnerListAdapter ownerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_owners);

        userRepo = new UserRepository(this);
        pitchRepo = new PitchRepository(this);

        initViews();
        setupFilter();
        setupSearch();
        setupAddButton();
        loadOwners();
    }

    private void initViews() {
        svSearchOwner = findViewById(R.id.svSearchOwner);
        spFilter = findViewById(R.id.spFilter);
        btnAddOwner = findViewById(R.id.btnAddOwner);
        lvOwners = findViewById(R.id.lvOwners);
        tvTotalOwners = findViewById(R.id.tvTotalOwners);
        tvActiveOwners = findViewById(R.id.tvActiveOwners);
        tvResultCount = findViewById(R.id.tvResultCount);
    }

    private void setupFilter() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Tất cả", "Có sân", "Không có sân", "Bị chặn"});
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndShow(svSearchOwner.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        svSearchOwner.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAndShow(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndShow(newText);
                return true;
            }
        });
    }

    private void setupAddButton() {
        btnAddOwner.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditOwnerActivity.class);
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadOwners(); // reload lại danh sách chủ sân
        }
    }

    private void loadOwners() {
        fullList.clear();
        fullList.addAll(userRepo.getUsersByRole("owner"));
        fullList.addAll(userRepo.getUsersByRole("inactive"));

        updateStats();
        filterAndShow(svSearchOwner.getQuery().toString());
    }

    private void updateStats() {
        int totalOwners = fullList.size();
        int activeOwners = 0;

        for (User user : fullList) {
            if ("owner".equals(user.getRole())) {
                activeOwners++;
            }
        }

        tvTotalOwners.setText(String.valueOf(totalOwners));
        tvActiveOwners.setText(String.valueOf(activeOwners));
    }

    private void filterAndShow(String query) {
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        String selectedFilter = spFilter.getSelectedItem().toString();

        filteredList.clear();

        for (User user : fullList) {
            String name = user.getFullName();
            String role = user.getRole();
            int pitchCount = pitchRepo.getPitchCountByOwner(user.getId());

            boolean matchesSearch = name.toLowerCase().contains(lowerQuery);
            boolean matchesFilter = true;

            switch (selectedFilter) {
                case "Có sân":
                    matchesFilter = pitchCount > 0;
                    break;
                case "Không có sân":
                    matchesFilter = pitchCount == 0;
                    break;
                case "Bị chặn":
                    matchesFilter = "inactive".equals(role);
                    break;
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(user);
            }
        }

        // Cập nhật số lượng kết quả
        tvResultCount.setText(filteredList.size() + " kết quả");

        ownerListAdapter = new OwnerListAdapter(this, filteredList, pitchRepo);
        lvOwners.setAdapter(ownerListAdapter);
    }

    // Method để refresh danh sách từ adapter
    public void refreshOwnerList() {
        loadOwners();
    }
}