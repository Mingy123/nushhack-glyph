package com.example.hack;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class Building extends StackPane {
    public final int index, width, height, x, y;
    public int level;
    public static final int[] widths = new int[] { 1, 1, 2, 5 }, heights = new int[] { 1, 2, 4, 4};

    public final ImageView imageView;
    public final Button levelLabel;
    public static final Font font = new Font(20);
    private final Runnable refresh;

    public Building(String url, int index, int x, int y, int level, Runnable refresh) {
        getStyleClass().add("building");
        imageView = new ImageView(url);
        levelLabel = new Button(level+"");
        levelLabel.setFont(font);
        getChildren().addAll(imageView, levelLabel);

        levelLabel.setOnAction(this::levelup);

        this.index = index;
        this.level = level;
        this.x = x; this.y = y;
        this.refresh = refresh;
        width = widths[index]; height = heights[index];
    }

    private void levelup(ActionEvent actionEvent) {
        int cost = (int) (Controller.costs[index] * 0.5);
        if (Controller.deductCoin(cost)) {
            Controller.music("success.wav");
            levelLabel.setText(++level + "");
            refresh.run();
        } else Controller.music("invalid.wav");
    }
}
