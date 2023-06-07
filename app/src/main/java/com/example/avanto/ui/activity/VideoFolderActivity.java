package com.example.avanto.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.avanto.R;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.ActivityHomeBinding;
import com.example.avanto.databinding.ActivityVideoFolderBinding;
import com.example.avanto.ui.stateholder.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class VideoFolderActivity extends AppCompatActivity {

    private ActivityVideoFolderBinding binding;
    private RecyclerView recyclerView;
    private String name;
    private ArrayList<Video> videoArrayList = new ArrayList<>();
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("folderName");
        recyclerView = binding.videoRecyclerview;
        loadVideoList();
    }

    private void loadVideoList() {
        videoArrayList = getAllVideoFromFolder(this, name);
        if (name != null && videoArrayList.size() > 0) {
            videoAdapter = new VideoAdapter(videoArrayList, this);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    private ArrayList<Video> getAllVideoFromFolder(Context context, String name) {
        ArrayList<Video> VideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };
        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String resolution = cursor.getString(4);
                int duration = cursor.getInt(5);
                String disName = cursor.getString(6);
                String bucket_display_name = cursor.getString(7);
                String width_height = cursor.getString(8);

                String human_can_read = null;
                if (size < 1024)
                    human_can_read = String.format(context.getString(R.string.size_in_b), (double) size);
                else if (size < Math.pow(1024, 2))
                    human_can_read = String.format(context.getString(R.string.size_in_kb), (double)  (size / 1024));
                else if (size < Math.pow(1024, 3))
                    human_can_read = String.format(context.getString(R.string.size_in_mb), (double)  (size / Math.pow(1024, 2)));
                else
                    human_can_read = String.format(context.getString(R.string.size_in_gb), (double)  (size / Math.pow(1024, 3)));

                String duration_formatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = duration / (1000 * 60 * 60);

                if (hrs == 0) {
                    duration_formatted = String.valueOf(min)
                            .concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                }
                else {
                    duration_formatted = String.valueOf(hrs)
                            .concat(":".concat(String.format(Locale.UK, "%02d", min)
                                    .concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                }

                Video videoFiles = new Video(id, path, title, human_can_read, resolution, duration_formatted, disName, width_height);
                if (name.endsWith(bucket_display_name))
                    VideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return VideoFiles;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}