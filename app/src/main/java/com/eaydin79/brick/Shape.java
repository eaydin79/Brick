package com.eaydin79.brick;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class Shape {

    public static final boolean TO_LEFT = true;
    private boolean[][] pixels;
    private int width = 2;
    private int height = 4;
    private int posX = 0;
    private int posY = 0;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    public Shape() {
        pixels = ShapeFactory.getPixels(0);
    }

    public Shape(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = ShapeFactory.getPixels(0);
    }

    public void load(String stringVariables) {
        String[] variables = stringVariables.split(",");
        try {
            width = Integer.parseInt(variables[0]);
            height = Integer.parseInt(variables[1]);
            posX = Integer.parseInt(variables[2]);
            posY = Integer.parseInt(variables[3]);
        } catch (NumberFormatException e) {
            Log.e("Shape", "load: " + e);
        }
        int index = 4;
        for(int y=0; y<4; y++)
            for(int x=0; x<4; x++)
                pixels[x][y] = Boolean.parseBoolean(variables[index++]);
    }

    public String toStr() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(width).append(",").append(height).append(",").append(posX).append(",").append(posY);
        for(int y=0; y<4; y++)
            for(int x=0; x<4; x++)
                stringBuilder.append(",").append(pixels[x][y]);
        return stringBuilder.toString();
    }

    public void rotate(boolean direction){
        //noinspection SuspiciousNameCombination
        Shape shape = new Shape(height, width);

        if (direction == TO_LEFT) {
            for (int x = 0; x < height; x++)
                for (int y = 0; y < width; y++)
                    shape.pixels[x][y] = pixels[width - y - 1][x];
        } else {
            for (int x = 0; x < height; x++)
                for (int y = 0; y < width; y++)
                    shape.pixels[x][y] = pixels[y][height - x - 1];
        }

        width = shape.width;
        height = shape.height;
        pixels = shape.pixels;
    }

    public void moveUp() {
        posY--;
    }

    public void moveDown() {
        posY++;
    }

    public void moveLeft() {
        posX--;
    }

    public void moveRight() {
        posX++;
    }

    public void draw(Canvas canvas, int matrixWidth, int matrixPixelSize, Point margin) {
        paint.setColor(Preferences.CL_ENABLE);
        paint.setStrokeWidth(matrixPixelSize * 0.125f);
        int centerX = (4-width)/2;
        int centerY = (4-height)/2;
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                if (pixels[x][y]) {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(
                            margin.x + (x+matrixWidth+1+centerX) * matrixPixelSize + matrixPixelSize*0.125f,
                            margin.y + (y + 5 + centerY) * matrixPixelSize + matrixPixelSize*0.125f,
                            margin.x + (x+matrixWidth+1+centerX) * matrixPixelSize + matrixPixelSize*0.9f,
                            margin.y + (y + 5 + centerY) * matrixPixelSize + matrixPixelSize*0.9f, paint);
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawRect(
                            margin.x + (x+matrixWidth+1+centerX) * matrixPixelSize + matrixPixelSize*0.35f,
                            margin.y + (y + 5 + centerY) * matrixPixelSize + matrixPixelSize*0.35f,
                            margin.x + (x+matrixWidth+1+centerX) * matrixPixelSize + matrixPixelSize*0.68f,
                            margin.y + (y + 5 + centerY) * matrixPixelSize + matrixPixelSize*0.68f, paint);
                }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isPixelEmpty(int x, int y) {
        return !pixels[x][y];
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public void setXY(int x, int y) {
        posX = x;
        posY = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPixels(boolean[][] pixels) {
        this.pixels = pixels;
    }

    public void copyFrom(Shape shape) {
        width = shape.width;
        height = shape.height;
        for (int i=0; i<4; i++)
            System.arraycopy(shape.pixels[i], 0, pixels[i], 0, 4);
    }

}
