package com.eaydin79.brick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class BrickMatrix {
    private final boolean[][] pixels;
    private int score = 0;
    private int level = 1;
    private int broken = 0;
    private int speed = 1;
    private int pixelSize = 16;
    private final int width = 10;
    private final int height = 24;
    private int matrixWidth;
    private int matrixHeight;
    private final Point margin = new Point();
    private boolean newRecord = false;
    private final Shape shape = new Shape();
    private final Shape shapeNext = new Shape();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDigital = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Context context;
    private OnUpdateListener onUpdateListener;

    public BrickMatrix(Context context) {
        this.context = context;
        paintDigital.setTypeface(context.getResources().getFont(R.font.seven_segment));
        paintDigital.setStrokeWidth(0);
        paintDigital.setTextAlign(Paint.Align.RIGHT);
        paintDigital.setStyle(Paint.Style.FILL_AND_STROKE);
        pixels = new boolean[width][height];
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                pixels[x][y]=false;
        selectNextShape();
        selectShape();
    }

    public void setSize(int screenWidth, int screenHeight) {
        int tileWidth = screenWidth / (width + 7); //52
        int tileHeight = screenHeight / (height - 2); //66
        pixelSize = Integer.min(tileWidth, tileHeight);
        matrixWidth = pixelSize*16;
        matrixHeight = pixelSize*20;
        margin.x = (screenWidth-matrixWidth)/2;
        margin.y = (screenHeight-matrixHeight)/2;
        paintDigital.setTextSize(pixelSize);
    }

    public void loadMatrix() {
        loadPixels();
        if (Preferences.matrixVariables.isEmpty()) return;
        String[] variables = Preferences.matrixVariables.split(",");
        try {
            score = Integer.parseInt(variables[0]);
            level = Integer.parseInt(variables[1]);
            broken = Integer.parseInt(variables[2]);
            speed = Integer.parseInt(variables[3]);
        } catch (NumberFormatException e) {
            Log.e("BrickMatrix", "loadMatrix: " + e);
        }
        newRecord = Boolean.parseBoolean(variables[4]);
        shape.load(Preferences.shape);
        shapeNext.load(Preferences.shapeNext);
        setShape(true);
    }

    private void loadPixels() {
        if (Preferences.pixels.isEmpty()) return;
        String[] stringPixels = Preferences.pixels.split(",");
        int index = 0;
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                pixels[x][y] = Boolean.parseBoolean(stringPixels[index++]);
    }

    private void savePixels() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                stringBuilder.append(pixels[x][y]).append(",");
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        Preferences.pixels = stringBuilder.toString();
    }

    public void saveMatrix() {
        setShape(false);
        savePixels();
        Preferences.matrixVariables = score + "," + level + "," + broken + "," + speed + "," + newRecord;
        Preferences.shape = shape.toStr();
        Preferences.shapeNext = shapeNext.toStr();
    }

    public Point getMargin() {
        return margin;
    }

    private boolean isPixelEmpty(int x, int y) {
        return !pixels[x][y];
    }

    private boolean isRowFull(int row) {
        for(int x=0; x<width; x++)
            if (isPixelEmpty(x, row)) return false;
        return true;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    public void selectShape() {
        shape.copyFrom(shapeNext);
        shape.setXY((width-shape.getWidth()) / 2, 4-shape.getHeight() );
        selectNextShape();
    }

    public void selectNextShape() {
        int shapeType = (int) (Math.random() * (Preferences.simpleShapes ? 7 : 16));
        shapeNext.setWidth(ShapeFactory.getWidth(shapeType));
        shapeNext.setHeight(ShapeFactory.getHeight(shapeType));
        shapeNext.setPixels(ShapeFactory.getPixels(shapeType));
        int i = (int) (Math.random() * (shapeType>3 ? 4 : 2));  // don't turn simple shapes
        for(int x=0; x<i; x++) shapeNext.rotate(Shape.TO_LEFT); // i=0 don't turn
    }
    public void draw(Canvas canvas) {
        paint.setStrokeWidth(0);
        paint.setTextSize(pixelSize*0.75f);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Preferences.CL_SCREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(margin.x-2, margin.y-2, margin.x + matrixWidth + 2, margin.y + matrixHeight + 4, paint);

        paint.setColor(Preferences.CL_ENABLE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(margin.x-2, margin.y-2, margin.x + matrixWidth + 2, margin.y + matrixHeight + 4, paint);
        canvas.drawRect(margin.x-1, margin.y-1, margin.x + width*pixelSize + 4, margin.y + matrixHeight + 3, paint);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        paintDigital.setColor(Preferences.CL_DISABLE);
        canvas.drawText("0000",margin.x + (width+5)*pixelSize, margin.y + pixelSize*2, paintDigital);
        canvas.drawText("0000",margin.x + (width+5)*pixelSize, margin.y + pixelSize*4, paintDigital);
        canvas.drawText("000",margin.x + (width+5)*pixelSize, margin.y + pixelSize*12, paintDigital);
        canvas.drawText("00",margin.x + (width+5)*pixelSize, margin.y + pixelSize*14, paintDigital);

        paintDigital.setColor(Preferences.CL_ENABLE);
        canvas.drawText("Hi-Score",margin.x + (width+5)*pixelSize, margin.y + pixelSize, paint);
        canvas.drawText(String.valueOf(Preferences.hiScore),margin.x + (width+5)*pixelSize, margin.y + pixelSize*2, paintDigital);
        canvas.drawText("Score",margin.x + (width+5)*pixelSize, margin.y + pixelSize*3, paint);
        canvas.drawText(String.valueOf(score),margin.x + (width+5)*pixelSize, margin.y + pixelSize*4, paintDigital);
        canvas.drawText("Level", margin.x + (width+5)*pixelSize, margin.y + pixelSize*11, paint);
        canvas.drawText(String.valueOf(level),margin.x + (width+5)*pixelSize, margin.y + pixelSize*12, paintDigital);
        canvas.drawText("Speed",margin.x + (width+5)*pixelSize, margin.y + pixelSize*13, paint);
        canvas.drawText(String.valueOf(speed),margin.x + (width+5)*pixelSize, margin.y + pixelSize*14, paintDigital);

        // draw pixels
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(pixelSize * 0.125f);
        for(int y=0; y<height-4; y++)
            for(int x=0; x<width; x++) {
                paint.setColor(pixels[x][y+4] ? Preferences.CL_ENABLE : Preferences.CL_DISABLE);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(margin.x + x*pixelSize + pixelSize*0.125f, margin.y + y*pixelSize + pixelSize*0.125f, margin.x + x*pixelSize + pixelSize*0.9f, margin.y + y*pixelSize + pixelSize*0.9f, paint);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(margin.x + x*pixelSize + pixelSize*0.35f, margin.y + y*pixelSize + pixelSize*0.35f, margin.x + x*pixelSize + pixelSize*0.68f, margin.y + y*pixelSize + pixelSize*0.68f, paint);
            }
        // draw next block area
        paint.setColor(Preferences.CL_DISABLE);
        for(int y=5; y<9; y++)
            for(int x=width+1; x<width+5; x++) {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(margin.x + x*pixelSize + pixelSize*0.125f, margin.y + y*pixelSize + pixelSize*0.125f, margin.x + x*pixelSize + pixelSize*0.9f, margin.y + y*pixelSize + pixelSize*0.9f, paint);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(margin.x + x*pixelSize + pixelSize*0.35f, margin.y + y*pixelSize + pixelSize*0.35f, margin.x + x*pixelSize + pixelSize*0.68f, margin.y + y*pixelSize + pixelSize*0.68f, paint);
            }
        shapeNext.draw(canvas, width, pixelSize, margin);
    }

    private void setShape(boolean value) {
        for(int y=0; y<shape.getHeight(); y++)
            for(int x=0; x<shape.getWidth(); x++) {
                if (shape.isPixelEmpty(x, y)) continue;
                if (shape.getX()+x < width && shape.getX()+x >= 0 && shape.getY()+y < height && shape.getY()+y >= 0)
                    pixels[shape.getX()+x][shape.getY()+y]=value;
            }
    }

    private boolean collision() {
        for(int y=0; y<shape.getHeight(); y++)
            for(int x=0; x<shape.getWidth(); x++) {
                if (shape.isPixelEmpty(x, y)) continue;
                if (shape.getX()+x < 0 || shape.getX()+x >= width || shape.getY()+y < 0 || shape.getY()+y >= height || pixels[shape.getX()+x][shape.getY()+y]) return true;
            }
        return false;
    }

    public boolean moveDown() {
        setShape(false);
        shape.moveDown();
        if (collision()) {
            shape.moveUp();
            setShape(true);
            return false;
        }
        setShape(true);
        return true;
    }

    private void clearRow(int row) {
        for(int x=0; x<width; x++) pixels[x][row]=false;
    }

    private void shiftRow(int row) {
        for(int x=0; x<width; x++)
            pixels[x][row] = pixels[x][row-1];
    }

    private void clearBrokenRows() {
        for(int row=0; row<height; row++) {
            if (isRowFull(row)) {
                clearRow(row);
                for(int y=row; y>0; y--) shiftRow(y);
                clearRow(0);
            }
        }
    }

    public void checkRow() {
        int brokenCount = 0;
        int level = this.level;
        int[] brokenRow = new int[4];

        for(int row=0; row<height; row++) { //detect full rows
            if (isRowFull(row)) {
                brokenRow[brokenCount] = row;
                brokenCount++;
            }
        }

        if (brokenCount>0) {
            SoundPlayer.playSound(context, R.raw.snd_break);
            breakRowAnimation(brokenRow, brokenCount);
            clearBrokenRows();
            score += (brokenCount * brokenCount);
            if (!Preferences.simpleShapes) score += (brokenCount*brokenCount);
            if (score>9999) score = 9999;
            if (!newRecord && score > Preferences.hiScore) {
                SoundPlayer.playSound(context, R.raw.snd_hiscore);
                newRecord = true;
            }
            if (score > Preferences.hiScore) Preferences.hiScore = score;

            broken += brokenCount;
            if (broken>120) broken=120;
            this.level = (score / 75) + 1;
            if (this.level != level) {            //check levelUp
                broken -= (brokenCount + 10);
                SoundPlayer.playSound(context, R.raw.snd_levelup);
            }
            speed = (broken / 10) + 1;
            if (speed<1) speed=1;
        } else SoundPlayer.playSound(context, R.raw.snd_land);

    }

    void breakRowAnimation(final int[] brokenY, final int brokenCount) {
        for (int z=0; z<6; z++) {
            for (int y = 0; y < brokenCount; y++)
                for (int x = 0; x < width; x++)
                    pixels[x][brokenY[y]] = !pixels[x][brokenY[y]]; //show-hide broken rows
            if (onUpdateListener != null) onUpdateListener.onUpdate();
            try {
                Thread.sleep(75);
            } catch (Exception exception) {
                Log.i("BrickMatrix", "breakRowAnimation: " + exception);
            }
        }
    }

    public boolean isGameOver() {
        for(int y=0; y<shape.getHeight(); y++)
            for(int x=0; x<shape.getWidth(); x++)
                if (!shape.isPixelEmpty(x, y) && shape.getY()+y<4) return true;
        return false;
    }

    public void rotateShape() {
        setShape(false);
        shape.rotate(Preferences.direction);
        if (collision()) shape.rotate(!Preferences.direction); else SoundPlayer.playSound(context, R.raw.snd_rotate);
        setShape(true);
    }

    void moveLeft() {
        setShape(false);
        shape.moveLeft();
        if (collision()) shape.moveRight();
        setShape(true);
    }

    void moveRight() {
        setShape(false);
        shape.moveRight();
        if (collision()) shape.moveLeft();
        setShape(true);
    }

    public int getSpeed() {
        return speed;
    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

}
