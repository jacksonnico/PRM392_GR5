package com.example.prm392_gr5.Ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;

public class StatusChangeDialogFragment extends DialogFragment {

    private static final String ARG_BOOKING_ID = "booking_id";
    private static final String ARG_CURRENT_STATUS = "current_status";

    private int bookingId;
    private String currentStatus;
    private BookingRepository bookingRepo;
    private OnStatusChangedListener listener;

    public interface OnStatusChangedListener {
        void onStatusChanged(int bookingId, String newStatus);
    }

    public static StatusChangeDialogFragment newInstance(int bookingId, String currentStatus) {
        StatusChangeDialogFragment fragment = new StatusChangeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOOKING_ID, bookingId);
        args.putString(ARG_CURRENT_STATUS, currentStatus);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingId = getArguments().getInt(ARG_BOOKING_ID);
            currentStatus = getArguments().getString(ARG_CURRENT_STATUS);
        }
        bookingRepo = new BookingRepository(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_status, null);

        TextView tvCurrentStatus = view.findViewById(R.id.tvCurrentStatus);
        RadioGroup rgStatusOptions = view.findViewById(R.id.rgStatusOptions);
        RadioButton rbPending = view.findViewById(R.id.rbPending);
        RadioButton rbConfirmed = view.findViewById(R.id.rbConfirmed);
        RadioButton rbCompleted = view.findViewById(R.id.rbCompleted);
        RadioButton rbCancelled = view.findViewById(R.id.rbCancelled);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        // Set current status
        tvCurrentStatus.setText("Trạng thái hiện tại: " + currentStatus);

        // Set selected radio button based on current status
        setCurrentStatusSelection(rgStatusOptions, currentStatus);

        // Set up click listeners
        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            int selectedId = rgStatusOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Vui lòng chọn trạng thái mới", Toast.LENGTH_SHORT).show();
                return;
            }

            String newStatus = getStatusFromRadioButton(selectedId);
            if (newStatus.equals(currentStatus)) {
                Toast.makeText(getContext(), "Trạng thái mới giống trạng thái hiện tại", Toast.LENGTH_SHORT).show();
                return;
            }

            updateBookingStatus(newStatus);
        });

        builder.setView(view);
        return builder.create();
    }

    private void setCurrentStatusSelection(RadioGroup radioGroup, String status) {
        switch (status.toLowerCase()) {
            case "đang chờ xác nhận":
            case "pending":
                radioGroup.check(R.id.rbPending);
                break;
            case "đã xác nhận":
            case "confirmed":
                radioGroup.check(R.id.rbConfirmed);
                break;
            case "hoàn thành":
            case "completed":
                radioGroup.check(R.id.rbCompleted);
                break;
            case "đã hủy":
            case "cancelled":
                radioGroup.check(R.id.rbCancelled);
                break;
        }
    }

    private String getStatusFromRadioButton(int radioButtonId) {
        if (radioButtonId == R.id.rbPending) {
            return "Đang chờ xác nhận";
        } else if (radioButtonId == R.id.rbConfirmed) {
            return "Đã xác nhận";
        } else if (radioButtonId == R.id.rbCompleted) {
            return "Hoàn thành";
        } else if (radioButtonId == R.id.rbCancelled) {
            return "Đã hủy";
        }
        return "";
    }

    private void updateBookingStatus(String newStatus) {
        try {
            boolean success = bookingRepo.updateBookingStatus(bookingId, newStatus);
            if (success) {
                if (listener != null) {
                    listener.onStatusChanged(bookingId, newStatus);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}