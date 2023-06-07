package com.example.avanto.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.FragmentHomeBinding;
import com.example.avanto.databinding.FragmentVideoBinding;
import com.example.avanto.ui.stateholder.adapter.FolderAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class VideoFragment extends Fragment {

    private FragmentVideoBinding binding;
    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<Video> videoList = new ArrayList<>();
    FolderAdapter folderAdapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()).getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()).getApplicationContext(), Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
            videoList = getAllVideos(Objects.requireNonNull(requireContext()));
            Toast.makeText(getContext(), "HERE!", Toast.LENGTH_SHORT).show();
        }
        recyclerView = binding.videoFoldersRV;
        if (folderList != null && folderList.size() > 0 && videoList != null) {
            folderAdapter = new FolderAdapter(folderList, videoList, getContext());
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
    }

    public ArrayList<Video> getAllVideos(Context context) {
        ArrayList<Video> tempVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };
        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String resolution = cursor.getString(4);
                String duration = cursor.getString(5);
                String disName = cursor.getString(6);
                String width_height = cursor.getString(7);
                Video videoFiles = new Video(id, path, title, size, resolution, duration, disName, width_height);
                Log.e("Path", path);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                if (!folderList.contains(subString))
                    folderList.add(subString);
                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return tempVideoFiles;
    }
}