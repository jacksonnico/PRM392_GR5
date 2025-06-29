package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.MainActivity;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.AdminMainActivity;
import com.example.prm392_gr5.Ui.auth.HomeActivity;

public class LoginActivity extends Activity {

    // Login
    EditText edtUsername, edtPassword;
    CheckBox chkSaveLogin;
    Button btnLogin;
    ImageView btnTogglePwd;
    boolean isPasswordVisible = false;

    // Register
    EditText edtFullName, edtPhoneReg, edtPasswordReg, edtPasswordConfirm;
    ImageView btnTogglePwdReg, btnTogglePwdConfirm;
    Button btnRegister, btnRegisterOwner;
    boolean isPasswordRegVisible = false;
    boolean isPasswordConfirmVisible = false;

    // Tab
    LinearLayout formLogin, formRegister;
    TextView tabLogin, tabRegister, tvForgotPassword;

    UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepo = new UserRepository(this);
        mappingViews();
        setupLoginFunctions();
        setupRegisterFunctions();
        setupTabSwitch();

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });


    }

    private void mappingViews() {
        // Login
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        chkSaveLogin = findViewById(R.id.chkSaveLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnTogglePwd = findViewById(R.id.btnTogglePwd);

        // Register
        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneReg = findViewById(R.id.edtPhoneReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        btnTogglePwdReg = findViewById(R.id.btnTogglePwdReg);
        btnTogglePwdConfirm = findViewById(R.id.btnTogglePwdConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegisterOwner = findViewById(R.id.btnRegisterOwner);

        // Tab
        formLogin = findViewById(R.id.formLogin);
        formRegister = findViewById(R.id.formRegister);
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    // Login Functions
    private void setupLoginFunctions() {
        edtUsername.setText(SharedPreferencesHelper.getSavedPhone(this));
        edtPassword.setText(SharedPreferencesHelper.getSavedPassword(this));

        btnTogglePwd.setOnClickListener(v -> {
            isPasswordVisible = togglePassword(edtPassword, btnTogglePwd, isPasswordVisible);
        });

        btnLogin.setOnClickListener(v -> {
            String phone = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || pass.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ thông tin");
                return;
            }
            if (!userRepo.isPhoneExists(phone)) {
                showToast("Số điện thoại chưa đăng ký. Vui lòng đăng ký để truy cập.");
                return;
            }

            User user = userRepo.login(phone, pass);
            if (user != null) {
                if (chkSaveLogin.isChecked()) {
                    SharedPreferencesHelper.saveLogin(this, phone, pass);
                } else {
                    SharedPreferencesHelper.clear(this);
                }

                SharedPreferencesHelper.saveUserInfo(this, user.getFullName(), user.getPhoneNumber(), user.getRole());
                showToast("Đăng nhập thành công");

                // Điều hướng theo role
                switch (user.getRole()) {
                    case "admin":
                        startActivity(new Intent(this, AdminMainActivity.class));
                        break;
                    case "owner":
//                        startActivity(new Intent(this, OwnerActivity.class));
                        break;
                    case "user":
                    default:
                        startActivity(new Intent(this, HomeActivity.class));
                        break;
                }

                finish();
            } else {
                showToast("Sai tài khoản hoặc mật khẩu");
            }
        });
    }

    // Register Functions
    private void setupRegisterFunctions() {
        btnTogglePwdReg.setOnClickListener(v -> {
            isPasswordRegVisible = togglePassword(edtPasswordReg, btnTogglePwdReg, isPasswordRegVisible);
        });

        btnTogglePwdConfirm.setOnClickListener(v -> {
            isPasswordConfirmVisible = togglePassword(edtPasswordConfirm, btnTogglePwdConfirm, isPasswordConfirmVisible);
        });

        btnRegister.setOnClickListener(v -> handleRegister("user"));
        btnRegisterOwner.setOnClickListener(v -> handleRegister("owner"));
    }

    private void handleRegister(String role) {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhoneReg.getText().toString().trim();
        String pass = edtPasswordReg.getText().toString().trim();
        String confirm = edtPasswordConfirm.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin");
            return;
        }
        if (!fullName.matches("^[\\p{L} .'-]+$")) {
            Toast.makeText(this, "Họ tên không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("^0[3|5|7|8|9][0-9]{8}$")) {
            showToast("Số điện thoại không hợp lệ");
            return;
        }
        if (!isValidPassword(pass)) {
            showToast("Mật khẩu phải từ 6-20 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt");
            return;
        }

        if (!pass.equals(confirm)) {
            showToast("Mật khẩu xác nhận không khớp");
            return;
        }

        if (userRepo.isPhoneExists(phone)) {
            showToast("Số điện thoại đã tồn tại");
            return;
        }

        String otpCode = String.valueOf((int) (100000 + Math.random() * 900000));

        Intent intent = new Intent(this, OtpActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("fullName", fullName);
        intent.putExtra("password", pass);
        intent.putExtra("role", role);
        intent.putExtra("otp", otpCode);
        intent.putExtra("type", "register"); // Đánh dấu là đăng ký
        startActivity(intent);

        Toast.makeText(this, "Mã OTP của bạn là: " + otpCode, Toast.LENGTH_LONG).show();
    }

    private void clearRegisterForm() {
        edtFullName.setText("");
        edtPhoneReg.setText("");
        edtPasswordReg.setText("");
        edtPasswordConfirm.setText("");
    }

    // Toggle Password Visibility
    private boolean togglePassword(EditText editText, ImageView toggleBtn, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleBtn.setImageResource(R.drawable.ic_eye_closed);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleBtn.setImageResource(R.drawable.ic_eye_open);
        }
        editText.setSelection(editText.length());
        return !isVisible;
    }

    // Tab Switch
    private void setupTabSwitch() {
        tabLogin.setOnClickListener(v -> {
            formLogin.setVisibility(View.VISIBLE);
            formRegister.setVisibility(View.GONE);
            tabLogin.setBackgroundResource(R.drawable.tab_selected);
            tabRegister.setBackgroundColor(Color.TRANSPARENT);
            tabLogin.setTextColor(Color.BLACK);
            tabRegister.setTextColor(Color.parseColor("#B3E5FC"));
        });

        tabRegister.setOnClickListener(v -> {
            formLogin.setVisibility(View.GONE);
            formRegister.setVisibility(View.VISIBLE);
            tabRegister.setBackgroundResource(R.drawable.tab_selected);
            tabLogin.setBackgroundColor(Color.TRANSPARENT);
            tabRegister.setTextColor(Color.BLACK);
            tabLogin.setTextColor(Color.parseColor("#B3E5FC"));
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 20) return false;
        if (password.contains(" ")) return false;

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
