package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.model.Service;
import com.example.prm392_gr5.Data.repository.ServiceRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.owner.adapter.ServiceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageServicesActivity extends AppCompatActivity {

    private ServiceRepository serviceRepo;
    private Spinner spinnerPitches;
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> serviceList;
    private List<Pitch> pitchList;
    private FloatingActionButton fabAddService;
    private int ownerId;
    private int selectedPitchId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        serviceRepo = new ServiceRepository(this);
        ownerId = 2;

        initViews();
        loadPitches();
    }

    private void initViews() {
        spinnerPitches = findViewById(R.id.spinner_pitches);
        recyclerView = findViewById(R.id.recycler_services);
        fabAddService = findViewById(R.id.fab_add_service);

        serviceList = new ArrayList<>();
        pitchList = new ArrayList<>();

        adapter = new ServiceAdapter(serviceList, new ServiceAdapter.OnServiceActionListener() {
            @Override
            public void onEdit(Service service) {
                showEditServiceDialog(service);
            }

            @Override
            public void onDelete(int serviceId) {
                deleteService(serviceId);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddService.setOnClickListener(v -> {
            if (selectedPitchId != -1) {
                showAddServiceDialog();
            } else {
                Toast.makeText(this, "Vui lòng chọn sân trước", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerPitches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedPitchId = pitchList.get(position - 1).getId();
                    loadServices();
                } else {
                    selectedPitchId = -1;
                    serviceList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPitchId = -1;
            }
        });
    }

    private void loadPitches() {
        pitchList.clear();
        pitchList.addAll(serviceRepo.getAllPitchesByOwner(ownerId));
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

    private void loadServices() {
        if (selectedPitchId == -1) return;

        serviceList.clear();
        serviceList.addAll(serviceRepo.getServicesByPitchId(selectedPitchId));
        adapter.notifyDataSetChanged();
    }

    private void showAddServiceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);

        EditText etName = dialogView.findViewById(R.id.et_service_name);
        EditText etPrice = dialogView.findViewById(R.id.et_service_price);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm dịch vụ mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = etName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    addService(name, price);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void addService(String name, double price) {
        boolean success = serviceRepo.insertService(selectedPitchId, name, price);

        if (success) {
            Toast.makeText(this, "Thêm dịch vụ thành công", Toast.LENGTH_SHORT).show();
            loadServices();
        } else {
            Toast.makeText(this, "Lỗi khi thêm dịch vụ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditServiceDialog(Service service) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);

        EditText etName = dialogView.findViewById(R.id.et_service_name);
        EditText etPrice = dialogView.findViewById(R.id.et_service_price);

        etName.setText(service.getName());
        etPrice.setText(String.valueOf(service.getPrice()));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sửa dịch vụ")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = etName.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    updateService(service.getId(), name, price);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void updateService(int serviceId, String name, double price) {
        boolean success = serviceRepo.updateService(serviceId, name, price);

        if (success) {
            Toast.makeText(this, "Cập nhật dịch vụ thành công", Toast.LENGTH_SHORT).show();
            loadServices();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật dịch vụ", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteService(int serviceId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa dịch vụ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = serviceRepo.deleteService(serviceId);

                    if (success) {
                        Toast.makeText(this, "Xóa dịch vụ thành công", Toast.LENGTH_SHORT).show();
                        loadServices();
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa dịch vụ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
