package com.eaydin79.brick;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends Activity {

    private MainView mainView;
    private MenuItem menuItemPlayPause;
    private MenuItem menuItemSound;
    private AudioManager audioManager;
    private final PointF touchPoint = new PointF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.background));
        ActionBar actionBar=getActionBar();
        if (actionBar != null) actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.background)));
        Preferences.load(this);
        mainView = new MainView(this, this);
        //mainView.setBackgroundColor(getColor(R.color.background));
        mainView.setOnGameOverListener(() -> runOnUiThread(() -> {
            Toast.makeText(this, R.string.toast_game_over, Toast.LENGTH_SHORT).show();
            setPlayPauseMenu(false);
        } ));
        setContentView(mainView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainView.invalidate();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Preferences.load(this);
        mainView.load();
        if (Preferences.firstRun) {
            Preferences.firstRun = false;
            showHelpDialog();
        }
    }

    @Override
    protected void onPause() {
        setPlayPauseMenu(false);
        super.onPause();
        mainView.save();
        Preferences.save(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Preferences.saved) return;
        mainView.save();
        Preferences.save(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItemPlayPause = menu.findItem(R.id.action_play_pause);
        menuItemSound = menu.findItem(R.id.action_sound);
        setSoundMenu();
        return super.onCreateOptionsMenu(menu);
    }

    private void pauseGame() {
        SoundPlayer.playSound(this, R.raw.snd_start);
        Preferences.gameState = Preferences.GS_PAUSE;
        setPlayPauseMenu(false);
    }

    private void playGame(boolean newGame) {
        if (newGame) mainView.newGame();
        SoundPlayer.playSound(this, R.raw.snd_start);
        Preferences.gameState = Preferences.GS_PLAY;
        setPlayPauseMenu(true);
    }

    private void setPlayPauseMenu(boolean pause) {
        menuItemPlayPause.setTitle(pause ? R.string.pause : R.string.play);
        menuItemPlayPause.setIcon(pause ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    }

    private void setSoundMenu() {
        menuItemSound.setTitle(Preferences.muted ? R.string.sound_on : R.string.sound_off);
        menuItemSound.setIcon(Preferences.muted ? R.drawable.ic_action_sound_off : R.drawable.ic_action_sound_on);
    }

    private void playPauseClick() {
        if (Preferences.gameState == Preferences.GS_PLAY) pauseGame(); else playGame(Preferences.gameState == Preferences.GS_GAME_OVER);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_help)
                .setTitle(R.string.help_dialog_title)
                .setMessage(R.string.help_dialog_text)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_button_ok, (dialogInterface, i) -> dialogInterface.cancel())
                .create()
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_about)
                .setTitle(getString(R.string.app_name))
                .setMessage("Brick game \neaydin79 2025")
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_button_ok, (dialogInterface, i) -> dialogInterface.cancel())
                .create()
                .show();
    }

    private void showOptionsDialog() {
        final boolean[] options = {Preferences.direction, Preferences.simpleShapes};
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_options)
                .setTitle(R.string.options_dialog_title)
                .setCancelable(true)
                .setNegativeButton(R.string.dialog_button_cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton(R.string.dialog_button_ok, (dialogInterface, i) -> {
                    Preferences.direction = options[0];
                    Preferences.simpleShapes = options[1];
                })
                .setMultiChoiceItems(R.array.options_dialog_items, options, (dialogInterface, index, value) -> options[index] = value)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sound) {
            if (Preferences.muted && audioManager != null && audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,0);
            Preferences.toggleMuted();
            setSoundMenu();
        }
        if (item.getItemId() == R.id.action_options) {
            if (Preferences.gameState == Preferences.GS_PLAY) pauseGame();
            showOptionsDialog();
        }
        if (item.getItemId() == R.id.action_help) {
            if (Preferences.gameState == Preferences.GS_PLAY) pauseGame();
            showHelpDialog();
        }
        if (item.getItemId() == R.id.action_about) {
            if (Preferences.gameState == Preferences.GS_PLAY) pauseGame();
            showAboutDialog();
        }
        if (item.getItemId() == R.id.action_exit) finish();
        if (item.getItemId() == R.id.action_new_game) playGame(true);
        if (item.getItemId() == R.id.action_play_pause) playPauseClick();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Preferences.gameState == Preferences.GS_PLAY)
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: mainView.setCommand(MainView.CMD_LEFT); break;
                case KeyEvent.KEYCODE_DPAD_RIGHT: mainView.setCommand(MainView.CMD_RIGHT); break;
                case KeyEvent.KEYCODE_DPAD_DOWN: mainView.setCommand(MainView.CMD_DOWN); break;
            }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER: playPauseClick(); break;
            case KeyEvent.KEYCODE_DPAD_UP: mainView.setCommand(MainView.CMD_ROTATE); break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (Preferences.gameState != Preferences.GS_PLAY) return super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: touchPoint.set(x, y); break;
            case MotionEvent.ACTION_UP :
                if (y < touchPoint.y - mainView.getTileSize()*2) {
                    touchPoint.set(x, y);
                    mainView.setCommand(MainView.CMD_ROTATE);
                } else mainView.setCommand(MainView.CMD_NONE);
                break;
            case MotionEvent.ACTION_MOVE :
                if (x < touchPoint.x - mainView.getTileSize()*3) {
                    touchPoint.set(x, y);
                    mainView.setCommand(MainView.CMD_LEFT);
                } else if (x > touchPoint.x + mainView.getTileSize()*3) {
                    touchPoint.set(x, y);
                    mainView.setCommand(MainView.CMD_RIGHT);
                }
                if (y > touchPoint.y + mainView.getTileSize()) {
                    touchPoint.set(x, y);
                    mainView.setCommand(MainView.CMD_DOWN);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}