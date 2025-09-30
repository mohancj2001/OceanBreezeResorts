package com.oceanbreezeresorts.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.model.Customer;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Verify_Email extends AppCompatActivity {

    private static final String TAG = "VerifyEmailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button continue_btn = findViewById(R.id.button12);
        PinView pinView = findViewById(R.id.otp_view);

        continue_btn.setOnClickListener(view -> {
            String otp = pinView.getText().toString().trim();

            if (otp.isEmpty()) {
                Toast.makeText(Verify_Email.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            String json = getIntent().getStringExtra("CUSTOMER_JSON");

            if (json == null) {
                Toast.makeText(Verify_Email.this, "Missing user data. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            Gson gson = new Gson();
            Customer customer = gson.fromJson(json, Customer.class);
            String email = customer.getEmail();

            OkHttpClient client = OkHttpSingleton.getInstance();
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .add("otp", otp)
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/Email_Verification")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(Verify_Email.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    Log.d(TAG, "Server Response: " + responseBody);

                    try {
                        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                        boolean status = jsonObject.get("status").getAsBoolean();

                        runOnUiThread(() -> {
                            if (status) {
                                Intent intent = new Intent(Verify_Email.this, HomeActivity.class); // Replace with actual home screen activity
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Verify_Email.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Response parsing error: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(Verify_Email.this, "Unexpected response from server", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });
    }
}
