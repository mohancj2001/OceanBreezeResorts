package com.oceanbreezeresorts.hotel.ui.profile;

import static com.oceanbreezeresorts.hotel.model.Validation.isValidEmail;
import static com.oceanbreezeresorts.hotel.model.Validation.isValidMobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.oceanbreezeresorts.hotel.databinding.FragmentProfileBinding;
import com.oceanbreezeresorts.hotel.model.Customer;
import com.oceanbreezeresorts.hotel.model.OkHttpSingleton;
import com.oceanbreezeresorts.hotel.view.LoginActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private EditText fnameTxt, lnameTxt, emailTxt, mobileTxt, passwordTxt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView16;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        fnameTxt = binding.profileFnameTxt;
        lnameTxt = binding.profileLnameTxt;
        emailTxt = binding.profileEmailTxt;
        mobileTxt = binding.profileMobileTxt;
        passwordTxt = binding.profilePasswordTxt;

        emailTxt.setEnabled(false);

        loadData();

        binding.updateProfileBtn.setOnClickListener(view -> updateProfile());

        return root;
    }

    private void updateProfile() {
        String fname = fnameTxt.getText().toString().trim();
        String lname = lnameTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();
        String mobile = mobileTxt.getText().toString().trim();
        String password = passwordTxt.getText().toString().trim();

        if (fname.isEmpty()) {
            showToast("First Name cannot be empty");
            return;
        }
        if (lname.isEmpty()) {
            showToast("Last Name cannot be empty");
            return;
        }
        if (email.isEmpty() || !isValidEmail(email)) {
            showToast("Invalid Email");
            return;
        }
        if (mobile.isEmpty() || !isValidMobile(mobile)) {
            showToast("Invalid Mobile");
            return;
        }
        if (password.isEmpty()) {
            showToast("Password cannot be empty");
            return;
        }

        Customer customer = new Customer(fname, lname, mobile, password, email);
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        OkHttpClient client = OkHttpSingleton.getInstance();
        Request request = new Request.Builder()
                .url("http://192.168.1.117:8080/OceanBreeze_Resorts/UpdateCustomer")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> showToast("Update failed. Please try again."));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = "";

                    SharedPreferences sp = requireContext().getSharedPreferences("com.oceanbreezeresorts.data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.apply();

                    requireActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.putExtra("CUSTOMER_JSON", json);
                        startActivity(intent);
                        requireActivity().finish();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> showToast("Update failed. Please try again."));
                }
            }

        });
    }

    private void loadData() {
        new Thread(() -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    SharedPreferences sp = requireActivity().getSharedPreferences("com.oceanbreezeresorts.data", Context.MODE_PRIVATE);
                    fnameTxt.setText(sp.getString("first_name", "Guest"));
                    lnameTxt.setText(sp.getString("last_name", ""));
                    emailTxt.setText(sp.getString("email", ""));
                    mobileTxt.setText(sp.getString("mobile", ""));
                    passwordTxt.setText(sp.getString("password", ""));
                });
            }
        }).start();
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
