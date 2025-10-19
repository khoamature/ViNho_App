package fpt.edu.vn.vinho_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.vn.vinho_app.R;

public class InsightsFragment extends Fragment {
    private TextView tvInsightResponse;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvInsightResponse = view.findViewById(R.id.tvInsightResponse);
        progressBar = view.findViewById(R.id.progressBar);

        generateInsight();
    }

    private void generateInsight() {
        progressBar.setVisibility(View.VISIBLE);
        tvInsightResponse.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");

        GenerativeModelFutures model = GenerativeModelFutures.from(ai);

        // TODO: Replace this with a real prompt based on user's financial data
        Content prompt = new Content.Builder()
                .addText("Write a short, encouraging, and actionable financial tip for a user of a budgeting app. The user is new to budgeting.")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvInsightResponse.setText(result.getText());
                    tvInsightResponse.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvInsightResponse.setText("Error: Could not generate insight. Please try again later.");
                    tvInsightResponse.setVisibility(View.VISIBLE);
                    t.printStackTrace();
                });
            }
        }, executor);
    }
}
