package com.example.avanto.data;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.avanto.R;
import com.example.avanto.ui.activity.HomeActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.Objects;

public class MusicPlayerService extends Service {

    private final IBinder serviceBinder = new ServiceBinder();

    public ExoPlayer musicPlayer;
    PlayerNotificationManager notificationManager;

    public class ServiceBinder extends Binder {
        public MusicPlayerService getMusicPlayerService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        musicPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        musicPlayer.setAudioAttributes(audioAttributes, true);

        final String channelId = getResources().getString(R.string.app_name) + "Music Channel ";
        final int notificationId = 1111111;
        notificationManager = new PlayerNotificationManager.Builder(getApplicationContext(), notificationId, channelId)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(descriptionAdapter)
                .setChannelImportance(IMPORTANCE_HIGH)
                .setSmallIconResourceId(R.drawable.ic_music)
                .setChannelDescriptionResourceId(R.string.app_name)
                .setNextActionIconResourceId(R.drawable.ic_next)
                .setPreviousActionIconResourceId(R.drawable.ic_previous)
                .setPauseActionIconResourceId(R.drawable.ic_pause)
                .setPlayActionIconResourceId(R.drawable.ic_play)
                .setChannelNameResourceId(R.string.app_name)
                .build();

        notificationManager.setPlayer(musicPlayer);
        notificationManager.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.setUseRewindAction(false);
        notificationManager.setUseFastForwardAction(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer.isPlaying()) musicPlayer.stop();
        notificationManager.setPlayer(null);
        musicPlayer.release();
        musicPlayer = null;
        stopForeground(true);
        stopSelf();
    }

    PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
            stopForeground(true);
            if (musicPlayer.isPlaying())
                musicPlayer.pause();
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId, notification);
        }
    };

    PlayerNotificationManager.MediaDescriptionAdapter descriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return Objects.requireNonNull(musicPlayer.getCurrentMediaItem()).mediaMetadata.title;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            Intent openAppIntent = new Intent(getApplicationContext(), HomeActivity.class);
            return PendingIntent.getActivity(getApplicationContext(), 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return null;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            ImageView view = new ImageView(getApplicationContext());
            view.setImageURI(Objects.requireNonNull(musicPlayer.getCurrentMediaItem()).mediaMetadata.artworkUri);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
            if (bitmapDrawable == null) {
                bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.defaultuserpic);
            }
            assert bitmapDrawable != null;
            return bitmapDrawable.getBitmap();
        }
    };
}