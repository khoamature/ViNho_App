package fpt.edu.vn.vinho_app.ui.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.ui.fragments.AddTransactionFragment;
import fpt.edu.vn.vinho_app.ui.fragments.BudgetFragment;
import fpt.edu.vn.vinho_app.ui.fragments.HomeFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ProfileFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ReportFragment;
import fpt.edu.vn.vinho_app.ui.fragments.TransactionFragment;
import fpt.edu.vn.vinho_app.ui.viewmodel.SharedViewModel;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener {
    private BottomNavigationView bottomNav;
    private MaterialCardView fabAdd;
    private FrameLayout frameContainer;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        bottomNav = findViewById(R.id.bottom_nav);
        fabAdd = findViewById(R.id.fab_add);
        frameContainer = findViewById(R.id.frame_container);

        // FAB click với animation
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                AddTransactionFragment addTransactionFragment = new AddTransactionFragment();
                addTransactionFragment.show(getSupportFragmentManager(), addTransactionFragment.getTag());
            });
        }

        // Disable placeholder
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_placeholder).setEnabled(false);
        }

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Bottom navigation listener
        if (bottomNav != null) {
            bottomNav.setOnNavigationItemSelectedListener(item -> {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_transaction) {
                    selectedFragment = new TransactionFragment();
                }
                else if (itemId == R.id.nav_budget) {
                    selectedFragment = new BudgetFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_placeholder) {
                    return false;
                }
                return loadFragment(selectedFragment);
            });
        }
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

    @Override
    public void onReportsButtonClicked() {
        loadFragment(new ReportFragment());
    }
}