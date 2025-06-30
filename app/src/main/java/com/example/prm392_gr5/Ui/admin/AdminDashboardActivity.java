package com.example.prm392_gr5.Ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.repository.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {
    private UserRepository uRepo;
    private BookingRepository bRepo;
    private PaymentRepository pRepo;

    private TextView tvUsers, tvOwners, tvBookings, tvRevenue;
    private Spinner spTimeFilter;
    private PieChart pieChart;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_dashboard);

        uRepo = new UserRepository(this);
        bRepo = new BookingRepository(this);//comment
        pRepo = new PaymentRepository(this);

        tvUsers    = findViewById(R.id.tvTotalUsers);
        tvOwners   = findViewById(R.id.tvTotalOwners);
        tvBookings = findViewById(R.id.tvTotalBookings);
        tvRevenue  = findViewById(R.id.tvTotalRevenue);
        spTimeFilter = findViewById(R.id.spTimeFilter);
        pieChart = findViewById(R.id.pieChart);
        btnRefresh = findViewById(R.id.btnRefresh);

        setupSpinner();
        loadStats();
        setupListeners();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tất cả", "Tuần này", "Tháng này", "Quý này", "Năm nay"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTimeFilter.setAdapter(adapter);
    }

    private void setupListeners() {
        spTimeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadRevenueChart();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRefresh.setOnClickListener(v -> {
            loadStats();
            loadRevenueChart();
        });
    }

    private void loadStats() {
        int users  = uRepo.getUserCountByRole("user");
        int owners = uRepo.getUserCountByRole("owner");
        int books  = bRepo.getBookingCount();
        double rev = pRepo.getTotalRevenue();

        tvUsers.setText(String.valueOf(users));
        tvOwners.setText(String.valueOf(owners));
        tvBookings.setText(String.valueOf(books));
        tvRevenue.setText(String.format("%,.0f VNĐ", rev));
    }

    private void loadRevenueChart() {
        String selected = spTimeFilter.getSelectedItem().toString().toLowerCase();
        double total = pRepo.getTotalRevenue();
        double value;

        if (selected.contains("tuần")) {
            value = pRepo.getRevenueByTimeRange("week");
        } else if (selected.contains("tháng")) {
            value = pRepo.getRevenueByTimeRange("month");
        } else if (selected.contains("quý")) {
            value = pRepo.getRevenueByTimeRange("quarter");
        } else if (selected.contains("năm")) {
            value = pRepo.getRevenueByTimeRange("year");
        } else {
            value = total;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) value, "Đã thu"));
        entries.add(new PieEntry((float) (total - value), "Khác"));

        PieDataSet dataSet = new PieDataSet(entries, "Doanh thu");
        dataSet.setColors(new int[]{R.color.teal_700, R.color.gray}, this);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        Description desc = new Description();
        desc.setText("Tỉ lệ doanh thu " + selected);
        pieChart.setDescription(desc);

        pieChart.invalidate(); // refresh
    }
}
