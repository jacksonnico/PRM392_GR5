package com.example.prm392_gr5.Ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.prm392_gr5.Data.model.Service;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.ServiceRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.HomeActivity;

import org.json.JSONArray;

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
    private ServiceRepository serviceRepo;

    private LinearLayout llDates, llServices;
    private CheckBox cbRepeat;
    private Spinner spinnerTimeSlot;
    private TextView tvSelectedPitch;
    private Button btnBookNow;

    private final Set<String> selectedDates = new HashSet<>();
    private final Set<Integer> selectedServices = new HashSet<>();
    private String selectedTimeSlot;
    private int currentUserId = 1;
    private int selectedPitchId;
    private final SimpleDateFormat dbDateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        bookingRepo = new BookingRepository(this);
        pitchRepo = new PitchRepository(this);
        serviceRepo = new ServiceRepository(this);

        selectedPitchId = getIntent().getIntExtra("pitchId", -1);
        if (selectedPitchId < 0) finishWithError("Không có sân được chọn");

        Pitch pitch = pitchRepo.getPitchById(selectedPitchId);
        if (pitch == null) finishWithError("Sân không tồn tại");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        llDates = findViewById(R.id.llDates);
        cbRepeat = findViewById(R.id.cbRepeat);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);
        tvSelectedPitch = findViewById(R.id.tvSelectedPitch);
        llServices = findViewById(R.id.llServices);
        btnBookNow = findViewById(R.id.btnBookNow);

        tvSelectedPitch.setText(pitch.getName());

        initDateButtons();
        setupCheckboxRepeat(); // ✅ THÊM DÒNG NÀY
        setupTimeSlotSpinner();
        loadServiceCheckboxes();
        setupBookingButton();
    }

    private void finishWithError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initDateButtons() {
        Locale vi = new Locale("vi", "VN");
        SimpleDateFormat dfDisplay = new SimpleDateFormat("dd/MM", vi);
        SimpleDateFormat dfDay = new SimpleDateFormat("EEEE", vi);
        SimpleDateFormat dfTag = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String dayLabel = dfDay.format(cal.getTime());
            String dateLabel = dfDisplay.format(cal.getTime());
            String fullDate = dfTag.format(cal.getTime());

            Button btn = new Button(this);
            btn.setText(dayLabel + "\n" + dateLabel);
            btn.setTag(fullDate);
            btn.setAllCaps(false);
            btn.setAlpha(1f);
            btn.setOnClickListener(v -> {
                String d = (String) v.getTag();
                if (selectedDates.contains(d)) {
                    selectedDates.remove(d);
                    v.setAlpha(1f);
                } else {
                    selectedDates.add(d);
                    v.setAlpha(0.5f);
                }
                cbRepeat.setChecked(selectedDates.size() == 7); // cập nhật trạng thái checkbox
                updateBookButtonState();
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(lp);

            llDates.addView(btn);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void setupCheckboxRepeat() {
        cbRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedDates.clear(); // clear trước
            for (int i = 0; i < llDates.getChildCount(); i++) {
                View v = llDates.getChildAt(i);
                if (v instanceof Button) {
                    String date = (String) v.getTag();
                    if (isChecked) {
                        selectedDates.add(date);
                        v.setAlpha(0.5f);
                    } else {
                        v.setAlpha(1f);
                    }
                }
            }
            updateBookButtonState();
        });
    }

    private void setupTimeSlotSpinner() {
        String[] slots = {
                "Chọn giờ", "6:00-8:00", "8:00-10:00",
                "10:00-12:00", "14:00-16:00",
                "16:00-18:00", "18:00-20:00"
        };
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, slots
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlot.setAdapter(ad);

        spinnerTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedTimeSlot = pos > 0 ? slots[pos] : null;
                updateBookButtonState();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {
                selectedTimeSlot = null;
                updateBookButtonState();
            }
        });
    }

    private void loadServiceCheckboxes() {
        List<Service> services = serviceRepo.getServicesByPitchId(selectedPitchId);
        for (Service s : services) {
            CheckBox cb = new CheckBox(this);
            cb.setText(s.getName() + " (" + String.format(Locale.getDefault(), "%,.0f", s.getPrice()) + " đ)");
            cb.setTag(s.getId());
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int sid = (Integer) buttonView.getTag();
                if (isChecked) selectedServices.add(sid);
                else selectedServices.remove(sid);
            });
            llServices.addView(cb);
        }
    }

    private void setupBookingButton() {
        btnBookNow.setOnClickListener(v -> {
            if (selectedDates.isEmpty() || selectedTimeSlot == null) {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày và khung giờ", Toast.LENGTH_SHORT).show();
                return;
            }

            String startTime = selectedTimeSlot.split("-")[0];
            Pitch selectedPitch = pitchRepo.getPitchById(selectedPitchId);
            if (selectedPitch == null) {
                Toast.makeText(this, "Không tìm thấy thông tin sân", Toast.LENGTH_LONG).show();
                return;
            }

            List<Booking> toCreate = new ArrayList<>();
            for (String date : selectedDates) {
                String dt = date + "T" + startTime + ":00";
                try {
                    Date parsed = dbDateFormat.parse(dt);
                    String norm = dbDateFormat.format(parsed);
                    String servicesJson = new JSONArray(new ArrayList<>(selectedServices)).toString();
                    toCreate.add(new Booking(
                            0, currentUserId, selectedPitchId,
                            norm, selectedTimeSlot, servicesJson, "pending"
                    ));
                } catch (ParseException e) {
                    Log.e("BookingActivity", "Parse date error", e);
                    Toast.makeText(this, "Lỗi định dạng ngày giờ", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            List<Long> ids = new ArrayList<>();
            for (Booking b : toCreate) {
                long id = bookingRepo.addBooking(b);
                if (id > 0) ids.add(id);
            }

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận đặt sân")
                    .setMessage("Bạn muốn thanh toán trước hay sau?")
                    .setPositiveButton("Trả trước", (d, w) -> {
                        Intent i = new Intent(this, PaymentActivity.class);
                        i.putExtra("bookingIds", ids.toArray(new Long[0]));
                        startActivity(i);
                        finish();
                    })
                    .setNegativeButton("Trả sau", (d, w) -> {
                        Toast.makeText(this, "Bạn đã chọn trả sau", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void updateBookButtonState() {
        btnBookNow.setEnabled(!selectedDates.isEmpty() && selectedTimeSlot != null);
    }
}
