package com.oceanbreezeresorts.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;
import com.oceanbreezeresorts.hotel.view.LoginActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.reset_password_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String customerDataString = getIntent().getStringExtra("customer_data");
                if (customerDataString != null) {
                    JsonObject customerJson = JsonParser.parseString(customerDataString).getAsJsonObject();

                    Log.i("CustomerData", customerJson.toString());
                    String email = customerJson.get("email").getAsString();
                    EditText passwordText = findViewById(R.id.password1_txt);
                    String password = passwordText.getText().toString().trim();

                    Log.i("OceanApp", password);
                    if (password.isEmpty()) {
                        Toast.makeText(ForgetPassword.this, "Enter new Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    OkHttpClient client = OkHttpSingleton.getInstance();
                    Gson gson = new Gson();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("password", password);
                    jsonObject.addProperty("email",email );

                    String json = gson.toJson(jsonObject);
                    RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

                    Request request = new Request.Builder()
                            .url("http://192.168.1.117:8080/OceanBreeze_Resorts/ForgetPassword")
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e("oceanBreezeLog", "Request failed: " + e.getMessage());
                            runOnUiThread(() -> Toast.makeText(ForgetPassword.this, "Request failed. Please try again.", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(ForgetPassword.this, "Invalid email or server error.", Toast.LENGTH_SHORT).show());
                                return;
                            }

                            String responseBody = response.body() != null ? response.body().string() : "";
                            Log.i("OceanApp", "Response: " + responseBody);

                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                boolean status = jsonObject.getBoolean("status");
                                if (!status) {
                                    throw new IllegalStateException("Missing customer data in response");
                                }

                                runOnUiThread(() -> {
                                    Intent intent = new Intent(ForgetPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                });

                            } catch (Exception e) {
                                Log.e("OceanApp", "JSON Parsing Error: " + e.getMessage());
                                runOnUiThread(() -> Toast.makeText(ForgetPassword.this, "Invalid server response", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
                }
            }
        });

    }
}