package fpt.edu.vn.vinho_app.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import fpt.edu.vn.vinho_app.R;

public class ChatbotFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private ImageView btnOpenDrawer;
    private NavigationView navView;
    private RecyclerView recyclerChatMessages;
    private TextView tvWelcomeMessage;
    private EditText editTextMessage;
    private FloatingActionButton btnSendMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạm thời ẩn StatusBar của Activity
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hiện lại StatusBar khi Fragment bị hủy
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapViews(view);
        setupListeners();
    }

    private void mapViews(View view) {
        drawerLayout = view.findViewById(R.id.drawer_layout);
        btnOpenDrawer = view.findViewById(R.id.btnOpenDrawer); // Ánh xạ nút back mới
        navView = view.findViewById(R.id.nav_view);
        recyclerChatMessages = view.findViewById(R.id.recycler_chat_messages);
        tvWelcomeMessage = view.findViewById(R.id.tv_welcome_message);
        editTextMessage = view.findViewById(R.id.edit_text_message);
        btnSendMessage = view.findViewById(R.id.btn_send_message);
    }

    private void setupListeners() {
        // SỬA LẠI: Sự kiện cho nút Back
        btnOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btnSendMessage.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });

        View headerView = navView.getHeaderView(0);
        headerView.findViewById(R.id.btnNewChat).setOnClickListener(v -> {
            newChat();
            drawerLayout.closeDrawer(GravityCompat.START);
        });
        ImageView btnCloseDrawer = headerView.findViewById(R.id.btnCloseDrawer);
        if (btnCloseDrawer != null) {
            btnCloseDrawer.setOnClickListener(v -> {
                // Gọi hàm onBackPressed của Activity cha để đóng toàn bộ fragment
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void sendMessage(String message) {
        Toast.makeText(getContext(), "Sending: " + message, Toast.LENGTH_SHORT).show();
        editTextMessage.setText("");
        if (tvWelcomeMessage.getVisibility() == View.VISIBLE) {
            tvWelcomeMessage.setVisibility(View.GONE);
        }
    }

    private void newChat() {
        Toast.makeText(getContext(), "New chat started!", Toast.LENGTH_SHORT).show();
        tvWelcomeMessage.setVisibility(View.VISIBLE);
    }
}
