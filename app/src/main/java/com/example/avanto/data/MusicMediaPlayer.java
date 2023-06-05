package com.example.avanto.data;

import android.media.MediaPlayer;

public class MusicMediaPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;
}
