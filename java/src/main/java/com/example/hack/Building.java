package com.example.hack;

import javafx.scene.image.ImageView;

public class Building extends ImageView {
    public final int index, x, y;
    public static final int[] widths = new int[] { 1, 1, 2, 5 }, heights = new int[] { 1, 2, 4, 4};
    public final int width, height;
    public Building(String url, int index, int x, int y) {
        super(url);
        this.index = index;
        this.x = x; this.y = y;
        width = widths[index]; height = heights[index];
    }
}
