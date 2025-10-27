package fpt.edu.vn.vinho_app.ui.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.ui.fragments.BudgetFragment;
import fpt.edu.vn.vinho_app.ui.fragments.HomeFragment;
import fpt.edu.vn.vinho_app.ui.fragments.InsightsFragment;
import fpt.edu.vn.vinho_app.ui.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private MaterialCardView fabAdd;
    private FrameLayout frameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        fabAdd = findViewById(R.id.fab_add);
        frameContainer = findViewById(R.id.frame_container);

        // FAB click với animation
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();

                // Animation nhấn
                v.animate()
                        .scaleX(0.85f)
                        .scaleY(0.85f)
                        .setDuration(100)
                        .withEndAction(() -> v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start())
                        .start();
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
                } else if (itemId == R.id.nav_budget) {
                    selectedFragment = new BudgetFragment();
                } else if (itemId == R.id.nav_insights) {
                    selectedFragment = new InsightsFragment();
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
}