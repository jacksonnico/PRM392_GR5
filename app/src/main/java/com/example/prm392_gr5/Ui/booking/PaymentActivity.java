package com.example.prm392_gr5.Ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.HomeActivity;

import java.text.NumberFormat;
import java.util.Locale;

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

        // Tính tổng tiền từ các booking
        double totalAmount = calculateTotalAmount(bookingIds);

        // Bind views
        ImageView ivQr = findViewById(R.id.imageView);
        Button btnConfirm = findViewById(R.id.btnConfirmPayment);

        // Hiển thị tổng tiền (nếu có TextView để hiển thị)
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount); // Add this to your layout if needed
        if (tvTotalAmount != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvTotalAmount.setText("Tổng tiền: " + formatter.format(totalAmount));
        }

        // Tạo QR code với số tiền thực tế
        String qrUrl = "https://img.vietqr.io/image/BIDV-3950175888-compact2.png"
                + "?amount=" + (int)totalAmount
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

            // Tạo payment record cho từng booking
            if (allSuccess) {
                for (Long bookingId : bookingIds) {
                    double bookingAmount = getBookingAmount(bookingId.intValue());
                    // Assuming you have a method to create payment record
                    // bookingRepo.createPayment(bookingId.intValue(), "VNPay", bookingAmount, "completed");
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

    private double calculateTotalAmount(Long[] bookingIds) {
        double total = 0;
        for (Long bookingId : bookingIds) {
            total += getBookingAmount(bookingId.intValue());
        }
        return total;
    }

    private double getBookingAmount(int bookingId) {
        // Get booking details and calculate amount
        // This assumes you have a method in BookingRepository to get booking details
        return bookingRepo.getBookingAmount(bookingId);
    }
}