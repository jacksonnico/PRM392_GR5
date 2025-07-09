package com.example.prm392_gr5.Ui.owner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;
import java.util.Locale;

public class PitchScheduleActivity extends AppCompatActivity {

    private Spinner spinnerPitches;
    private TextView tvSelectedDate;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<ScheduleInfo> scheduleList;
    private List<Pitch> pitchList;
    private PitchRepository pitchRepository;
    private int ownerId = 2;
    private int selectedPitchId = -1;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PitchSchedule", "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_schedule);

        pitchRepository = new PitchRepository(this);
        selectedDate = Calendar.getInstance();

        initViews();
        loadPitches();
        updateDateDisplay();
    }

    private void initViews() {
        spinnerPitches = findViewById(R.id.spinner_pitches);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        recyclerView = findViewById(R.id.recycler_schedule);
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        Button btnPrevDay = findViewById(R.id.btn_prev_day);
        Button btnNextDay = findViewById(R.id.btn_next_day);
        Button btnToday = findViewById(R.id.btn_today);

        scheduleList = new ArrayList<>();
        pitchList = new ArrayList<>();
        adapter = new ScheduleAdapter(this, scheduleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        spinnerPitches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPitchId = pitchList.get(position - 1).getId();
                    loadSchedule();
                } else {
                    selectedPitchId = -1;
                    scheduleList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPitchId = -1;
            }
        });

        btnPrevDay.setOnClickListener(v -> {
            selectedDate.add(Calendar.DAY_OF_MONTH, -1);
            updateDateDisplay();
            loadSchedule();
        });

        btnNextDay.setOnClickListener(v -> {
            selectedDate.add(Calendar.DAY_OF_MONTH, 1);
            updateDateDisplay();
            loadSchedule();
        });

        btnToday.setOnClickListener(v -> {
            selectedDate = Calendar.getInstance();
            updateDateDisplay();
            loadSchedule();
        });
    }

    private void updateDateDisplay() {
        tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void loadPitches() {
        pitchList = pitchRepository.getPitchesByOwnerId(ownerId);
        setupPitchSpinner();
    }

    private void setupPitchSpinner() {
        List<String> pitchNames = new ArrayList<>();
        pitchNames.add("Chọn sân bóng");

        for (Pitch pitch : pitchList) {
            pitchNames.add(pitch.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, pitchNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPitches.setAdapter(spinnerAdapter);
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    loadSchedule();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void loadSchedule() {
        if (selectedPitchId == -1) return;

        Pitch selectedPitch = null;
        for (Pitch pitch : pitchList) {
            if (pitch.getId() == selectedPitchId) {
                selectedPitch = pitch;
                break;
            }
        }

        if (selectedPitch == null) return;

        // Lấy danh sách schedule mới
        List<ScheduleInfo> newScheduleList = pitchRepository.getScheduleForPitch(selectedPitchId, selectedDate, selectedPitch);

        // Cập nhật danh sách trong adapter thông qua setter
        adapter.setScheduleList(newScheduleList);
        // Thông báo cho adapter rằng dữ liệu đã thay đổi để nó cập nhật RecyclerView
        adapter.notifyDataSetChanged();
    }
}
