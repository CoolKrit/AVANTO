package com.example.avanto.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Music implements Parcelable {

    private final String title;
    private final Uri path;
    private final Uri albumPath;
    private final String artist;
    private final int duration;

    public Music(String title, Uri path, Uri albumPath, String artist, int duration) {
        this.title = title;
        this.path = path;
        this.albumPath = albumPath;
        this.artist = artist;
        this.duration = duration;
    }

    protected Music(Parcel in) {
        title = in.readString();
        path = in.readParcelable(Uri.class.getClassLoader());
        albumPath = in.readParcelable(Uri.class.getClassLoader());
        artist = in.readString();
        duration = in.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public String getTitle() {
        return title;
    }
    public Uri getUri() {
        return path;
    }
    public Uri getAlbumPath() {
        return albumPath;
    }
    public String getArtist() {
        return artist;
    }
    public int getDuration() {
        return duration;
    }

    public static String formatDuration(int totalDuration) {
        long hours = TimeUnit.MILLISECONDS.toHours(totalDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalDuration) % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(path, flags);
        dest.writeParcelable(albumPath, flags);
        dest.writeString(artist);
        dest.writeInt(duration);
    }
}