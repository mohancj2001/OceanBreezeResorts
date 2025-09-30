package com.oceanbreezeresorts.hotel.view;

import static com.oceanbreezeresorts.hotel.model.Validation.isValidEmail;
import static com.oceanbreezeresorts.hotel.model.Validation.isValidMobile;
import static com.oceanbreezeresorts.hotel.model.Validation.isValidPassword;

import android.content.Intent;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.HomeActivity;
import com.oceanbreezeresorts.hotel.R;
import com.oceanbreezeresorts.hotel.Verify_Email;
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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView register = findViewById(R.id.textView8);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        Button register_btn = findViewById(R.id.reg_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText fname_text = findViewById(R.id.fname_txt);
                EditText lname_text = findViewById(R.id.lname_txt);
                EditText email_text = findViewById(R.id.email_txt);
                EditText password_text = findViewById(R.id.password_txt);
                EditText mobile_text = findViewById(R.id.mobile_txt);

                String fname = fname_text.getText().toString();
                String lname = lname_text.getText().toString();
                String email = email_text.getText().toString();
                String password = password_text.getText().toString();
                String mobile = mobile_text.getText().toString();


                if (fname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "First Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (lname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Last Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mobile.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Mobile cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!isValidMobile(mobile)) {
                    Toast.makeText(RegisterActivity.this, "Invalid Mobile", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    Customer customer = new Customer(fname, lname, mobile, password, email);
                    Gson gson = new Gson();
                    String json = gson.toJson(customer);
                    RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));



                    // Create HTTP request using OkHttp
                    OkHttpClient client = OkHttpSingleton.getInstance();
                    Request request = new Request.Builder()
                            .url("http://192.168.1.117:8080/OceanBreeze_Resorts/Register")
                            .post(requestBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e("RegisterActivity", "Request failed: " + e.getMessage());
                            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();
                                JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                                if (jsonObject.get("status").getAsBoolean()) {
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(RegisterActivity.this, Verify_Email.class);
                                        intent.putExtra("CUSTOMER_JSON", json);
                                        startActivity(intent);
                                        finish();
                                    });
                                } else {
                                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    });

//                  new Thread(new Runnable() {
//                      @Override
//                      public void run() {
//                          OkHttpClient client = OkHttpSingleton.getInstance();
//                          Gson gson = new Gson();
//
//                          String json = gson.toJson(new Customer(fname,lname,mobile,password, email));
//                          RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
//
//                          Request request = new Request.Builder()
//                                  .url("http://192.168.1.117:8080/OceanBreeze_Resorts/Register")
//                                  .post(requestBody)
//                                  .build();
//
//                          client.newCall(request).enqueue(new Callback() {
//                              @Override
//                              public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                  Log.e("oceanBreezeLog", "Request failed: " + e.getMessage());
//                                  runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show());
//                              }
//                              @Override
//                              public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//                                  Log.i("appLog", json);
//
//                                  if (response.isSuccessful()) {
//                                      String responseBody = response.body().string();
//                                      JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
//
//                                      if (jsonObject.get("status").getAsBoolean()) {
//                                          Intent intent = new Intent(RegisterActivity.this, Verify_Email.class);
//                                          startActivity(intent);
//                                          finish();
//                                      } else {
//                                          runOnUiThread(new Runnable() {
//                                              @Override
//                                              public void run() {
//                                                  Toast.makeText(RegisterActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
//                                              }
//                                          });
//                                      }
//                                  }
//                              }
//                          });
//                      }
//                  }).start();
                }
            }
        });


    }

}

