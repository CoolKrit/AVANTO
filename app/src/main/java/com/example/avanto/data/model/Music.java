package com.example.avanto.data.model;

import android.net.Uri;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Music {
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
}