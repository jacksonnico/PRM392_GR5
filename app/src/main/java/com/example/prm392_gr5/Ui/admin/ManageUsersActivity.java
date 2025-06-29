package com.example.prm392_gr5.Ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {
    private UserRepository userRepo;
    private List<User> fullUserList;
    private List<User> filteredList;

    private EditText etSearch;
    private Spinner spRoleFilter;
    private ListView lvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        userRepo = new UserRepository(this);

        etSearch = findViewById(R.id.etSearch);
        spRoleFilter = findViewById(R.id.spRoleFilter);
        lvUsers = findViewById(R.id.lvUsers);

        setupRoleSpinner();
        setupListeners();
        loadUsers();
    }

    private void setupRoleSpinner() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tất cả", "user", "owner", "admin"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoleFilter.setAdapter(roleAdapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterUsers(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        spRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterUsers();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button btnAddUser = findViewById(R.id.btnAddUser);
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUsersActivity.this, AddEditUserActivity.class);
            intent.putExtra("mode", "add");
            startActivityForResult(intent, 100);
        });
    }

    private void loadUsers() {
        fullUserList = userRepo.getAllUsers();
        filterUsers();
    }

    private void filterUsers() {
        String keyword = etSearch.getText().toString().toLowerCase();
        String selectedRole = spRoleFilter.getSelectedItem().toString();

        filteredList = new ArrayList<>();
        for (User u : fullUserList) {
            boolean matchesRole = selectedRole.equals("Tất cả") || u.getRole().equalsIgnoreCase(selectedRole);
            boolean matchesSearch = u.getFullName().toLowerCase().contains(keyword);
            if (matchesRole && matchesSearch) {
                filteredList.add(u);
            }
        }

        lvUsers.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return filteredList.size(); }

            @Override public Object getItem(int position) { return filteredList.get(position); }

            @Override public long getItemId(int position) { return position; }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ManageUsersActivity.this).inflate(R.layout.item_user_row, parent, false);
                }

                TextView tvUserName = convertView.findViewById(R.id.tvUserName);
                TextView tvUserRole = convertView.findViewById(R.id.tvUserRole);
                Switch switchUserStatus = convertView.findViewById(R.id.switchUserStatus);
                ImageButton btnEditUser = convertView.findViewById(R.id.btnEditUser);

                User user = filteredList.get(position);

                tvUserName.setText(user.getFullName());
                tvUserRole.setText(user.getRole());
                switchUserStatus.setChecked(user.isActive());

                switchUserStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        userRepo.setUserActive(user.getId());
                    } else {
                        userRepo.setUserInactive(user.getId());
                    }
                });

                btnEditUser.setOnClickListener(v -> {
                    Intent intent = new Intent(ManageUsersActivity.this, AddEditUserActivity.class);
                    intent.putExtra("mode", "edit");
                    intent.putExtra("userId", user.getId());
                    startActivityForResult(intent, 100);
                });

                return convertView;
            }
        });
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == 100 && res == RESULT_OK) {
            loadUsers();
        }
    }
}
