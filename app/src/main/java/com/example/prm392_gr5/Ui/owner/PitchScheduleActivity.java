package com.example.prm392_gr5.Ui.owner;

import android.app.DatePickerDialog;
import android.content.Intent; // Import Intent
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.model.ScheduleInfo;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.owner.adapter.ScheduleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PitchScheduleActivity extends AppCompatActivity {

    private Spinner spinnerPitches;
    private TextView tvSelectedDate;
    private TextView tvHeaderSelectedDay; // TextView mới cho tiêu đề ngày (Thứ 2, Thứ 3,...)
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<Pitch> pitchList;
    private PitchRepository pitchRepository;
    private int ownerId = 2; // Ví dụ Owner ID, nên lấy từ SharedPreferences hoặc Intent
    private int selectedPitchId = -1;
    private Calendar currentSelectedDate; // Đổi tên để rõ ràng hơn, nó là ngày đang được hiển thị

    // Định dạng đầy đủ cho tvSelectedDate
    private SimpleDateFormat dateFormatFull = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
    // Định dạng chỉ tên thứ cho tvHeaderSelectedDay (Ví dụ: "Thứ Ba")
    private SimpleDateFormat dayOfWeekFormatHeader = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));

    private List<String> timeSlots;

    // KEY để truyền ownerId qua Intent
    public static final String EXTRA_OWNER_ID = "owner_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PitchSchedule", "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_schedule);

        pitchRepository = new PitchRepository(this);
        currentSelectedDate = Calendar.getInstance();
        normalizeCalendarToStartOfDay(currentSelectedDate); // Chuẩn hóa ngày về 00:00:00

        timeSlots = new ArrayList<>();
        timeSlots.add("06:00-08:00");
        timeSlots.add("08:00-10:00");
        timeSlots.add("10:00-12:00");
        timeSlots.add("14:00-16:00");
        timeSlots.add("16:00-18:00");
        timeSlots.add("18:00-20:00");

        initViews();
        loadPitches(); // Sẽ gọi loadSchedule() sau khi sân được chọn/khởi tạo
    }

    private void initViews() {
        spinnerPitches = findViewById(R.id.spinner_pitches);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvHeaderSelectedDay = findViewById(R.id.tv_header_selected_day);

        ImageView btnPrevDay = findViewById(R.id.btn_prev_day);
        ImageView btnNextDay = findViewById(R.id.btn_next_day);
        ImageView btnCalendar = findViewById(R.id.ic_calendar); // Ánh xạ icon lịch
        ImageView icNotifications = findViewById(R.id.ic_notifications); // ÁNH XẠ ICON THÔNG BÁO

        recyclerView = findViewById(R.id.recyclerViewSchedule);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ScheduleAdapter(this, new ArrayList<>(), timeSlots, currentSelectedDate);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("PitchScheduleActivity", "Error: recyclerView is null. Check activity_pitch_schedule.xml for @+id/recyclerViewSchedule");
        }

        // Click listener cho ngày
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        btnCalendar.setOnClickListener(v -> showDatePickerDialog());

        // Logic điều hướng ngày (chuyển 1 ngày)
        btnPrevDay.setOnClickListener(v -> {
            currentSelectedDate.add(Calendar.DAY_OF_MONTH, -1);
            normalizeCalendarToStartOfDay(currentSelectedDate);
            updateDateDisplay();
            loadSchedule();
        });

        btnNextDay.setOnClickListener(v -> {
            currentSelectedDate.add(Calendar.DAY_OF_MONTH, 1);
            normalizeCalendarToStartOfDay(currentSelectedDate);
            updateDateDisplay();
            loadSchedule();
        });

        // Click listener cho biểu tượng thông báo
        if (icNotifications != null) {
            icNotifications.setOnClickListener(v -> {
                Log.d("PitchSchedule", "Notifications icon clicked. Opening ApproveBookingActivity.");
                Intent intent = new Intent(PitchScheduleActivity.this, ApproveBookingActivity.class);
                intent.putExtra(EXTRA_OWNER_ID, ownerId); // Truyền ownerId
                startActivity(intent);
            });
        } else {
            Log.e("PitchScheduleActivity", "Error: ic_notifications ImageView is null. Check activity_pitch_schedule.xml for @+id/ic_notifications");
        }

        // Xử lý spinner chọn sân
        spinnerPitches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && pitchList != null && position < pitchList.size()) {
                    selectedPitchId = pitchList.get(position).getId();
                    Log.d("PitchSchedule", "Pitch selected: " + pitchList.get(position).getName() + " ID: " + selectedPitchId);
                    loadSchedule();
                } else {
                    selectedPitchId = -1;
                    Log.d("PitchSchedule", "No pitch selected or invalid position. Clearing schedule.");
                    if (adapter != null) {
                        adapter.setScheduleData(new ArrayList<>(), currentSelectedDate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPitchId = -1;
                Log.d("PitchSchedule", "Nothing selected. Clearing schedule.");
                if (adapter != null) {
                    adapter.setScheduleData(new ArrayList<>(), currentSelectedDate);
                }
            }
        });
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(dateFormatFull.format(currentSelectedDate.getTime()));
        tvHeaderSelectedDay.setText(dayOfWeekFormatHeader.format(currentSelectedDate.getTime()));
        Log.d("PitchSchedule", "Displaying schedule for: " + dateFormatFull.format(currentSelectedDate.getTime()));
    }

    private void loadPitches() {
        pitchList = pitchRepository.getPitchesByOwnerId(ownerId);
        if (pitchList == null) {
            pitchList = new ArrayList<>();
            Log.e("PitchSchedule", "Pitch list is null after fetching from repository. Initializing empty list.");
        }
        setupPitchSpinner();
    }

    private void setupPitchSpinner() {
        List<String> pitchNames = new ArrayList<>();
        for (Pitch pitch : pitchList) {
            pitchNames.add(pitch.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pitchNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPitches.setAdapter(spinnerAdapter);

        if (!pitchList.isEmpty()) {
            spinnerPitches.setSelection(0);
        } else {
            selectedPitchId = -1;
            Log.d("PitchSchedule", "No pitches found for ownerId: " + ownerId);
            if (adapter != null) {
                this.adapter.setScheduleData(new ArrayList<>(), currentSelectedDate);
            }
        }
    }

    public void loadSchedule() {
        Log.d("PitchSchedule", "loadSchedule() called for pitchId: " + selectedPitchId + " and date: " + dateFormatFull.format(currentSelectedDate.getTime()));

        if (selectedPitchId == -1) {
            Log.d("PitchSchedule", "No pitch selected. Schedule will not be loaded.");
            if (adapter != null) {
                adapter.setScheduleData(new ArrayList<>(), currentSelectedDate);
            }
            return;
        }

        Pitch selectedPitch = null;
        if (pitchList != null) {
            for (Pitch pitch : pitchList) {
                if (pitch.getId() == selectedPitchId) {
                    selectedPitch = pitch;
                    break;
                }
            }
        }

        if (selectedPitch == null) {
            Log.e("PitchSchedule", "Selected pitch not found in list for ID: " + selectedPitchId);
            if (adapter != null) {
                adapter.setScheduleData(new ArrayList<>(), currentSelectedDate);
            }
            return;
        }

        // Lấy dữ liệu lịch đặt sân cho MỘT NGÀY DUY NHẤT
        List<ScheduleInfo> dailySchedule = pitchRepository.getScheduleForPitch(selectedPitchId, currentSelectedDate, selectedPitch);

        if (dailySchedule == null) {
            dailySchedule = new ArrayList<>();
        }

        Log.d("PitchSchedule", "Fetched " + dailySchedule.size() + " schedules for " + dateFormatFull.format(currentSelectedDate.getTime()));
        for (ScheduleInfo s : dailySchedule) {
            Log.d("PitchSchedule", "  -> Slot: " + s.getTimeSlot() + ", Date: " + (s.getBookingDate() != null ? s.getBookingDate().toString() : "null") + ", Status: " + s.getStatus());
        }

        if (adapter != null) {
            adapter.setScheduleData(dailySchedule, currentSelectedDate);
            Log.d("PitchSchedule", "Adapter updated with " + dailySchedule.size() + " schedule items.");
        } else {
            Log.e("PitchSchedule", "Adapter is null, cannot load schedule.");
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    currentSelectedDate.set(Calendar.YEAR, year);
                    currentSelectedDate.set(Calendar.MONTH, month);
                    currentSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    normalizeCalendarToStartOfDay(currentSelectedDate);
                    updateDateDisplay();
                    loadSchedule();
                },
                currentSelectedDate.get(Calendar.YEAR),
                currentSelectedDate.get(Calendar.MONTH),
                currentSelectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void normalizeCalendarToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}