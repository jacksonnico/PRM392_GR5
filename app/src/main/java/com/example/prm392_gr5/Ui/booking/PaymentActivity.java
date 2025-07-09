package com.example.prm392_gr5.Ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.HomeActivity;

public class PaymentActivity extends AppCompatActivity {
    private BookingRepository bookingRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bookingRepo = new BookingRepository(this);

        // Lấy bookingIds từ Intent (array)
        Long[] bookingIds = (Long[]) getIntent().getSerializableExtra("bookingIds");
        if (bookingIds == null || bookingIds.length == 0) {
            Toast.makeText(this, "Booking không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        ImageView ivQr = findViewById(R.id.imageView);
        Button btnConfirm = findViewById(R.id.btnConfirmPayment);
        String qrUrl = "https://img.vietqr.io/image/BIDV-3950175888-compact2.png"
                + "?amount=500000"
                + "&addInfo=Chuy%E1%BB%83n%20ti%E1%BB%81n%20s%C3%A2n";

        // Load QR code vào ImageView
        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.qr)
                .into(ivQr);

        // Xác nhận thanh toán
        btnConfirm.setOnClickListener(v -> {
            boolean allSuccess = true;

            // Cập nhật trạng thái cho tất cả booking
            for (Long bookingId : bookingIds) {
                boolean ok = bookingRepo.updateBookingStatus(bookingId.intValue(), "confirmed");
                if (!ok) {
                    allSuccess = false;
                }
            }

            Toast.makeText(
                    this,
                    allSuccess ? "Thanh toán thành công" : "Có lỗi xảy ra khi cập nhật trạng thái",
                    Toast.LENGTH_SHORT
            ).show();

            // Quay về danh sách sân (xóa hết backstack)
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}