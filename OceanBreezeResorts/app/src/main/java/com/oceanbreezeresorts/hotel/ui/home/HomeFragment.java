package com.oceanbreezeresorts.hotel.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.oceanbreezeresorts.hotel.AboutUs;
import com.oceanbreezeresorts.hotel.Chart;
import com.oceanbreezeresorts.hotel.MainActivity;
import com.oceanbreezeresorts.hotel.R;
import com.oceanbreezeresorts.hotel.SingleProductView;
import com.oceanbreezeresorts.hotel.databinding.FragmentHomeBinding;
import com.oceanbreezeresorts.hotel.ui.booking.BookingFragment;
import com.oceanbreezeresorts.hotel.ui.profile.ProfileFragment;
import com.oceanbreezeresorts.hotel.view.LoginActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        askNotificationPermission();

        ImageSlider imageSlider = binding.imageSlider;

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.image1,"Welcome to OceanBreeze", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, "Enjoy Luxurious Stays", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, "Book Your Dream Vacation",ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);



        Button button = binding.button9;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = binding.button9;
                button.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), Chart.class);
                    startActivity(intent);
                });
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button aboutbtn = binding.button8;
        aboutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use getActivity() or requireContext() to obtain the context
                Intent intent = new Intent(getActivity(), AboutUs.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {

                }
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show a custom UI explaining why the app needs this permission
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}