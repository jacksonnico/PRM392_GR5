package com.example.prm392_gr5.Ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

import java.util.ArrayList;
import java.util.List;

public class AddEditPitchActivity extends AppCompatActivity {
    private EditText edtName, edtPrice, edtAddress, edtPhone, edtOpen, edtClose, edtImageUrl;
    private Spinner spOwner;
    private Button btnSave;

    private PitchRepository pitchRepo;
    private UserRepository userRepo;

    private boolean isEdit = false;
    private int pitchId = -1;

    private List<User> owners = new ArrayList<>();
    private ArrayAdapter<String> ownerAdapter;
    private int selectedOwnerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pitch);

        // Init view
        edtName = findViewById(R.id.edtPitchName);
        edtPrice = findViewById(R.id.edtPitchPrice);
        edtAddress = findViewById(R.id.edtPitchAddress);
        edtPhone = findViewById(R.id.edtPitchPhone);
        edtOpen = findViewById(R.id.edtPitchOpen);
        edtClose = findViewById(R.id.edtPitchClose);
        edtImageUrl = findViewById(R.id.edtPitchImageUrl);
        spOwner = findViewById(R.id.spOwner);
        btnSave = findViewById(R.id.btnSavePitch);

        pitchRepo = new PitchRepository(this);
        userRepo = new UserRepository(this);

        loadOwners();

        if (getIntent().hasExtra("pitchId")) {
            isEdit = true;
            pitchId = getIntent().getIntExtra("pitchId", -1);
            loadPitch();
        }

        btnSave.setOnClickListener(v -> savePitch());
    }

    private void loadOwners() {
        owners = userRepo.getUsersByRole("owner");
        List<String> ownerNames = new ArrayList<>();
        for (User u : owners) {
            ownerNames.add(u.getFullName());
        }
        ownerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ownerNames);
        ownerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOwner.setAdapter(ownerAdapter);

        spOwner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOwnerId = owners.get(position).getId();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                selectedOwnerId = -1;
            }
        });
    }

    private void loadPitch() {
        Pitch p = pitchRepo.getPitchById(pitchId);
        if (p != null) {
            edtName.setText(p.getName());
            edtPrice.setText(String.valueOf(p.getPrice()));
            edtAddress.setText(p.getAddress());
            edtPhone.setText(p.getPhoneNumber());
            edtOpen.setText(p.getOpenTime());
            edtClose.setText(p.getCloseTime());
            edtImageUrl.setText(p.getImageUrl());

            for (int i = 0; i < owners.size(); i++) {
                if (owners.get(i).getId() == p.getOwnerId()) {
                    spOwner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void savePitch() {
        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String open = edtOpen.getText().toString().trim();
        String close = edtClose.getText().toString().trim();
        String image = edtImageUrl.getText().toString().trim();

        int selectedIndex = spOwner.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= owners.size()) {
            Toast.makeText(this, "Vui lòng chọn chủ sân", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedOwnerId = owners.get(selectedIndex).getId();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        Pitch p = new Pitch();
        p.setName(name);
        p.setPrice(price);
        p.setAddress(address);
        p.setPhoneNumber(phone);
        p.setOpenTime(open);
        p.setCloseTime(close);
        p.setImageUrl(image);
        p.setOwnerId(selectedOwnerId); // 🔥 cập nhật đúng owner mới từ Spinner

        if (isEdit) {
            p.setId(pitchId);
            pitchRepo.updatePitch(p);
            Toast.makeText(this, "Đã cập nhật sân", Toast.LENGTH_SHORT).show();
        } else {
            pitchRepo.addPitch(p);
            Toast.makeText(this, "Đã thêm sân", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
