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
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvBookings   = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingRepo = new BookingRepository(this);
        pitchRepo   = new PitchRepository(this);

        Button btnClear = findViewById(R.id.btnClearHistory);
        btnClear.setOnClickListener(v -> confirmDeleteHistory());

        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookings = bookingRepo.getBookingsByUser(currentUserId);
        List<Pitch>   pitches  = pitchRepo.getAllPitches();

        BookingAdapter adapter = new BookingAdapter(
                this,
                bookings,
                pitches,
                bookingId -> new AlertDialog.Builder(this)
                        .setTitle("Xác nhận hủy")
                        .setMessage("Bạn có chắc chắn muốn hủy booking này?")
                        .setPositiveButton("Hủy", (d, w) -> {
                            boolean ok = bookingRepo.cancelBooking(bookingId);
                            Toast.makeText(
                                    this,
                                    ok ? "Hủy thành công" : "Hủy thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                            loadBookings();
                        })
                        .setNegativeButton("Không", null)
                        .show()
        );

        rvBookings.setAdapter(adapter);
    }

    private void confirmDeleteHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa lịch sử")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử đặt sân?")
                .setPositiveButton("Xóa", (dlg, w) -> {
                    boolean ok = bookingRepo.deleteAllBookingsByUser(currentUserId);
                    Toast.makeText(
                            this,
                            ok ? "Đã xóa toàn bộ lịch sử" : "Không có gì để xóa",
                            Toast.LENGTH_SHORT
                    ).show();
                    loadBookings();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
