package com.eaydin79.brick;

public class ShapeFactory {
    
    private static final boolean[][][] shapes = {
            // simple shapes
            {{true, true, true, true}, {false, false, false, false}, {false, false, false, false}, {false, false, false, false}},   // I
            {{true, false, false, false}, {true, true, false, false}, {false, true, false, false}, {false, false, false, false}},   // Z
            {{false, true, false, false}, {true, true, false, false}, {true, false, false, false}, {false, false, false, false}},   // S
            {{true, true, false, false}, {true, true, false, false}, {false, false, false, false}, {false, false, false, false}},   // ▄
            {{true, true, true, false}, {false, false, true, false}, {false, false, false, false}, {false, false, false, false}},   // L
            {{false, false, true, false}, {true, true, true, false}, {false, false, false, false}, {false, false, false, false}},   // J
            {{true, false, false, false}, {true, true, false, false}, {true, false, false, false}, {false, false, false, false}},   // ┬
            // complex shapes
            {{false, true, false, false}, {true, true, true, true}, {false, false, false, false}, {false, false, false, false}},    // 1
            {{true, false, false, false}, {true, true, true, false}, {false, false, true, false}, {false, false, false, false}},    // 2
            {{false, false, true, false}, {true, true, true, false}, {true, false, false, false}, {false, false, false, false}},    // 5
            {{false, true, false, false}, {true, true, true, false}, {false, true, false, false}, {false, false, false, false}},    // +
            {{true, true, false, false}, {false, true, false, false}, {false, false, false, false}, {false, false, false, false}},  // └
            {{true, true, false, false}, {false, true, false, false}, {true, true, false, false}, {false, false, false, false}},    // u
            {{true, false, false, false}, {true, true, true, false}, {true, false, false, false}, {false, false, false, false}},    // T
            {{true, false, false, false}, {true, false, false, false}, {false, false, false, false}, {false, false, false, false}}, // .
            {{true, true, true, true}, {false, true, false, false}, {false, false, false, false}, {false, false, false, false}}     // mirrored 1
    };

    private static final int[][] sizes = {
        //simpleBlocks
            {2, 4}, // I
            {3, 2}, // Z
            {3, 2}, // S
            {2, 2}, // O
            {2, 3}, // L
            {2, 3}, // J
            {3, 2}, // T
        //complexBlocks
            {2, 4}, // 1
            {3, 3}, // 2
            {3, 3}, // 5
            {3, 3}, // +
            {2, 2}, // L
            {3, 2}, // u
            {3, 3}, // T
            {1, 1}, // .
            {2, 4}  // mirrored 1
    };

    public static boolean[][] getPixels(int shapeType) {
        boolean[][] pixels = new boolean[4][4];
        for (int i=0; i<4; i++)
            System.arraycopy(shapes[shapeType][i], 0, pixels[i], 0, 4);
        return pixels;
    }

    public static int getWidth(int shapeType) {
        return sizes[shapeType][0];
    }

    public static int getHeight(int shapeType) {
        return sizes[shapeType][1];
    }

}
