package com.oceanbreezeresorts.hotel.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.R;
import com.oceanbreezeresorts.hotel.SingleProductView;
import com.oceanbreezeresorts.hotel.databinding.FragmentBookingBinding;
import com.oceanbreezeresorts.hotel.model.Location;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;
import com.oceanbreezeresorts.hotel.model.RoomModel;
import com.oceanbreezeresorts.hotel.model.RoomTypes;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookingFragment extends Fragment {

    private FragmentBookingBinding binding;
    private RoomCardAdapter adapter;
    private Spinner locationSpinner, categorySpinner;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.roomRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<RoomModel> roomList = new ArrayList<>();
        adapter = new RoomCardAdapter(roomList, roomId -> {
            Log.d("OceanApp", "Clicked Room ID: " + roomId);
            Intent intent = new Intent(getActivity(), SingleProductView.class);
            intent.putExtra("ROOM_ID", String.valueOf(roomId));
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        locationSpinner = binding.spinner;
        categorySpinner = binding.spinner2;
        Button clearBtn = binding.clearBtn;
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSpinner.setSelection(0);
                categorySpinner.setSelection(0);
            }
        });

        fetchFilters();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchRooms(locationSpinner.getSelectedItem().toString(), categorySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        locationSpinner.setOnItemSelectedListener(filterListener);
        categorySpinner.setOnItemSelectedListener(filterListener);

        super.onViewCreated(view, savedInstanceState);
    }

    private void fetchRooms(String selectLocation, String selectCategory) {
        new Thread(() -> {
            OkHttpClient client = OkHttpSingleton.getInstance();
            Request request = new Request.Builder()
                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/LoadRooms?selectLocation=" + selectLocation + "&selectCategory=" + selectCategory)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        String responseData = response.body().string();
                        Log.d("OceanApp", "Response Data: " + responseData);

                        JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                        List<RoomModel> roomList = new ArrayList<>();

                        if (jsonObject.has("roomList")) {
                            JsonArray roomListJson = jsonObject.getAsJsonArray("roomList");

                            for (JsonElement element : roomListJson) {
                                RoomModel roomModel = gson.fromJson(element, RoomModel.class);
                                roomList.add(roomModel);
                            }
                        }

                        requireActivity().runOnUiThread(() -> {
                            adapter.updateData(roomList);
                            recyclerView.setVisibility(roomList.isEmpty() ? View.GONE : View.VISIBLE);
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("OceanApp", "Request Failed: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> recyclerView.setVisibility(View.GONE));
                }
            });
        }).start();
    }

    private void fetchFilters() {
        new Thread(() -> {
            OkHttpClient client = OkHttpSingleton.getInstance();
            Request request = new Request.Builder()
                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/LoadFilters")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("OceanApp", "Request Failed: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> recyclerView.setVisibility(View.GONE));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        String responseData = response.body().string();
                        Log.d("OceanApp", "Response Data: " + responseData);

                        JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                        List<String> roomTypesNameList = new ArrayList<>();
                        List<String> locationCategoryNameList = new ArrayList<>();

                        roomTypesNameList.add("Select");
                        locationCategoryNameList.add("Select");

                        if (jsonObject.has("eventCategoryList") && jsonObject.get("eventCategoryList").isJsonArray()) {
                            JsonArray roomTypesJson = jsonObject.getAsJsonArray("eventCategoryList");
                            for (JsonElement element : roomTypesJson) {
                                RoomTypes roomTypes = gson.fromJson(element, RoomTypes.class);
                                roomTypesNameList.add(roomTypes.getRoom_type());
                            }
                        }

                        if (jsonObject.has("eventLocationList") && jsonObject.get("eventLocationList").isJsonArray()) {
                            JsonArray locationsJson = jsonObject.getAsJsonArray("eventLocationList");
                            for (JsonElement element : locationsJson) {
                                Location location = gson.fromJson(element, Location.class);
                                locationCategoryNameList.add(location.getCity());
                            }
                        }

                        requireActivity().runOnUiThread(() -> {
                            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, roomTypesNameList);
                            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            categorySpinner.setAdapter(categoryAdapter);

                            ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(),
                                    android.R.layout.simple_spinner_item, locationCategoryNameList);
                            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            locationSpinner.setAdapter(locationAdapter);

                            if (roomTypesNameList.size() == 1 && locationCategoryNameList.size() == 1) {
                                recyclerView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


class RoomCardAdapter extends RecyclerView.Adapter<RoomCardAdapter.ViewHolder> {

    private List<RoomModel> roomModelList;
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(int roomId);
    }

    public RoomCardAdapter(List<RoomModel> roomModelList, OnRoomClickListener listener) {
        this.roomModelList = roomModelList;
        this.listener = listener;
    }

    public void updateData(List<RoomModel> newRoomList) {
        this.roomModelList.clear();
        this.roomModelList.addAll(newRoomList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomModel roomModel = roomModelList.get(position);
        holder.titleTextView.setText(roomModel.getTitle());
        holder.priceTextView.setText(String.valueOf(roomModel.getPrice()));
        holder.ratingTextView.setText(String.valueOf(roomModel.getRating()));


        String imageUrl = "http://192.168.1.117:8080/OceanBreeze_Resorts/RoomImages/" +
                roomModel.getId() + "/" + roomModel.getId() + "image1.jpg";
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.pimage1)
                .into(holder.product_image);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRoomClick(roomModel.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, priceTextView, ratingTextView;
        ImageView product_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.product_title);
            ratingTextView = itemView.findViewById(R.id.product_rating);
            priceTextView = itemView.findViewById(R.id.product_price);
            product_image = itemView.findViewById(R.id.product_image);
        }
    }
}