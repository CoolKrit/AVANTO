package com.example.avanto.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.data.model.Music;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.ActivityHomeBinding;
import com.example.avanto.databinding.ActivityVideoFolderBinding;
import com.example.avanto.ui.stateholder.adapter.VideoAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoFolderActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String MY_SORT_PREF = "sortOrder";
    private ActivityVideoFolderBinding binding;
    private RecyclerView recyclerView;
    private String name;
    private ArrayList<Video> videoArrayList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = getIntent().getStringExtra("folderName");
        recyclerView = binding.videoRecyclerview;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int index = name.lastIndexOf("/");
        String onlyFolderName = name.substring(index + 1);
        toolbar.setTitle(onlyFolderName);
        loadVideoList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        ImageView ivClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadVideoList() {
/*        videoArrayList = getAllVideoFromFolder(this, name);
        if (name != null && videoArrayList.size() > 0) {
            videoAdapter = new VideoAdapter(videoArrayList, this);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }*/
        videoArrayList = getAllVideoFromFolder(this, name);
        if (name != null && videoArrayList.size() > 0) {
            videoAdapter = new VideoAdapter(videoArrayList, this);
            //if your recyclerview lagging then just add this line
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setNestedScrollingEnabled(false);

            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(this, "Can't find any videos", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Video> getAllVideoFromFolder(Context context, String name) {
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sort = preferences.getString("sorting", "sortByDate");
        String order = null;
        switch (sort) {
            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
        }

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

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + name + "%"};

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, order);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
        switch (item.getItemId()) {

            case R.id.sort_by_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_name:
                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<Video> searchList = new ArrayList<>();
        for (Video model : videoArrayList) {
            if (model.getTitle().toLowerCase().contains(input)) {
                searchList.add(model);
            }
        }
        videoAdapter.updateSearchList(searchList);

        return false;
    }
}