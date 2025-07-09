package com.example.prm392_gr5.Ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    private final Set<Calendar> selectedCalendars = new HashSet<>();
    private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

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
        // Định dạng đầy đủ để lưu vào tag hoặc object
        SimpleDateFormat fullDateTagFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String dateStr = dfDate.format(cal.getTime());
            String dayStr  = dfDay.format(cal.getTime());
            String fullDateString = fullDateTagFormat.format(cal.getTime()); // Định dạng ngày đầy đủ

            Button btn = new Button(this);
            btn.setText(dayStr + "\n" + dateStr);
            btn.setTag(fullDateString); // Lưu ngày đầy đủ vào tag
            btn.setAllCaps(false);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                String tagFullDate = (String)v.getTag(); // Lấy ngày đầy đủ từ tag
                if (selectedDates.contains(tagFullDate)) { // Vẫn dùng selectedDates Set cho chuỗi ngày
                    selectedDates.remove(tagFullDate);
                    v.setAlpha(1f);
                } else {
                    selectedDates.add(tagFullDate);
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

//    private void setupBookingButton() {
//        // Bật listener cho btnBookNow
//        btnBookNow.setOnClickListener(v -> {
//            if (selectedDates.isEmpty() || selectedTimeSlot == null) {
//                Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Chuyển set ngày thành JSON array string
//            String datesJson = "[\"" + String.join("\",\"", selectedDates) + "\"]";
//            double deposit = 100_000 * 2 * selectedDates.size();
//
//            // Tạo Booking (ghi nhớ đã thêm constructor 7 tham số vào Booking.java)
//            Booking booking = new Booking(
//                    0,
//                    currentUserId,
//                    selectedPitchId,
//                    selectedTimeSlot,
//                    datesJson,
//                    deposit,
//                    "pending"
//            );
//            long bookingId = bookingRepo.addBooking(booking);
//
//            // Dialog chọn thanh toán
//            new AlertDialog.Builder(this)
//                    .setTitle("Xác nhận")
//                    .setMessage("Bạn muốn thanh toán trước hay thanh toán sau?")
//                    .setPositiveButton("Thanh toán trước", (d, w) -> {
//                        Intent i = new Intent(BookingActivity.this, PaymentActivity.class);
//                        i.putExtra("bookingId", bookingId);
//                        startActivity(i);
//                        finish();
//                    })
//                    .setNegativeButton("Thanh toán sau", (d, w) -> {
//                        // Thêm thông báo cho lựa chọn thanh toán sau
//                        Toast.makeText(
//                                BookingActivity.this,
//                                "Bạn đã chọn thanh toán sau.",
//                                Toast.LENGTH_LONG
//                        ).show();
//
//                        Intent i = new Intent(BookingActivity.this, HomeActivity.class);
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
//                    })
//                    .setCancelable(false)
//                    .show();
//
//        });
//    }
private void setupBookingButton() {
    btnBookNow.setOnClickListener(v -> {
        if (selectedDates.isEmpty() || selectedTimeSlot == null || selectedTimeSlot.equals("Chọn giờ")) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy giờ bắt đầu từ selectedTimeSlot (ví dụ "8:00-10:00" -> "8:00")
        String startTimeOnly = selectedTimeSlot.split("-")[0]; // "HH:mm"

        // Tính toán tiền cọc (giá sân * số giờ * số ngày) - giả định 100k/giờ
        // Nếu khung giờ là 2 tiếng như "8:00-10:00" thì là 2 giờ
        double pricePerHour = 100_000; // Hoặc lấy từ pitch.getPrice()
        int durationHours = 2; // Giả định mỗi slot là 2 tiếng

        // Danh sách các booking cần tạo (một booking cho mỗi ngày được chọn)
        List<Booking> bookingsToCreate = new ArrayList<>();

        for (String selectedDateStr : selectedDates) { // selectedDateStr bây giờ là "yyyy-MM-dd"
            try {
                // Kết hợp ngày và giờ thành định dạng DB
                String fullDateTimeForDb = selectedDateStr + "T" + startTimeOnly + ":00"; // "yyyy-MM-ddTHH:mm:ss"
                // Parse để đảm bảo định dạng và xử lý lỗi
                Date tempDate = dbDateFormat.parse(fullDateTimeForDb);
                fullDateTimeForDb = dbDateFormat.format(tempDate); // Chuẩn hóa lại chuỗi

                double deposit = pricePerHour * durationHours; // Tiền cọc cho 1 booking
                // Nếu bạn muốn tính theo tổng tất cả các ngày,
                // thì sẽ phải tính tổng tiền cọc ở ngoài vòng lặp.
                // Hiện tại, mỗi booking sẽ có tiền cọc riêng.

                // Tạo Booking object mới
                Booking booking = new Booking(
                        0, // ID booking (sẽ được tự động tăng trong DB)
                        currentUserId,
                        selectedPitchId,
                        fullDateTimeForDb, // Lưu DateTime đầy đủ vào cột dateTime
                        "",                // Cột services để trống hoặc null nếu không có dịch vụ
                        deposit,
                        "pending"          // Trạng thái ban đầu
                );
                bookingsToCreate.add(booking);

            } catch (ParseException e) {
                Toast.makeText(BookingActivity.this, "Lỗi định dạng ngày giờ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("BookingActivity", "Error parsing date or time for DB: " + selectedDateStr + " " + startTimeOnly, e);
                return; // Dừng lại nếu có lỗi định dạng
            }
        }

        // Thực hiện thêm tất cả các booking vào DB và thu thập IDs
        final List<Long> createdBookingIds = new ArrayList<>();
        for (Booking booking : bookingsToCreate) {
            long bookingId = bookingRepo.addBooking(booking);
            if (bookingId != -1) {
                createdBookingIds.add(bookingId);
            } else {
                Toast.makeText(BookingActivity.this, "Lỗi khi lưu booking cho ngày: " + booking.getDateTime(), Toast.LENGTH_SHORT).show();
                // Xử lý lỗi: có thể xóa các booking đã lưu thành công nếu một booking bị lỗi
                return;
            }
        }

        // Dialog chọn thanh toán
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn muốn thanh toán trước hay thanh toán sau?")
                .setPositiveButton("Thanh toán trước", (d, w) -> {
                    // Nếu có nhiều booking, bạn cần quyết định cách chuyển sang PaymentActivity
                    // Có thể chuyển danh sách IDs hoặc tổng tiền
                    // Hiện tại, tôi sẽ chỉ chuyển ID của booking đầu tiên cho ví dụ
                    if (!createdBookingIds.isEmpty()) {
                        Intent i = new Intent(BookingActivity.this, PaymentActivity.class);
                        i.putExtra("bookingId", createdBookingIds.get(0)); // Chỉ gửi ID booking đầu tiên
                        // Hoặc gửi một mảng các IDs nếu PaymentActivity có thể xử lý nhiều booking
                        // i.putExtra("bookingIds", (Serializable) createdBookingIds);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Không có booking nào được tạo để thanh toán.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Thanh toán sau", (d, w) -> {
                    Toast.makeText(
                            BookingActivity.this,
                            "Bạn đã chọn thanh toán sau. Booking của bạn đang ở trạng thái chờ duyệt.",
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
