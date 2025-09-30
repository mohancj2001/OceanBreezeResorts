package com.oceanbreezeresorts.hotel.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.ForgetPassword;
import com.oceanbreezeresorts.hotel.HomeActivity;
import com.oceanbreezeresorts.hotel.R;
import com.oceanbreezeresorts.hotel.model.Customer;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView register = findViewById(R.id.textView5);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button button = findViewById(R.id.signin_btn);
        button.setOnClickListener(view -> {
            EditText emailText = findViewById(R.id.login_email_txt);
            EditText passwordText = findViewById(R.id.login_password_txt);

            String email = emailText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loginUser(email, password);
                }
            }).start();

        });
        EditText emailText = findViewById(R.id.login_email_txt);
        TextView forgotPassword = findViewById(R.id.textView3);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Must Add Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                OkHttpClient client = OkHttpSingleton.getInstance();
                Gson gson = new Gson();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("email", email);

                String json = gson.toJson(jsonObject);
                RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url("http://192.168.1.117:8080/OceanBreeze_Resorts/Verify_User")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("oceanBreezeLog", "Request failed: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Request failed. Please try again.", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid email or server error.", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        String responseBody = response.body() != null ? response.body().string() : "";
                        Log.i("OceanApp", "Response: " + responseBody);

                        try {
                            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                            if (!jsonResponse.has("customer")) {
                                throw new IllegalStateException("Missing customer data in response");
                            }

                            JsonObject customerJson = jsonResponse.getAsJsonObject("customer");

                            runOnUiThread(() -> {
                                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                                intent.putExtra("customer_data", customerJson.toString());
                                startActivity(intent);
                                finish();
                            });

                        } catch (Exception e) {
                            Log.e("OceanApp", "JSON Parsing Error: " + e.getMessage());
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        });

    }

    private void loginUser(String email, String password) {
        OkHttpClient client = OkHttpSingleton.getInstance();
        Gson gson = new Gson();

        String json = gson.toJson(new Customer(email, password));
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.1.117:8080/OceanBreeze_Resorts/SignIn")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("oceanBreezeLog", "Request failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.i("OceanApp", "Response: " + responseBody);

//                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Response received successfully!", Toast.LENGTH_SHORT).show());

                    try {
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                        if (!jsonResponse.has("customer")) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show());
                            throw new IllegalStateException("Missing customer data in response");
                        }

                        JsonObject customerJson = gson.fromJson(jsonResponse.get("customer").getAsString(), JsonObject.class);

                        // Extract required fields
                        String id = customerJson.get("id").getAsString();
                        String firstName = customerJson.get("first_name").getAsString();
                        String lastName = customerJson.get("last_name").getAsString();
                        String mobile = customerJson.get("mobile").getAsString();
                        String email = customerJson.get("email").getAsString();
                        String password = customerJson.get("password").getAsString();
                        String status = customerJson.getAsJsonObject("status").get("state").getAsString();

//                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Parsing successful!", Toast.LENGTH_SHORT).show());

                        // Save only required fields
                        SharedPreferences sp = getSharedPreferences("com.oceanbreezeresorts.data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("id", id);
                        editor.putString("first_name", firstName);
                        editor.putString("last_name", lastName);
                        editor.putString("mobile", mobile);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putString("status", status);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });

                    } catch (Exception e) {
                        Log.e("OceanApp", "JSON Parsing Error: " + e.getMessage());
//                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Response unsuccessful", Toast.LENGTH_SHORT).show());
                }
            }

        });
    }
}
