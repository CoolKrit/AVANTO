package com.example.avanto.ui.stateholder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avanto.R;
import com.example.avanto.data.model.Music;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.ItemFolderBinding;
import com.example.avanto.databinding.ItemMusicBinding;
import com.example.avanto.databinding.ItemVideoBinding;
import com.example.avanto.ui.activity.VideoFolderActivity;
import com.example.avanto.ui.fragment.MusicFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

    private ArrayList<String> folderName;
    private ArrayList<Video> videos;
    private Context context;

    public FolderAdapter(ArrayList<String> folderName, ArrayList<Video> videos, Context context) {
        this.folderName = folderName;
        this.videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFolderBinding binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int index = folderName.get(position).lastIndexOf("/");
        String folderNames = folderName.get(position).substring(index+1);

        holder.name.setText(folderNames);
        holder.countVideos.setText(String.valueOf(countVideos(folderName.get(position))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(context, VideoFolderActivity.class);
                intent.putExtra("folderName", folderName.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, countVideos;
        private final ItemFolderBinding binding;
        public MyViewHolder (ItemFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            name = binding.folderName;
            countVideos = binding.videosCount;
        }
    }

    int countVideos(String folders) {
        int count = 0;

        for (Video video : videos) {
            if (video.getPath().substring(0, video.getPath().lastIndexOf("/")).endsWith(folders)) {
                count++;
            }
        }
        return count;
    }
}
