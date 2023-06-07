package com.example.avanto.ui.stateholder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.avanto.R;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.ItemVideoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder> {

    private ArrayList<Video> videoFolder = new ArrayList<>();
    private Context context;

    public VideoAdapter(ArrayList<Video> videoFolder, Context context) {
        this.videoFolder = videoFolder;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.title.setText(videoFolder.get(position).getTitle());
        Glide.with(context).load(videoFolder.get(position).getPath()).into(holder.thumbnail);
        holder.duration.setText(videoFolder.get(position).getDuration());
        holder.size.setText(videoFolder.get(position).getSize());
        holder.resolution.setText(videoFolder.get(position).getResolution());
        holder.menu.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.file_menu, null);

            bottomSheetView.findViewById(R.id.mebu_down).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_share).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_rename).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_delete).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_proporties).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return videoFolder.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu;
        TextView title, size, duration, resolution;
        private final ItemVideoBinding binding;

        public MyHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            thumbnail = binding.thumbnail;
            title = binding.videoTitle;
            size = binding.videoSize;
            duration = binding.videoDuration;
            resolution = binding.videoQuality;
            menu = binding.videoMenu;
        }
    }
}
