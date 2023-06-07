package com.example.avanto.ui.stateholder.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avanto.R;
import com.example.avanto.data.MusicPlayerService;
import com.example.avanto.data.model.Music;
import com.example.avanto.databinding.ItemMusicBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private final ArrayList<Music> musicList;
    ExoPlayer musicPlayer;
    ConstraintLayout musicPlayerView;
    Context context;

    public MusicAdapter(Context context, ArrayList<Music> musicList, ExoPlayer musicPlayer, ConstraintLayout musicPlayerView) {
        this.context = context;
        this.musicList = musicList;
        this.musicPlayer = musicPlayer;
        this.musicPlayerView = musicPlayerView;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MusicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Music music = musicList.get(position);
        holder.bind(music);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startService(new Intent(context.getApplicationContext(), MusicPlayerService.class));
                musicPlayerView.setVisibility(View.VISIBLE);
                if (!musicPlayer.isPlaying()) {
                    musicPlayer.setMediaItems(getMediaItems(), position, 0);
                }
                else {
                    musicPlayer.pause();
                    musicPlayer.seekTo(position, 0);
                }
                musicPlayer.prepare();
                musicPlayer.play();
            }
        });
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();

        for (Music music : musicList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(music.getUri())
                    .setMediaMetadata(getMetadata(music))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetadata(Music music) {
        return new MediaMetadata.Builder()
                .setTitle(music.getTitle())
                .setArtworkUri(music.getAlbumPath())
                .build();
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void filterMusicList(List<Music> filteredList) {
        musicList.clear();
        musicList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        private final ItemMusicBinding binding;

        public MusicViewHolder(ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Music music) {
            binding.itemMusicTitle.setText(music.getTitle());
            binding.itemMusicArtist.setText(music.getArtist());
            binding.itemMusicDuration.setText(Music.formatDuration(music.getDuration()));

            Uri artworkUri = music.getAlbumPath();
            if (artworkUri != null) {
                Picasso.get().load(artworkUri).into(binding.itemMusicLogo);

                if (binding.itemMusicLogo.getDrawable() == null) {
                    binding.itemMusicLogo.setImageResource(R.drawable.ic_music);
                }
            }
        }
    }
}