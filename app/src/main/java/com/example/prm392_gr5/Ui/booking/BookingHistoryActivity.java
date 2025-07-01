package com.example.prm392_gr5.Ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private RecyclerView rvBookings;
    private BookingRepository bookingRepo;
    private PitchRepository pitchRepo;
    private int currentUserId = 1; // giả định userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingRepo = new BookingRepository(this);
        pitchRepo = new PitchRepository(this);

        // Nút xoá toàn bộ lịch sử
        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(v -> confirmDeleteHistory());

        loadBookings(); // Load lần đầu
    }

    private void loadBookings() {
        List<Booking> bookings = bookingRepo.getBookingsByUser(currentUserId);
        List<Pitch> pitches = pitchRepo.getAllPitches();

        BookingAdapter adapter = new BookingAdapter(
                this,
                bookings,
                pitches,
                bookingId -> {
                    if (bookingRepo.cancelBooking(bookingId)) {
                        Toast.makeText(this, "Hủy thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Hủy thất bại", Toast.LENGTH_SHORT).show();
                    }
                    loadBookings(); // reload sau khi hủy
                }
        );
        rvBookings.setAdapter(adapter);
    }

    private void confirmDeleteHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá lịch sử")
                .setMessage("Bạn có chắc chắn muốn xoá tất cả lịch sử đặt sân?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    boolean success = bookingRepo.deleteAllBookingsByUser(currentUserId);
                    if (success) {
                        Toast.makeText(this, "Đã xoá tất cả lịch sử", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Không có gì để xoá", Toast.LENGTH_SHORT).show();
                    }
                    loadBookings();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
