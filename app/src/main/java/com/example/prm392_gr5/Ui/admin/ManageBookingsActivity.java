package com.example.prm392_gr5.Ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.adapter.BookingListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity implements BookingListAdapter.OnBookingClickListener {

    private RecyclerView recyclerView;
    private BookingRepository bookingRepo;
    private BookingListAdapter adapter;
    private SearchView searchView;
    private Spinner spinnerStatus;
    private Button btnClearFilter;
    private TextView tvResultsCount;
    private TextView tvNoResults;

    private List<Booking> allBookings;
    private List<Booking> filteredBookings;
    private String currentFilter = "";

    private int pitchId = -1;
    private String pitchName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        initViews();
        setupRecyclerView();
        setupSearchView();
        setupStatusSpinner();
        setupClickListeners();

        bookingRepo = new BookingRepository(this);

        pitchId = getIntent().getIntExtra("pitchId", -1);
        pitchName = getIntent().getStringExtra("pitchName");

        if (pitchId != -1 && pitchName != null) {
            setTitle("Booking của sân: " + pitchName);
        } else {
            setTitle("Quản lý đặt sân");
        }

        loadBookings();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewBookings);
        searchView = findViewById(R.id.searchView);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        tvNoResults = findViewById(R.id.tvNoResults);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allBookings = new ArrayList<>();
        filteredBookings = new ArrayList<>();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBookings(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    resetToAllBookings();
                } else {
                    searchBookings(newText);
                }
                return false;
            }
        });
    }

    private void setupStatusSpinner() {
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("Tất cả trạng thái");

        try {
            List<String> dbStatuses = bookingRepo.getAllBookingStatuses();
            statusOptions.addAll(dbStatuses);
        } catch (Exception e) {
            statusOptions.add("Đang chờ xác nhận");
            statusOptions.add("Đã xác nhận");
            statusOptions.add("Hoàn thành");
            statusOptions.add("Đã hủy");
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = (String) parent.getItemAtPosition(position);
                filterByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        btnClearFilter.setOnClickListener(v -> clearAllFilters());
    }

    private void loadBookings() {
        try {
            if (pitchId != -1) {
                allBookings = bookingRepo.getBookingsByPitch(pitchId);
            } else {
                allBookings = bookingRepo.getAllBookingsWithPitchNames();
            }

            filteredBookings = new ArrayList<>(allBookings);
            adapter = new BookingListAdapter(filteredBookings, this);
            recyclerView.setAdapter(adapter);

            updateResultsCount();
            updateNoResultsVisibility();
        } catch (Exception e) {
            Toast.makeText(this, "Không thể tải danh sách booking", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void searchBookings(String query) {
        if (query.isEmpty()) {
            resetToAllBookings();
            return;
        }

        List<Booking> searchResult = new ArrayList<>();
        for (Booking booking : allBookings) {
            if (booking.getUserName() != null && booking.getUserName().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(booking);
            } else if (booking.getPitchName() != null && booking.getPitchName().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(booking);
            } else if (booking.getStatus() != null && booking.getStatus().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(booking);
            }
        }

        filteredBookings = searchResult;
        currentFilter = "search:" + query;
        updateAdapter();
    }

    private void filterByStatus(String status) {
        if (status.equals("Tất cả trạng thái")) {
            resetToAllBookings();
            return;
        }

        List<Booking> filtered = new ArrayList<>();
        for (Booking booking : allBookings) {
            if (status.equalsIgnoreCase(booking.getStatus())) {
                filtered.add(booking);
            }
        }

        filteredBookings = filtered;
        currentFilter = "status:" + status;
        updateAdapter();
    }

    private void resetToAllBookings() {
        filteredBookings = new ArrayList<>(allBookings);
        currentFilter = "";
        updateAdapter();
    }

    private void clearAllFilters() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        spinnerStatus.setSelection(0);
        resetToAllBookings();
    }

    private void updateAdapter() {
        if (adapter != null) {
            adapter.updateData(filteredBookings);
        }
        updateResultsCount();
        updateNoResultsVisibility();
    }

    private void updateResultsCount() {
        String countText = "Tổng cộng: " + filteredBookings.size() + " kết quả";

        if (pitchId != -1 && pitchName != null) {
            countText += " (sân: " + pitchName + ")";
        } else if (!currentFilter.isEmpty()) {
            if (currentFilter.startsWith("search:")) {
                countText += " (tìm kiếm: \"" + currentFilter.substring(7) + "\")";
            } else if (currentFilter.startsWith("status:")) {
                countText += " (lọc: " + currentFilter.substring(7) + ")";
            }
        }

        tvResultsCount.setText(countText);
    }

    private void updateNoResultsVisibility() {
        if (filteredBookings.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBookingClick(Booking booking) {
        Intent intent = new Intent(this, BookingDetailActivity.class);
        intent.putExtra("booking_id", booking.getId());
        startActivity(intent);
    }

    @Override
    public void onStatusChangeClick(Booking booking) {
        StatusChangeDialogFragment dialog = StatusChangeDialogFragment.newInstance(
                booking.getId(), booking.getStatus());
        dialog.setOnStatusChangedListener(this::onStatusChanged);
        dialog.show(getSupportFragmentManager(), "StatusChangeDialog");
    }

    private void onStatusChanged(int bookingId, String newStatus) {
        loadBookings();
        Toast.makeText(this, "Đã cập nhật trạng thái booking", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}
