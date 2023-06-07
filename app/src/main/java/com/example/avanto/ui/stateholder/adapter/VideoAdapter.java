package com.example.avanto.ui.stateholder.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.avanto.R;
import com.example.avanto.data.model.Video;
import com.example.avanto.databinding.ItemVideoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
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
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            bottomSheetView.findViewById(R.id.mebu_down).setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });
            bottomSheetView.findViewById(R.id.menu_share).setOnClickListener(v2 -> {
                bottomSheetDialog.dismiss();
                shareFile(position);
            });
            bottomSheetView.findViewById(R.id.menu_rename).setOnClickListener(v3 -> {
                bottomSheetDialog.dismiss();
                renameFile(position, v);
            });
            bottomSheetView.findViewById(R.id.menu_delete).setOnClickListener(v4 -> {
                bottomSheetDialog.dismiss();
                deleteFile(position, v);
            });
            bottomSheetView.findViewById(R.id.menu_proporties).setOnClickListener(v5 -> {
                bottomSheetDialog.dismiss();
                showProperties(position);
            });
        });


    }

    private void shareFile(int position) {
        Uri uri = Uri.parse(videoFolder.get(position).getPath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "share"));
        Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
    }

    private void deleteFile(int position, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("delete")
                .setMessage(videoFolder.get(position).getTitle())
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(videoFolder.get(position).getId()));
                        File file = new File(videoFolder.get(position).getPath());
                        boolean deleted = file.delete();
                        if (deleted) {
                            context.getApplicationContext().getContentResolver().delete(contentUri, null, null);
                            videoFolder.remove(position);
                            notifyItemRemoved(position);
                            notifyItemChanged(position, videoFolder.size());
                            Snackbar.make(view, "File Deleted Successfuly", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(view, "File Deleted Failed", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private void renameFile(int position, View view) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_rename_layout);
        final EditText editText = dialog.findViewById(R.id.rename_edit_text);
        Button cancel = dialog.findViewById(R.id.cancel_rename_button);
        Button rename_btn = dialog.findViewById(R.id.rename_button);
        final File rename_file = new File(videoFolder.get(position).getPath());
        String nameText = rename_file.getName();
        nameText = nameText.substring(0, nameText.lastIndexOf("."));
        editText.setText(nameText);
        editText.clearFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        cancel.setOnClickListener(v -> {dialog.dismiss();});
        rename_btn.setOnClickListener(v1 -> {
            String onlyPath = rename_file.getParentFile().getAbsolutePath();
            String ext = rename_file.getAbsolutePath();
            ext = ext.substring(ext.lastIndexOf("."));
            String newPath = onlyPath + "/" + editText.getText() + ext;
            File newFile = new File(newPath);
            boolean rename = rename_file.renameTo(newFile);
            if (rename) {
                context.getApplicationContext().getContentResolver()
                        .delete(MediaStore.Files.getContentUri("external"),
                        MediaStore.MediaColumns.DATA + "=?", new String[] {rename_file.getAbsolutePath()});
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                context.getApplicationContext().sendBroadcast(intent);
                Snackbar.make(view, "File Renamed Successfuly", Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(view, "File Renamed Failed", Snackbar.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showProperties(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_proporties_layout);

        String name = videoFolder.get(position).getTitle();
        String path = videoFolder.get(position).getPath();
        String size = videoFolder.get(position).getSize();
        String duration = videoFolder.get(position).getDuration();
        String resolution = videoFolder.get(position).getResolution();

        TextView tit = dialog.findViewById(R.id.pro_title);
        TextView st = dialog.findViewById(R.id.pro_storage);
        TextView siz = dialog.findViewById(R.id.pro_size);
        TextView dur = dialog.findViewById(R.id.pro_duration);
        TextView res = dialog.findViewById(R.id.pro_resolution);

        tit.setText(name);
        st.setText(path);
        siz.setText(size);
        dur.setText(duration);
        res.setText(resolution + "p");

        dialog.show();
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
