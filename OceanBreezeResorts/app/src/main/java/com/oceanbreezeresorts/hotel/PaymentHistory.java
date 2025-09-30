package com.oceanbreezeresorts.hotel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.databinding.FragmentPaymentHistoryBinding;
import com.oceanbreezeresorts.hotel.model.HistoryModel;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentHistory extends Fragment {

    private HistoryCardAdapter adapter;
    private FragmentPaymentHistoryBinding binding;

    public PaymentHistory() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.historyRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new HistoryCardAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchPaymentHistory();

        Button button = binding.cancelButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "94702292678";
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
            }
        });

        return root;
    }

    private void fetchPaymentHistory() {
        new Thread(() -> {
            SharedPreferences sp = requireContext().getSharedPreferences("com.oceanbreezeresorts.data", Context.MODE_PRIVATE);
            String id = sp.getString("id", "");

            if (id.isEmpty()) {
                Log.e("OceanApp", "User ID not found in SharedPreferences.");
                return;
            }

            OkHttpClient client = OkHttpSingleton.getInstance();
            Request request = new Request.Builder()
                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/LoadPaymentHistory?id=" + id)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        Log.d("OceanApp", "Response Data: " + responseData);

                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);

                        if (jsonObject.has("bookingList")) {
                            JsonArray bookingArray = jsonObject.getAsJsonArray("bookingList");
                            List<HistoryModel> historyList = new ArrayList<>();

                            for (JsonElement element : bookingArray) {
                                HistoryModel historyModel = gson.fromJson(element, HistoryModel.class);
                                historyList.add(historyModel);
                            }

                            requireActivity().runOnUiThread(() -> adapter.updateData(historyList));
                        } else {
                            Log.d("OceanApp", "No bookings found.");
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

    class HistoryCardAdapter extends RecyclerView.Adapter<HistoryCardAdapter.ViewHolder> {
        private List<HistoryModel> historyModelList;

        public HistoryCardAdapter(List<HistoryModel> historyModelList) {
            this.historyModelList = historyModelList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payhistory, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HistoryModel historyModel = historyModelList.get(position);
            holder.titleTextView.setText("Receipt");
            holder.dateTextView.setText(historyModel.getDate());
//            holder.fromDateTextView.setText(historyModel.getFromDate());
//            holder.toDateTextView.setText(historyModel.getToDate());
//            holder.paymentTextView.setText(historyModel.getPaymentStatus());
//            holder.qtyTextView.setText(historyModel.getQty());
            holder.priceTextView.setText(historyModel.getPrice());
        }

        @Override
        public int getItemCount() {
            return historyModelList.size();
        }

        public void updateData(List<HistoryModel> newList) {
            historyModelList.clear();
            historyModelList.addAll(newList);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, dateTextView, priceTextView, paymentTextView, qtyTextView, fromDateTextView, toDateTextView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.tvTitle);
                dateTextView = itemView.findViewById(R.id.tvDate);
                priceTextView = itemView.findViewById(R.id.tvPrice);
//                paymentTextView = itemView.findViewById(R.id.tvPaymentStatus);
//                qtyTextView = itemView.findViewById(R.id.tvQuantity);
//                fromDateTextView = itemView.findViewById(R.id.tvFromDate);
//                toDateTextView = itemView.findViewById(R.id.tvToDate);
            }
        }
    }
}
