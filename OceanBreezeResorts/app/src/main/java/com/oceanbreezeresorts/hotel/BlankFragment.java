package com.oceanbreezeresorts.hotel;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BlankFragment extends Fragment {

    private List<String> addressList = new ArrayList<>();
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.frameLayout1);

        if (supportMapFragment == null) {
            supportMapFragment = new SupportMapFragment();
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout1, supportMapFragment);
            fragmentTransaction.commit();
        }

        loadLocations();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                updateMapMarkers();
            }
        });

        return view;
    }

    private void loadLocations() {
        new Thread(() -> {
            OkHttpClient client = OkHttpSingleton.getInstance();
            Request request = new Request.Builder()
                    .url("http://192.168.1.117:8080/OceanBreeze_Resorts/LoadHotel")
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("OceanApp", "Request Failed: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        String responseData = response.body().string();
                        Log.d("OceanApp", "Response Data: " + responseData);

                        JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                        JsonArray jsonArray = jsonObject.getAsJsonArray("hotelList");

                        List<String> newAddressList = new ArrayList<>();

                        // Extract city and address for each hotel
                        for (JsonElement element : jsonArray) {
                            JsonObject hotel = element.getAsJsonObject();
                            JsonObject location = hotel.getAsJsonObject("locations");

                            String city = location.get("city").getAsString();
                            String address = city + ", " + location.get("address_line").getAsString();
                            newAddressList.add(address);
                        }

                        requireActivity().runOnUiThread(() -> {
                            addressList.clear();
                            addressList.addAll(newAddressList);
                            Toast.makeText(getContext(), "Loaded " + addressList.size() + " locations.", Toast.LENGTH_SHORT).show();
                            updateMapMarkers();
                        });
                    }
                }
            });
        }).start();
    }

    private void updateMapMarkers() {
        if (mMap == null || addressList.isEmpty()) {
            return;
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        for (String address : addressList) {
            try {
                List<Address> addressResults = geocoder.getFromLocationName(address, 1);
                if (addressResults != null && !addressResults.isEmpty()) {
                    Address location = addressResults.get(0);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Add marker
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(address));

                    // Move and zoom the camera
                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder()
                                            .target(latLng)
                                            .zoom(10)
                                            .build()
                            )
                    );
                } else {
                    Log.e("OceanApp", "Location not found for: " + address);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("OceanApp", "Geocoding failed for: " + address);
            }
        }
    }
}
