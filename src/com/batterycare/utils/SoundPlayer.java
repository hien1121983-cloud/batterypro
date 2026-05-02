package com.batterycare.utils;

import android.content.Context;
import android.media.MediaPlayer;
import com.batterycare.R;

public class SoundPlayer {
    public static void playAlertSound(Context context) {
        try {
            MediaPlayer player = MediaPlayer.create(context, R.raw.alert_sound);
            if (player != null) {
                player.setOnCompletionListener(MediaPlayer::release);
                player.start();
            }
        } catch (Exception ignored) {
        }
    }
}
