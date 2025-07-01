package com.example.prm392_gr5.Ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnSave;

    private UserRepository userRepo;

    private boolean isOldPassVisible = false;
    private boolean isNewPassVisible = false;
    private boolean isConfirmPassVisible = false;

    private String loggedInPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSave = findViewById(R.id.btnSavePassword);

        userRepo = new UserRepository(this);
        loggedInPhone = SharedPreferencesHelper.getSavedPhone(this); // 🔒

        setupPasswordToggle(edtOldPassword, 0);
        setupPasswordToggle(edtNewPassword, 1);
        setupPasswordToggle(edtConfirmPassword, 2);

        btnSave.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            if (!isValidPassword(newPass)) {
                showToast("Mật khẩu mới phải 6–20 ký tự, gồm chữ hoa, thường, số và ký tự đặc biệt");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                showToast("Mật khẩu xác nhận không khớp!");
                return;
            }

            boolean success = userRepo.changePassword(loggedInPhone, oldPass, newPass);
            if (success) {
                showToast("Đổi mật khẩu thành công!");
                Intent intent = new Intent(ChangePasswordActivity.this, AccountInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                showToast("Mật khẩu cũ không đúng!");
            }
        });
    }

    private void setupPasswordToggle(EditText editText, int fieldIndex) {
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    boolean isVisible;
                    switch (fieldIndex) {
                        case 0:
                            isOldPassVisible = !isOldPassVisible;
                            isVisible = isOldPassVisible;
                            break;
                        case 1:
                            isNewPassVisible = !isNewPassVisible;
                            isVisible = isNewPassVisible;
                            break;
                        case 2:
                            isConfirmPassVisible = !isConfirmPassVisible;
                            isVisible = isConfirmPassVisible;
                            break;
                        default:
                            isVisible = false;
                    }

                    if (isVisible) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0);
                    }

                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 20) return false;
        if (password.contains(" ")) return false;

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~\\-].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
