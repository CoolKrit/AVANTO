package com.example.avanto.ui.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avanto.R;
import com.example.avanto.data.MusicMediaPlayer;
import com.example.avanto.data.model.Music;
import com.example.avanto.databinding.FragmentMusicPlayerBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogRecord;


public class MusicPlayerFragment extends Fragment {

    FragmentMusicPlayerBinding binding;
    TextView titleMusic, artistMusic, playerPosition, playerDuration;
    SeekBar seekBar;
    ImageView album, btnPrev, btnPlay, btnNext;
    MediaPlayer mediaPlayer = MusicMediaPlayer.getInstance();
    Uri musicUri;
    List<Music> musicList;
    Music currentMusic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicList = getArguments().getParcelableArrayList("musicList");
        musicUri = musicList.get(MusicMediaPlayer.currentIndex).getUri();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleMusic = binding.musicplayerMusicName;
        artistMusic = binding.musicplayerArtistName;
        playerPosition = binding.musicplayerOngoingMusicTime;
        playerDuration = binding.musicplayerRemainingMusicTime;
        seekBar = binding.musicplayerMusicSeekBar;
        album = binding.musicPlayerAlbumImage;
        btnPrev = binding.musicplayerPreviousMusicImageButton;
        btnPlay = binding.musicplayerPlayMusicImageButton;
        btnNext = binding.musicplayerNextMusicImageButton;

        titleMusic.setSelected(true);

        setResourceWithMusic();

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    playerPosition.setText(Music.formatDuration(mediaPlayer.getCurrentPosition()));

                    if (mediaPlayer.isPlaying()) {
                        btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    }
                    else {
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        if (seekBar.getProgress() == seekBar.getMax())
                            playNextMusic();
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourceWithMusic() {
        currentMusic = musicList.get(MusicMediaPlayer.currentIndex);
        Uri artworkUri = currentMusic.getAlbumPath();
        if (artworkUri != null) {
            Picasso.get().load(artworkUri).into(album);

            if (album.getDrawable() == null) {
                Picasso.get().load(R.drawable.defaultuserpic).into(album);
            }
        }
        titleMusic.setText(currentMusic.getTitle());
        artistMusic.setText(currentMusic.getArtist());
        playerDuration.setText(Music.formatDuration(currentMusic.getDuration()));

        btnPrev.setOnClickListener(v -> playPreviousMusic());
        btnPlay.setOnClickListener(v -> pauseMusic());
        btnNext.setOnClickListener(v -> playNextMusic());

        playMusic();
    }

    private void playPreviousMusic() {
        if (MusicMediaPlayer.currentIndex == 0)
            return;
        MusicMediaPlayer.currentIndex -= 1;
        musicUri = musicList.get(MusicMediaPlayer.currentIndex).getUri();
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getContext(), musicUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playNextMusic() {
        if (MusicMediaPlayer.currentIndex == musicList.size() - 1)
            return;
        MusicMediaPlayer.currentIndex += 1;
        musicUri = musicList.get(MusicMediaPlayer.currentIndex).getUri();
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();

        // Остановите и освободите MediaPlayer при уничтожении фрагмента
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }*/
}