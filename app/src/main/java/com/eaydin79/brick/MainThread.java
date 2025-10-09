package com.eaydin79.brick;

public class MainThread implements Runnable {

    private volatile long nanoTime = 0;
    private final BrickMatrix brickMatrix;
    private OnUpdateListener onUpdateListener;
    private OnGameOverListener onGameOverListener;
    private int command = MainView.CMD_NONE;

    public MainThread(BrickMatrix brickMatrix) {
        this.brickMatrix = brickMatrix;
    }

    public synchronized void setCommand(int command) {
        this.command = command;
    }

    public synchronized int getCommand() {
        int cmd = command;
        command = MainView.CMD_NONE;
        return cmd;
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void setOnGameOverListener(OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }

    private void updateMainView() {
        if (Preferences.gameState != Preferences.GS_PLAY) {
            if (System.nanoTime() - nanoTime < 500000000) return;
            nanoTime = System.nanoTime();
        }
        if (onUpdateListener != null) onUpdateListener.onUpdate();
    }

    @Override
    public void run() {
        if (Preferences.gameState == Preferences.GS_PLAY) {
            switch (getCommand()) {
                case MainView.CMD_ROTATE: brickMatrix.rotateShape(); updateMainView(); break;
                case MainView.CMD_LEFT: brickMatrix.moveLeft(); updateMainView(); break;
                case MainView.CMD_RIGHT: brickMatrix.moveRight(); updateMainView(); break;
                case MainView.CMD_DOWN: nanoTime = 0; break;
            }
            if (System.nanoTime() - nanoTime > (840 - brickMatrix.getSpeed()*60L)*1000000) {
                if (!brickMatrix.moveDown()) {
                    if (brickMatrix.isGameOver()) {
                        Preferences.gameState = Preferences.GS_GAME_OVER;
                        if (onGameOverListener != null) onGameOverListener.onGameOver();
                    } else {
                        brickMatrix.checkRow();
                        brickMatrix.selectShape();
                    }
                }
                nanoTime = System.nanoTime();
                updateMainView();
            }
        } else updateMainView();
    }

}
