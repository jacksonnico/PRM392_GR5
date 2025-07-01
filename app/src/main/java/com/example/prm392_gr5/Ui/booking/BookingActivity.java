package com.example.prm392_gr5.Ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;            // <-- thêm import này
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class BookingActivity extends AppCompatActivity {
    private BookingRepository bookingRepo;
    private PitchRepository pitchRepo;

    private LinearLayout llDates;
    private CheckBox cbRepeat;
    private Spinner spinnerTimeSlot;
    private TextView tvSelectedPitch;
    private Button btnBookNow;

    private final Set<String> selectedDates = new HashSet<>();
    private String selectedTimeSlot;
    private int currentUserId = 1;
    private int selectedPitchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingRepo = new BookingRepository(this);
        pitchRepo   = new PitchRepository(this);

        // Lấy pitchId từ Intent
        selectedPitchId = getIntent().getIntExtra("pitchId", -1);
        if (selectedPitchId < 0) {
            Toast.makeText(this, "Không có sân được chọn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Pitch pitch = pitchRepo.getPitchById(selectedPitchId);
        if (pitch == null) {
            Toast.makeText(this, "Sân không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind views
        llDates          = findViewById(R.id.llDates);
        cbRepeat         = findViewById(R.id.cbRepeat);
        spinnerTimeSlot  = findViewById(R.id.spinnerTimeSlot);
        tvSelectedPitch  = findViewById(R.id.tvSelectedPitch);
        btnBookNow       = findViewById(R.id.btnBookNow);

        // Hiển thị tên sân đã chọn
        tvSelectedPitch.setText(pitch.getName());

        // Khởi tạo UI
        initDateButtons();
        setupTimeSlotSpinner();
        setupBookingButton();    // <-- Bật sự kiện cho nút Đặt sân ngay
    }

    private void initDateButtons() {
        Locale vi = new Locale("vi", "VN");
        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM", vi);
        SimpleDateFormat dfDay  = new SimpleDateFormat("EEEE", vi);
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String dateStr = dfDate.format(cal.getTime());
            String dayStr  = dfDay.format(cal.getTime());

            Button btn = new Button(this);
            btn.setText(dayStr + "\n" + dateStr);
            btn.setTag(dateStr);
            btn.setAllCaps(false);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                String tag = (String)v.getTag();
                if (selectedDates.contains(tag)) {
                    selectedDates.remove(tag);
                    v.setAlpha(1f);
                } else {
                    selectedDates.add(tag);
                    v.setAlpha(0.5f);
                }
                updateBookButtonState();
            });

            llDates.addView(btn);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void setupTimeSlotSpinner() {
        String[] slots = {
                "Chọn giờ",
                "6:00-8:00", "8:00-10:00", "10:00-12:00",
                "14:00-16:00", "16:00-18:00", "18:00-20:00"
        };
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, slots
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlot.setAdapter(ad);

        spinnerTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTimeSlot = pos > 0 ? slots[pos] : null;
                updateBookButtonState();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                selectedTimeSlot = null;
                updateBookButtonState();
            }
        });
    }

    private void setupBookingButton() {
        // Bật listener cho btnBookNow
        btnBookNow.setOnClickListener(v -> {
            if (selectedDates.isEmpty() || selectedTimeSlot == null) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển set ngày thành JSON array string
            String datesJson = "[\"" + String.join("\",\"", selectedDates) + "\"]";
            double deposit = 100_000 * 2 * selectedDates.size();

            // Tạo Booking (ghi nhớ đã thêm constructor 7 tham số vào Booking.java)
            Booking booking = new Booking(
                    0,
                    currentUserId,
                    selectedPitchId,
                    selectedTimeSlot,
                    datesJson,
                    deposit,
                    "pending"
            );
            long bookingId = bookingRepo.addBooking(booking);

            // Dialog chọn thanh toán
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn muốn thanh toán trước hay thanh toán sau?")
                    .setPositiveButton("Thanh toán trước", (d, w) -> {
                        Intent i = new Intent(BookingActivity.this, PaymentActivity.class);
                        i.putExtra("bookingId", bookingId);
                        startActivity(i);
                        finish();
                    })
                    .setNegativeButton("Thanh toán sau", (d, w) -> {
                        // Thêm thông báo cho lựa chọn thanh toán sau
                        Toast.makeText(
                                BookingActivity.this,
                                "Bạn đã chọn thanh toán sau.",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent i = new Intent(BookingActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    })
                    .setCancelable(false)
                    .show();

        });
    }

    private void updateBookButtonState() {
        btnBookNow.setEnabled(
                !selectedDates.isEmpty() &&
                        selectedTimeSlot != null
        );
    }
}
