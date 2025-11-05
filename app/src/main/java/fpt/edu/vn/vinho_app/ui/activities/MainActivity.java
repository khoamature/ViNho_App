package fpt.edu.vn.vinho_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.ui.fragments.AddTransactionFragment;
import fpt.edu.vn.vinho_app.ui.fragments.BudgetFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ChatbotFragment;
import fpt.edu.vn.vinho_app.ui.fragments.HomeFragment;
import fpt.edu.vn.vinho_app.ui.fragments.InsightsFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ProfileFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ReportFragment;
import fpt.edu.vn.vinho_app.ui.fragments.TransactionFragment;
import fpt.edu.vn.vinho_app.ui.viewmodel.SharedViewModel;

// SỬA LẠI: Implement cả 2 interface
public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener, InsightsFragment.OnInsightFragmentInteractionListener {
    private BottomNavigationView bottomNav;
    private MaterialCardView fabAdd;
    private FrameLayout frameContainer, fullScreenContainer;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Ánh xạ các Views
        bottomNav = findViewById(R.id.bottom_nav);
        fabAdd = findViewById(R.id.fab_add);
        frameContainer = findViewById(R.id.frame_container);
        fullScreenContainer = findViewById(R.id.full_screen_container);

        // Sự kiện click cho nút Add (FAB)
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                AddTransactionFragment addTransactionFragment = new AddTransactionFragment();
                addTransactionFragment.show(getSupportFragmentManager(), addTransactionFragment.getTag());
            });
        }

        // Vô hiệu hóa item ở giữa BottomNav
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_placeholder).setEnabled(false);
        }

        // Load fragment mặc định khi ứng dụng khởi động
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Sự kiện click cho các item trên BottomNav
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_transaction) {
                    selectedFragment = new TransactionFragment();
                } else if (itemId == R.id.nav_budget) {
                    selectedFragment = new BudgetFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_placeholder) {
                    return false;
                }
                return loadFragment(selectedFragment);
            });
        }

        // Xử lý nút Back theo cách mới, tương thích ngược
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Kiểm tra xem ChatbotFragment có đang mở không (dựa vào Back Stack)
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    // Nếu có, pop nó ra (đóng ChatbotFragment)
                    getSupportFragmentManager().popBackStack();
                    // Hiện lại các UI đã ẩn
                    fullScreenContainer.setVisibility(View.GONE);
                    bottomNav.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.VISIBLE);
                } else {
                    // Nếu không, thực hiện hành vi back mặc định
                    setEnabled(false);
                    onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Implement phương thức từ HomeFragment
    @Override
    public void onReportsButtonClicked() {
        loadFragment(new ReportFragment());
    }

    // Implement phương thức từ HomeFragment
    @Override
    public void onInsightButtonClicked() {
        loadFragment(new InsightsFragment());
    }

    // Implement phương thức từ InsightsFragment
    @Override
    public void onChatbotButtonClicked() {
        ChatbotFragment chatbotFragment = new ChatbotFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.full_screen_container, chatbotFragment)
                .addToBackStack("chatbot") // Đặt tên để quản lý
                .commit();

        // Ẩn các UI không cần thiết
        fullScreenContainer.setVisibility(View.VISIBLE);
        bottomNav.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
    }
}
