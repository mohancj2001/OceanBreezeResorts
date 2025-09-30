package com.oceanbreezeresorts.hotel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.model.Customer;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingleProductView extends AppCompatActivity {


    private long checkInDateInMillis;
    private long checkOutDateInMillis;
    private static final String TAG = "OceanApp";
    Customer customer = null;
    int randomNumber=0;
    private JsonObject roomJsonPay;
    String qtyStr,cId,roomId;
    double totalAmount;
    private final ActivityResultLauncher<Intent> payHereResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            if (response.getData() != null) { // Check if data is not null

                                StatusResponse data2 = response.getData();
                                Log.i(TAG,data2.toString());
//                                Log.i("OceanApp", data2.toString());

                                if ("2".equals(String.valueOf(data2.getStatus()))) {
                                    Log.i(TAG, data2.getMessage() + " " + data2.getPrice());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            OkHttpClient client = OkHttpSingleton.getInstance();
                                            Gson gson = new Gson();
                                            JsonObject jsonObject = new JsonObject();

                                            Date checkInDate = new Date(checkInDateInMillis);
                                            Date checkOutDate = new Date(checkOutDateInMillis);

//                                            jsonObject.addProperty("eventPackageId", eventPackage1.getId());
                                            jsonObject.addProperty("from_date", checkInDate.toString());
                                            jsonObject.addProperty("to_date", checkOutDate.toString());
                                            jsonObject.addProperty("totalPaymentItem", roomJsonPay.get("price").getAsDouble());
                                            jsonObject.addProperty("totalPayment", totalAmount);
                                            jsonObject.addProperty("qty", qtyStr);
//                                            jsonObject.addProperty("per_member_price", eventPackage1.getPrice_per_member());
//                                            jsonObject.addProperty("payment_id", randomNumber);
                                            jsonObject.addProperty("customer_id", cId);
                                            jsonObject.addProperty("rooms_id", roomId);
//                                            jsonObject.addProperty("locations_id", eventPackage1.getLocation());

                                            String json = gson.toJson(jsonObject);
                                            RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
                                            Request request = new Request.Builder()
                                                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/PlaceBooking")
                                                    .post(requestBody)
                                                    .build();

                                            client.newCall(request).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                    Log.e("OceanApp", "Request Failed: " + e.getMessage());
                                                    runOnUiThread(() -> Toast.makeText(SingleProductView.this, "Failed to connect. Please try again.", Toast.LENGTH_LONG).show());
                                                }

                                                @Override
                                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        String responseData = response.body().string(); // Read response once
                                                        Log.i("OceanApp", responseData);

                                                        Gson gson = new Gson();
                                                        JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);

                                                        boolean success = jsonObject.get("success").getAsBoolean();
                                                        String message = jsonObject.get("message").getAsString(); // Correctly extract message

                                                        runOnUiThread(() -> {
                                                            Toast.makeText(SingleProductView.this, message, Toast.LENGTH_LONG).show();

                                                            if (success) {
                                                                Intent i = new Intent(SingleProductView.this, HomeActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        });
                                                    } else {
                                                        runOnUiThread(() -> Toast.makeText(SingleProductView.this, "Server error. Please try again.", Toast.LENGTH_LONG).show());
                                                        Log.e("OceanApp", "Response unsuccessful: " + response.code());
                                                    }
                                                }
                                            });

                                        }
                                    }).start();
                                } else {
                                    Log.i(TAG, data2.getMessage());
                                }
                            } else {
                                Log.w(TAG, "Response data is null");
                            }

                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Toast.makeText(SingleProductView.this, "Payment Canceled", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product_view);

        // Ensure the view exists before applying insets
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        Intent intent = getIntent();
        roomId = intent.getStringExtra("ROOM_ID");
        // Image Slider setup
        ImageSlider imageSlider = findViewById(R.id.imageSlider1);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" + roomId + "/" + roomId + "image1.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" + roomId + "/" + roomId + "image2.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" + roomId + "/" + roomId + "image3.jpg", ScaleTypes.FIT));
//        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
//        slideModels.add(new SlideModel("http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" + roomId + "/" + roomId + "image2.jpg", ScaleTypes.FIT));
//        slideModels.add(new SlideModel("http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" + roomId + "/" + roomId + "image3.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(slideModels);

//        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra("ROOM_ID")) {
//            String roomId = intent.getStringExtra("ROOM_ID");
//            Log.d("OceanApp", "Received Room ID in SingleProductView: " + roomId);
//        } else {
//            Log.e("OceanApp", "ROOM_ID is missing in SingleProductView Intent");
//        }




        if (intent != null && intent.hasExtra("ROOM_ID")) {
            roomId = intent.getStringExtra("ROOM_ID");
            Log.d("OceanApp", "Received Room ID in SingleProductView: " + roomId);
            new Thread(() -> {

                OkHttpClient client = OkHttpSingleton.getInstance();
                String url = "http://192.168.1.117:8080/OceanBreeze_Resorts/LoadSingleProductView?room_id=" + roomId;

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            Gson gson = new Gson();
//                            String responseData = response.body().string();
//                            Log.d("OceanApp", "Response Data: " + responseData);
//
//                            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
//                            if (jsonObject.has("roomList")) {
//                                JsonArray roomListJson = jsonObject.getAsJsonArray("roomList");
//                            }
                            String responseBody = response.body() != null ? response.body().string() : "";
                            Log.i("OceanApp", "Response: " + responseBody);

                            try {
                                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                                if (!jsonResponse.has("rooms")) {
                                    throw new IllegalStateException("Missing customer data in response");
                                }

                                JsonObject roomJson = jsonResponse.getAsJsonObject("rooms");
                                Log.i("OceanApp", roomJson.toString());
                                roomJsonPay = roomJson;

                                TextView titleTextView = findViewById(R.id.textView26);
                                TextView categoryTextView = findViewById(R.id.textView33);
                                TextView ratingTextView = findViewById(R.id.textView28);
                                TextView priceTextView = findViewById(R.id.textView36);
                                TextView descriptionTextView = findViewById(R.id.textView37);
                                TextView locationTextView = findViewById(R.id.textView34);

                                titleTextView.setText(roomJson.get("title").getAsString());
                                categoryTextView.setText(roomJson.getAsJsonObject("room_Types").get("room_type").getAsString());
                                locationTextView.setText(roomJson.getAsJsonObject("hotel")
                                        .getAsJsonObject("locations")
                                        .get("city")
                                        .getAsString());
                                ratingTextView.setText(roomJson.get("rating").getAsString());
                                priceTextView.setText(roomJson.get("price").getAsString());
                                descriptionTextView.setText(roomJson.get("description").getAsString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("OceanApp", "Request Failed: " + e.getMessage());
                    }

                });
            }).start();


        }

        CalendarView checkInCalendarView = findViewById(R.id.calendarView4);
        CalendarView checkOutCalendarView = findViewById(R.id.calendarView5);
        Button bookingBtn = findViewById(R.id.booking_btn);

        checkInDateInMillis = checkInCalendarView.getDate();
        checkOutDateInMillis = checkOutCalendarView.getDate();

        checkInCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                checkInDateInMillis = calendar.getTimeInMillis();
            }
        });


        checkOutCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                checkOutDateInMillis = calendar.getTimeInMillis();
            }
        });


        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedCheckInDate = sdf.format(new Date(checkInDateInMillis));
                String formattedCheckOutDate = sdf.format(new Date(checkOutDateInMillis));

                EditText qty = findViewById(R.id.qty_txt);

                Log.i("OceanApp", "Qty: " + qty.getText().toString());
                Log.i("OceanApp", "Check-in Date: " + formattedCheckInDate);
                Log.i("OceanApp", "Check-out Date: " + formattedCheckOutDate);

                initiatePayment();
            }
        });


    }
    private void initiatePayment() {

        SharedPreferences sp =  SingleProductView.this.getSharedPreferences("com.oceanbreezeresorts.data", Context.MODE_PRIVATE);
        String firstName = sp.getString("first_name", "Guest");
        String lastName = sp.getString("last_name", "");
        String email = sp.getString("email", "");
        String mobile = sp.getString("mobile", "");
        cId = sp.getString("id", "");

        if (email != null) {
//            customer = new Gson().fromJson(userJson, Customer.class);

            Random random = new Random();
            randomNumber = 1000 + random.nextInt(9000);
            EditText qty = findViewById(R.id.qty_txt);
            qtyStr = qty.getText().toString();
            int quantity = Integer.parseInt(qtyStr);
            Date checkInDate = new Date(checkInDateInMillis);
            Date checkOutDate = new Date(checkOutDateInMillis);
            long timeDifference = checkOutDate.getTime() - checkInDate.getTime();
            long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);
            int days = (int) daysDifference;

            totalAmount = roomJsonPay.get("price").getAsDouble()*quantity*days;

            InitRequest req = new InitRequest();
            req.setMerchantId("1221209");
            req.setCurrency("LKR");
            req.setAmount(totalAmount);
            req.setOrderId(String.valueOf(randomNumber));
            req.setItemsDescription(roomJsonPay.get("title").getAsString());

            req.setCustom1("This is the custom message 1");
            req.setCustom2("This is the custom message 2");
            req.getCustomer().setFirstName(firstName);
            req.getCustomer().setLastName(lastName);
            req.getCustomer().setEmail(email);
            req.getCustomer().setPhone(mobile);
            req.getCustomer().getAddress().setAddress("No 120");
            req.getCustomer().getAddress().setCity("Colombo");
            req.getCustomer().getAddress().setCountry("Sri Lanka");

            Intent intent1 = new Intent(this, PHMainActivity.class);
            intent1.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
            payHereResultLauncher.launch(intent1);
        } else {
            Toast.makeText(SingleProductView.this, "Customer not Found Please Login!", Toast.LENGTH_LONG).show();
        }
    }
}
