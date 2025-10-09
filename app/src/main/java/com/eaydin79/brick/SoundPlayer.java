package com.eaydin79.brick;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {

    public static void playSound(Context context, int rawSource) {
        if (Preferences.muted) return;
        new Thread(() -> {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, rawSource);
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
            });
            mediaPlayer.start();
        }).start();
    }

}
