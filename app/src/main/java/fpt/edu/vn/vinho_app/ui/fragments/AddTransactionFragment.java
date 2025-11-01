package fpt.edu.vn.vinho_app.ui.fragments;

import android.app.DatePickerDialog; // <-- THÊM IMPORT NÀY
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// import android.widget.CalendarView; // <-- XÓA IMPORT NÀY
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;

public class AddTransactionFragment extends BottomSheetDialogFragment {
    //Khai bao
    private Calendar currentDate;
    private TextView tvSelectedDate;
    private LinearLayout dateSelectorLayout;

    @Override
    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);

        // 1. Khởi tạo ngày
        currentDate = Calendar.getInstance();
        updateDateText();

        // 2. (Toàn bộ logic cho các nút mũi tên đã bị xóa)

        // 3. Xử lý sự kiện nhấn vào nút "Date" mới
        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());

    }

    private void updateDateText() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        String formattedDate = formatter.format(currentDate.getTime());
        formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        tvSelectedDate.setText(formattedDate);
    }

    private void showDatePickerDialog() {

        // 1. Lấy theme toàn màn hình mà chúng ta vừa tạo
        int themeResId = R.style.FullScreenMaterialDatePicker;

        // 2. Lấy ngày hiện tại (phải là mil giây)
        long currentMillis = currentDate.getTimeInMillis();

        // MaterialDatePicker luôn dùng múi giờ UTC,
        // chúng ta cần điều chỉnh để nó hiển thị đúng ngày theo giờ địa phương
        long utcMillis = currentMillis + TimeZone.getDefault().getOffset(currentMillis);

        // 3. Tạo MaterialDatePicker
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(utcMillis) // Dùng mil giây đã điều chỉnh
                        .setTheme(themeResId)    // Áp dụng theme toàn màn hình
                        .build();
        // 4. Lắng nghe khi người dùng nhấn "OK"
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 'selection' trả về là UTC, chúng ta điều chỉnh ngược lại
            long localMillis = selection - TimeZone.getDefault().getOffset(selection);

            // Cập nhật lại 'currentDate'
            currentDate.setTimeInMillis(localMillis);

            // Cập nhật lại text
            updateDateText();
        });
        // 5. Hiển thị lịch
        // Đảm bảo bạn gọi getParentFragmentManager() vì đây là một Fragment
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }
}