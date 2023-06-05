package com.example.avanto.ui.stateholder.adapter;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avanto.R;
import com.example.avanto.data.MusicMediaPlayer;
import com.example.avanto.data.model.Music;
import com.example.avanto.databinding.ItemMusicBinding;
import com.example.avanto.ui.fragment.MusicPlayerFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private final ArrayList<Music> musicList;

    public MusicAdapter(ArrayList<Music> musicList) {
        this.musicList = musicList;
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
                // Получите NavController из активности
                NavController navController = Navigation.findNavController(v);

                MusicMediaPlayer.getInstance().reset();
                MusicMediaPlayer.currentIndex = position;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("musicList", musicList);
                navController.navigate(R.id.musicPlayerFragment, bundle);

                /*// Перейдите к фрагменту AudioPlayerFragment с передачей аргумента аудио URI
                Bundle bundle = new Bundle();
                bundle.putString("audioUri", music.getUri().toString());
                navController.navigate(R.id.musicPlayerFragment, bundle);*/
            }
        });
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
                    Picasso.get().load(R.drawable.defaultuserpic).into(binding.itemMusicLogo);
                }
            }
        }
    }
}