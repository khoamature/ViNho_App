package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.user.UpdateProfileRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.user.GetProfileResponse;
import fpt.edu.vn.vinho_app.data.repository.UserRepository;
import fpt.edu.vn.vinho_app.ui.activities.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView emailTextView;
    private EditText fullNameEditText;
    private TextView createdAtTextView, fullNameTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private Button btnEditSave;
    private boolean isEditMode = false; // trạng thái toggle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        emailTextView = view.findViewById(R.id.tvEmail);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        fullNameTextView = view.findViewById(R.id.tvFullName);
        createdAtTextView = view.findViewById(R.id.tvCreateAt);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnEditSave = view.findViewById(R.id.btnEditSave);

        swipeRefreshLayout.setOnRefreshListener(this::fetchProfile);

        btnLogout.setOnClickListener(v -> logout());
        btnEditSave.setOnClickListener(v -> toggleEditSave());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchProfile();
    }

    private void fetchProfile() {
        String userId = sharedPreferences.getString("userId", "");

        if (userId.isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        UserRepository.getUserService(getContext()).getProfile(userId).enqueue(new Callback<BaseResponse<GetProfileResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<GetProfileResponse>> call, Response<BaseResponse<GetProfileResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        GetProfileResponse profile = response.body().getPayload();
                        emailTextView.setText(profile.getEmail());
                        fullNameTextView.setText(profile.getFullName());
                        fullNameEditText.setText(profile.getFullName());
                        createdAtTextView.setText(profile.getCreatedAt());
                    } else {
                        Toast.makeText(getContext(), "Server error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch profile: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<GetProfileResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleEditSave() {
        if (!isEditMode) {
            // Chuyển sang chế độ chỉnh sửa
            fullNameEditText.setFocusableInTouchMode(true);
            fullNameEditText.requestFocus();
            btnEditSave.setText("Save");
            btnEditSave.setBackgroundTintList(getResources().getColorStateList(R.color.black));
            isEditMode = true;
        } else {
            // Gọi API update
            updateProfile();
        }
    }

    private void updateProfile() {
        String userId = sharedPreferences.getString("userId", "");
        String fullName = fullNameEditText.getText().toString().trim();

        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "User ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(fullName);

        UserRepository.getUserService(getContext()).updateProfile(userId, request).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    disableEditMode();
                    fetchProfile();
                } else {
                    String errorMessage = "Update failed.";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableEditMode() {
        fullNameEditText.setFocusable(false);
        btnEditSave.setText("Edit");
        btnEditSave.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        isEditMode = false;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
