package fpt.edu.vn.vinho_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import fpt.edu.vn.vinho_app.R;

public class MainActivity extends AppCompatActivity {

    //Khai bao


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Provide a prompt that contains text
        Content prompt = new Content.Builder()
                .addText("Write a story about a magic backpack.")
                .build();

        // To generate text output, call generateContent with the text input
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                String resultText = result.getText();
//                System.out.println(resultText);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                t.printStackTrace();
//            }
//        }, executor);


    }


}