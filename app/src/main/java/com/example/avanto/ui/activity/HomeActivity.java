package com.example.avanto.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.avanto.R;
import com.example.avanto.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isReadPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.READ_MEDIA_AUDIO) != null) {
                    isReadPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_AUDIO));
                }
            }
        });

        requestPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavController navController = Navigation.findNavController(this, R.id.homeFragmentContainerView);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<>();
        if (!isReadPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_MEDIA_AUDIO);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }
}