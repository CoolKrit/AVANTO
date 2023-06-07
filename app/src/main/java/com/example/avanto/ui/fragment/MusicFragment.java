package com.example.avanto.ui.fragment;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avanto.R;
import com.example.avanto.data.MusicPlayerService;
import com.example.avanto.data.model.Music;
import com.example.avanto.databinding.FragmentMusicBinding;
import com.example.avanto.ui.stateholder.adapter.MusicAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MusicFragment extends Fragment {

    private FragmentMusicBinding binding;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private final ArrayList<Music> musicList = new ArrayList<>();

    private ExoPlayer musicPlayer;
    private ConstraintLayout playerView;
    private ImageView playerCloseBtn;
    private TextView songNameView, songArtistView, homeSongNameView;
    private ImageView skipPreviousBtn, skipNextBtn, playPauseBtn, homeSkipPreviousBtn, homePlayPauseBtn, homeSkipNextBtn, artworkView, homeSongAlbunView;
    private ConstraintLayout homeControlWrapper;
    private SeekBar seekBar;
    private TextView progressView, durationView;
    boolean isBound = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        Toast.makeText(getContext(), "onCreate", Toast.LENGTH_SHORT).show();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "onCreated", Toast.LENGTH_SHORT).show();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = binding.musicToolBar;
            activity.setSupportActionBar(toolbar);
        }

        recyclerView = binding.musicRecyclerView;
        playerView = view.findViewById(R.id.musicplayer_playerView);
        playerCloseBtn = view.findViewById(R.id.playerCloseBtn);
        songNameView = view.findViewById(R.id.songNameView);
        songArtistView = view.findViewById(R.id.songArtistView);

        skipPreviousBtn = view.findViewById(R.id.skipPreviousBtn);
        skipNextBtn = view.findViewById(R.id.skipNextBtn);
        playPauseBtn = view.findViewById(R.id.playPauseBtn);

        homeSongNameView = binding.homeSongNameView;
        homeSongAlbunView = binding.musicMusicPlayerIcon;
        homeSkipPreviousBtn = binding.homeSkipPreviousBtn;
        homePlayPauseBtn = binding.homePlayPauseBtn;
        homeSkipNextBtn = binding.homeSkipNextBtn;

        homeControlWrapper = binding.homeControlWrapper;

        artworkView = view.findViewById(R.id.artworkView);
        seekBar = view.findViewById(R.id.seekbar);
        progressView = view.findViewById(R.id.progressView);
        durationView = view.findViewById(R.id.durationView);

        doBindService();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (playerView.isEnabled())
                    exitPlayerView();
            }
        });
    }

    private void doBindService() {
        Intent musicPlayerIntent = new Intent(getContext(), MusicPlayerService.class);
        requireContext().bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection musicPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.ServiceBinder binder = (MusicPlayerService.ServiceBinder) service;
            musicPlayer = binder.getMusicPlayerService().musicPlayer;
            isBound = true;
            fetchMusicList();
            musicPlayerControls();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void musicPlayerControls() {
        songNameView.setSelected(true);
        homeSongNameView.setSelected(true);

        playerCloseBtn.setOnClickListener(v -> exitPlayerView());
        homeControlWrapper.setOnClickListener(v -> showPlayerView());

        musicPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                assert mediaItem != null;
                setResourceWithMusic();
                updateMusicPlayerPositionProgress();
                if (!musicPlayer.isPlaying()) {
                    musicPlayer.play();
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY) {
                    setResourceWithMusic();
                    updateMusicPlayerPositionProgress();
                }
                else {
                    playPauseBtn.setImageResource(R.drawable.ic_play);
                    homePlayPauseBtn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        skipNextBtn.setOnClickListener(v -> skipToNextSong());
        homeSkipNextBtn.setOnClickListener(v -> skipToNextSong());
        skipPreviousBtn.setOnClickListener(v -> skipToPreviousSong());
        homeSkipPreviousBtn.setOnClickListener(v -> skipToPreviousSong());
        playPauseBtn.setOnClickListener(v -> playOrPauseMusicPlayer());
        homePlayPauseBtn.setOnClickListener(v -> playOrPauseMusicPlayer());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicPlayer.getPlaybackState() == ExoPlayer.STATE_READY) {
                    seekBar.setProgress(progressValue);
                    progressView.setText(Music.formatDuration(progressValue));
                    musicPlayer.seekTo(progressValue);
                    musicPlayer.play();
                }
            }
        });
    }

    private void playOrPauseMusicPlayer() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
            playPauseBtn.setImageResource(R.drawable.ic_play);
            homePlayPauseBtn.setImageResource(R.drawable.ic_play);
        }
        else {
            musicPlayer.play();
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            homePlayPauseBtn.setImageResource(R.drawable.ic_pause);
        }
    }

    private void skipToPreviousSong() {
        if (musicPlayer.hasPreviousMediaItem()) {
            musicPlayer.seekToPrevious();
        }
    }

    private void skipToNextSong() {
        if (musicPlayer.hasNextMediaItem()) {
            musicPlayer.seekToNext();
        }
    }

    private void updateMusicPlayerPositionProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (musicPlayer.isPlaying()) {
                    progressView.setText(Music.formatDuration((int) musicPlayer.getCurrentPosition()));
                    seekBar.setProgress((int) musicPlayer.getCurrentPosition());
                }

                updateMusicPlayerPositionProgress();
            }
        }, 250);
    }

    private void showPlayerView() {
        playerView.setVisibility(View.VISIBLE);
    }

    private void exitPlayerView() {
        playerView.setVisibility(View.GONE);
    }

    private void setResourceWithMusic() {
        Music currentMusic = musicList.get(musicPlayer.getCurrentMediaItemIndex());
        songNameView.setText(currentMusic.getTitle());
        songArtistView.setText(currentMusic.getArtist());
        homeSongNameView.setText(currentMusic.getTitle());

        Uri artworkUri = currentMusic.getAlbumPath();
        if (artworkUri != null) {
            Picasso.get().load(artworkUri).into(artworkView);
            Picasso.get().load(artworkUri).into(homeSongAlbunView);
            if (artworkView.getDrawable() == null) {
                artworkView.setImageResource(R.drawable.ic_music);
                homeSongAlbunView.setImageResource(R.drawable.ic_music);
            }
        }

        progressView.setText(Music.formatDuration((int) musicPlayer.getCurrentPosition()));
        durationView.setText(Music.formatDuration(currentMusic.getDuration()));
        seekBar.setMax(currentMusic.getDuration());
        seekBar.setProgress((int) musicPlayer.getCurrentPosition());
        playPauseBtn.setImageResource(R.drawable.ic_pause);
        homePlayPauseBtn.setImageResource(R.drawable.ic_pause);
    }

    private void fetchMusicList() {
        ArrayList<Music> musics = new ArrayList<>();
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

    private void showMusicList(ArrayList<Music> musics) {
        if (musics.isEmpty()) {
            Toast.makeText(getContext(), "No music", Toast.LENGTH_SHORT).show();
            return;
        }

        musicList.clear();
        musicList.addAll(musics);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        musicAdapter = new MusicAdapter(getContext(), musics, musicPlayer, playerView);
        recyclerView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.music_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.music_searchButton);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            searchIcon.setColorFilter(getResources().getColor(R.color.app_unselected), PorterDuff.Mode.SRC_IN);
            ImageView closeIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            closeIcon.setColorFilter(getResources().getColor(R.color.app_unselected), PorterDuff.Mode.SRC_IN);

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
        doUnbindService();
    }

    private void doUnbindService() {
        if (isBound) {
            Objects.requireNonNull(requireContext()).unbindService(musicPlayerServiceConnection);
            isBound = false;
        }
    }
}