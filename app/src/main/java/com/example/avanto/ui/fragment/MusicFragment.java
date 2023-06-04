package com.example.avanto.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avanto.R;
import com.example.avanto.data.model.Music;
import com.example.avanto.databinding.FragmentMusicBinding;
import com.example.avanto.ui.stateholder.adapter.MusicAdapter;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MusicFragment extends Fragment {

    private FragmentMusicBinding binding;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private final List<Music> musicList = new ArrayList<>();
    ActivityResultLauncher<String> storagePermissionLauncher;
    final String permission = Manifest.permission.READ_MEDIA_AUDIO;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = binding.musicToolBar;
            activity.setSupportActionBar(toolbar);
        }

        recyclerView = binding.musicRecyclerView;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    fetchMusicList();
                } else {
                    userResponses();
                }
            });

            storagePermissionLauncher.launch(permission);
        } else {
            fetchMusicList();
        }
    }

    private void userResponses() {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            fetchMusicList();
        } else {
            if (shouldShowRequestPermissionRationale(permission)) {
                new AlertDialog.Builder(getContext()).setTitle("Request Permission")
                        .setMessage("Allow us to fetch music list on your device")
                        .setPositiveButton("Allow", (dialog, which) -> storagePermissionLauncher.launch(permission))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(getContext(), "You denied us to show music list", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .show();
            }
        }
    }

    private void fetchMusicList() {
        List<Music> musics = new ArrayList<>();
        Uri mediaStoreUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
        };

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = requireActivity().getContentResolver().query(mediaStoreUri, projection, null, null, sortOrder)) {
            if (cursor == null) {
                return;
            }

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String artist = cursor.getString(artistColumn);
                int duration = cursor.getInt(durationColumn);
                long albumId = cursor.getLong(albumIdColumn);

                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Uri albumArtworkUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                name = name.substring(0, name.lastIndexOf("."));
                Music music = new Music(name, uri, albumArtworkUri, artist, duration);
                musics.add(music);
            }

            showMusicList(musics);
        }
    }

    private void showMusicList(List<Music> musics) {
        if (musics.isEmpty()) {
            Toast.makeText(getContext(), "No music", Toast.LENGTH_SHORT).show();
            return;
        }

        musicList.clear();
        musicList.addAll(musics);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        musicAdapter = new MusicAdapter(musics);
        recyclerView.setAdapter(musicAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.music_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.music_searchButton);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterMusicList(newText.toLowerCase());
                    return true;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void filterMusicList(String query) {
        List<Music> filteredList = new ArrayList<>();

        for (Music music : musicList) {
            if (music.getTitle().toLowerCase().contains(query) || music.getArtist().toLowerCase().contains(query)) {
                filteredList.add(music);
            }
        }
        if (musicAdapter != null)
            musicAdapter.filterMusicList(filteredList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}