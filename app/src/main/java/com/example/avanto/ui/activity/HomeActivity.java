package com.example.avanto.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.example.avanto.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private ActivityResultLauncher<String[]> mPermissionResultLauncher;
    public boolean isPostNotificationGranted, isReadMediaAudioPermissionGranted, isReadMediaVideoPermissionGranted, isManageExternalStoragePermissionGranted, isReadExternalStoragePermissionGranted, isWriteExternalStoragePermissionGranted;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            isPostNotificationGranted = Boolean.TRUE.equals(result.get(Manifest.permission.POST_NOTIFICATIONS));
            isReadMediaAudioPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_AUDIO));
            isReadMediaVideoPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_VIDEO));
            isReadExternalStoragePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
            isWriteExternalStoragePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            isManageExternalStoragePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.MANAGE_EXTERNAL_STORAGE));
        });
        Log.e("WS", String.valueOf(isWriteExternalStoragePermissionGranted));

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
        isPostNotificationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        isReadMediaAudioPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        isReadMediaVideoPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        isReadExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        isWriteExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        isManageExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!isReadMediaAudioPermissionGranted || !isReadMediaVideoPermissionGranted || !isManageExternalStoragePermissionGranted || !isReadExternalStoragePermissionGranted || !isWriteExternalStoragePermissionGranted || !isPostNotificationGranted) {
            List<String> permissionRequest = new ArrayList<>();

            if (!isReadMediaAudioPermissionGranted) {
                permissionRequest.add(Manifest.permission.READ_MEDIA_AUDIO);
            }
            if (!isReadMediaVideoPermissionGranted) {
                permissionRequest.add(Manifest.permission.READ_MEDIA_VIDEO);
            }
            if (!isReadExternalStoragePermissionGranted) {
                permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!isWriteExternalStoragePermissionGranted) {
                permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!isManageExternalStoragePermissionGranted) {
                permissionRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
            if (!isPostNotificationGranted) {
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }
}