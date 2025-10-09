package com.eaydin79.brick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainView extends View {

    public static final int CMD_NONE = -1;
    public static final int CMD_LEFT = 0;
    public static final int CMD_RIGHT = 1;
    public static final int CMD_DOWN = 2;
    public static final int CMD_ROTATE = 3;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bmpSoundEnabled;
    private Bitmap bmpSoundDisabled;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private BrickMatrix brickMatrix;
    private final Matrix matrix = new Matrix();
    private boolean blink = false;
    private MainThread mainThread;
    private Context context;
    private Activity activity;
    private OnGameOverListener onGameOverListener;
    private ScheduledExecutorService scheduledExecutorService;

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
        bmpSoundEnabled = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sound_enabled);
        bmpSoundDisabled = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sound_disabled);
        setWillNotDraw(false);
    }

    public void save() {
        if (Preferences.gameState == Preferences.GS_PLAY) Preferences.gameState = Preferences.GS_PAUSE;
        waitToFinishThread();
        brickMatrix.saveMatrix();
    }

    private void waitToFinishThread() {
        scheduledExecutorService.shutdown();
        try {
            if (scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) return;
            scheduledExecutorService.shutdownNow();
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
        }
    }

    private void startNewThread() {
        mainThread = new MainThread(brickMatrix);
        mainThread.setOnUpdateListener(() -> activity.runOnUiThread(this::invalidate));
        mainThread.setOnGameOverListener(() -> {
            SoundPlayer.playSound(context, R.raw.snd_gameover);
            Preferences.gameState = Preferences.GS_GAME_OVER;
            if (onGameOverListener != null) onGameOverListener.onGameOver();
        });
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(mainThread,0,16, TimeUnit.MILLISECONDS);
    }

    public void load() {
        brickMatrix = new BrickMatrix(context);
        brickMatrix.setSize(screenWidth, screenHeight);
        brickMatrix.setOnUpdateListener(() -> activity.runOnUiThread(this::invalidate));
        brickMatrix.loadMatrix();
        startNewThread();
    }

    public void newGame() {
        waitToFinishThread();
        brickMatrix = new BrickMatrix(context);
        brickMatrix.setSize(screenWidth, screenHeight);
        brickMatrix.setOnUpdateListener(() -> activity.runOnUiThread(this::invalidate));
        startNewThread();
        invalidate();
    }

    public int getTileSize() {
        return brickMatrix.getPixelSize();
    }

    public void setCommand(int command) {
        mainThread.setCommand(command);
    }

    public void setOnGameOverListener(OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenWidth = w;
        screenHeight = h;
        if (brickMatrix != null) brickMatrix.setSize(screenWidth, screenHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Preferences.gameState == Preferences.GS_INIT) {
            if (screenWidth == 0 || screenHeight == 0) {
                screenWidth = getWidth();
                screenHeight = getHeight();
            }
            return;
        }
        int tileSize = brickMatrix.getPixelSize();
        brickMatrix.draw(canvas);
        paint.setStrokeWidth(0);
        paint.setColor(Preferences.CL_ENABLE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(tileSize*0.75f);
        paint.setTextAlign(Paint.Align.RIGHT);
        blink = !blink;
        paint.setColor(blink || Preferences.gameState==Preferences.GS_PLAY ? Preferences.CL_DISABLE : Preferences.CL_ENABLE);
        canvas.drawText("Press Play", brickMatrix.getMargin().x + (10+5)*tileSize, brickMatrix.getMargin().y + tileSize*15, paint);
        matrix.setScale(tileSize/256f, tileSize/256f);
        matrix.postTranslate( brickMatrix.getMargin().x + 13*tileSize, brickMatrix.getMargin().y + tileSize*16);
        canvas.drawBitmap(Preferences.muted ? bmpSoundDisabled : bmpSoundEnabled, matrix , paint);
    }

}
