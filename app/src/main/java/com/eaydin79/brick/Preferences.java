package com.eaydin79.brick;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final int GS_INIT = 0;
    public static final int GS_PLAY = 1;
    public static final int GS_PAUSE = 2;
    public static final int GS_GAME_OVER = 3;

    public static final int CL_SCREEN = 0xFFB2C4B2;
    public static final int CL_ENABLE = 0xFF002800;
    public static final int CL_DISABLE = 0xFFA6B8A6; // 0xFFA0B1A0;
    public static int hiScore;
    public static boolean firstRun;
    public static boolean muted;
    public static boolean direction;
    public static boolean simpleShapes;
    public static String pixels;
    public static String matrixVariables;
    public static String shape;
    public static String shapeNext;
    public static int gameState = GS_INIT;
    public static boolean saved = false;

    public static void toggleMuted() {
        muted = !muted;
    }

    public static void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BrickPreference",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("hiScore", hiScore)
                .putBoolean("firstRun", firstRun)
                .putBoolean("muted", muted)
                .putBoolean("direction", direction)
                .putBoolean("simpleShapes", simpleShapes)
                .putString("pixels", pixels)
                .putString("matrixVariables", matrixVariables)
                .putString("shape", shape)
                .putString("shapeNext", shapeNext)
                .putInt("gameState", gameState)
                .apply();
        saved = true;
    }

    public static void load(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BrickPreference",0);
        hiScore = sharedPreferences.getInt("hiScore", 100);
        firstRun = sharedPreferences.getBoolean("firstRun", true);
        muted = sharedPreferences.getBoolean("muted", false);
        direction = sharedPreferences.getBoolean("direction", true);
        simpleShapes = sharedPreferences.getBoolean("simpleShapes", true);
        pixels = sharedPreferences.getString("pixels", "");
        matrixVariables = sharedPreferences.getString("matrixVariables", "");
        shape = sharedPreferences.getString("shape", "");
        shapeNext = sharedPreferences.getString("shapeNext", "");
        gameState = sharedPreferences.getInt("gameState", GS_PAUSE);
        saved = false;
    }

}
