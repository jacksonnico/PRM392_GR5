package com.example.prm392_gr5.Ui.admin;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.repository.*;
import com.example.prm392_gr5.Ui.admin.adapter.RevenueOwnerAdapter;
import com.example.prm392_gr5.Ui.admin.adapter.TopPitchAdapter;

import java.util.*;

public class AdminStatisticsActivity extends AppCompatActivity {

    private UserRepository userRepo;
    private BookingRepository bookingRepo;
    private PaymentRepository paymentRepo;
    private PitchRepository pitchRepo;

    private TextView tvTotalRevenue, tvMonthRevenue, tvWeekRevenue;
    private Spinner spTimeRange;
    private RecyclerView rvTopPitches, rvRevenueByOwner;

    private TopPitchAdapter topPitchAdapter;
    private RevenueOwnerAdapter revenueOwnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistics);

        initRepositories();
        initViews();
        setupRecyclerViews();
        loadStatistics();

        spTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                loadStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initRepositories() {
        userRepo = new UserRepository(this);
        bookingRepo = new BookingRepository(this);
        paymentRepo = new PaymentRepository(this);
        pitchRepo = new PitchRepository(this);
    }

    private void initViews() {
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvMonthRevenue = findViewById(R.id.tvMonthRevenue);
        tvWeekRevenue = findViewById(R.id.tvWeekRevenue);
        spTimeRange = findViewById(R.id.spTimeRange);
        rvTopPitches = findViewById(R.id.rvTopPitches);
        rvRevenueByOwner = findViewById(R.id.rvRevenueByOwner);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tuần này", "Tháng này", "3 tháng", "6 tháng", "Năm nay"});
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeRange.setAdapter(timeAdapter);
    }

    private void setupRecyclerViews() {
        topPitchAdapter = new TopPitchAdapter(new ArrayList<>());
        revenueOwnerAdapter = new RevenueOwnerAdapter(new ArrayList<>());

        rvTopPitches.setLayoutManager(new LinearLayoutManager(this));
        rvRevenueByOwner.setLayoutManager(new LinearLayoutManager(this));

        rvTopPitches.setAdapter(topPitchAdapter);
        rvRevenueByOwner.setAdapter(revenueOwnerAdapter);
    }

    private void loadStatistics() {
        double totalRevenue = paymentRepo.getTotalRevenue();
        double monthRevenue = paymentRepo.getRevenueByTimeRange("month");
        double weekRevenue = paymentRepo.getRevenueByTimeRange("week");

        tvTotalRevenue.setText("• Tổng doanh thu: " + String.format("%,.0f VNĐ", totalRevenue));
        tvMonthRevenue.setText("• Tháng này: " + String.format("%,.0f VNĐ", monthRevenue));
        tvWeekRevenue.setText("• Tuần này: " + String.format("%,.0f VNĐ", weekRevenue));

        loadTopPitches();
        loadRevenueByOwner();
    }

    private void loadTopPitches() {
        Map<String, Integer> topPitches = bookingRepo.getTopPitchesByBookings(10);
        topPitchAdapter.updateData(topPitches);
    }

    private void loadRevenueByOwner() {
        Map<String, Double> revenueByOwner = paymentRepo.getRevenueByOwner();
        revenueOwnerAdapter.updateData(revenueByOwner);
    }
}
