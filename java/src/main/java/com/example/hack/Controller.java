package com.example.hack;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Controller {
    static int coins, buyIndex, selGridX, selGridY, battleship_total, battleship, population;
    public static final int[] costs = new int[]{50, 200, 1500, 6900},
                              ppl = new int[]{4, 20, 250, 1500};
    private static final ArrayList<Building> items = new ArrayList<>();
    public GridPane grid;
    public Pane stack;
    public Label status;
    public Button coinButton;
    public final static String[] images = new String[]{ "shack.png", "coffee.png", "pizza.png", "house.png" };
    private final Rectangle highlighter = new Rectangle();

    public void initialize() throws FileNotFoundException {
        grid.getStyleClass().add("gridpane");
        stack.getChildren().add(highlighter);
        Scanner sc = new Scanner(new File("base.dat"));
        coins = sc.nextInt(); sc.nextLine();
        for (int i = 0; i < 4; i++) {
            Scanner line = new Scanner(sc.nextLine());
            while (line.hasNextInt())
                addItem(i, line.nextInt(), line.nextInt(), line.nextInt());
        }

        if (coins < 0) battleship_setup();
        else normal_setup();
    }

    private void normal_setup() {
        refreshPopulation(); refreshCoin();
        ToggleButton.setOnDeselect(() -> highlighter.setVisible(false));
        ToggleButton.setOnChange(() -> highlighter.setVisible(false));
        grid.setOnMouseMoved(this::highlight);
        highlighter.setOnMouseMoved(this::highlight);
        highlighter.setOnMouseClicked(mev -> {
            Paint fill = highlighter.getFill();
            if (Color.web("rgba(0,255,0,0.5)").equals(fill)) {
                music("success.wav");
                addItem(buyIndex, selGridX, selGridY, 1);
                coins -= costs[buyIndex];
                refreshCoin();
            } else if (Color.RED.equals(fill)) {
                Building remove = cellOccupied(selGridX, selGridY);
                if (remove == null) return;
                items.remove(remove);
                items.remove(remove);
                grid.getChildren().remove(remove);
            } else music("invalid.wav");
        });
    }

    private void battleship_setup() {
        battleship_total = coins / -500;
        highlighter.setFill(Color.RED); highlighter.setVisible(false);
        status.setText(String.format("Debt collection! Battleship round %d/%d (click grid to continue)", battleship, battleship_total));
        grid.setOnMouseClicked(mev -> {
            if (battleship++ >= battleship_total) {
                highlighter.setVisible(false);
                coins = 0;
                normal_setup();
                return;
            }
            coins += 500;
            System.out.println("e");
            highlighter.setVisible(true);
            status.setText(String.format("Debt collection! Battleship round %d/%d", battleship, battleship_total));
            Random rd = new Random();
            int x = rd.nextInt(10), y = rd.nextInt(10);
            Building remove = cellOccupied(x, y);
            if (remove != null) {
                music("explode.wav");
                highlighter.setX(remove.x * 60);
                highlighter.setY(remove.y * 60);
                highlighter.setWidth(remove.width * 60);
                highlighter.setHeight(remove.height * 60);
                items.remove(remove);
                grid.getChildren().remove(remove);
            } else {
                highlighter.setX(x * 60); highlighter.setY(y * 60);
                highlighter.setWidth(60); highlighter.setHeight(60);
            }
        });
    }

    public void highlight(MouseEvent mev) {
        if (ToggleButton.selected == null) return;
        String text = ToggleButton.selected.getText();
        selGridX = (int) (mev.getX() / 60);
        selGridY = (int) (mev.getY() / 60);
        if (selGridX == 10 || selGridY == 10) return;
        if (text.startsWith("Shack")) buyIndex = 0;
        if (text.startsWith("Coffee")) buyIndex = 1;
        if (text.startsWith("Pizza")) buyIndex = 2;
        if (text.startsWith("House")) buyIndex = 3;
        if (text.equals("Remove")) {
            buyIndex = -1;
            highlighter.setFill(Color.RED);
            Building remove = cellOccupied(selGridX, selGridY);
            if (remove == null) highlighter.setVisible(false);
            else {
                highlighter.setVisible(true);
                highlighter.setX(remove.x * 60);
                highlighter.setY(remove.y * 60);
                highlighter.setWidth(remove.width * 60);
                highlighter.setHeight(remove.height * 60);
                refreshPopulation();
            } return;
        }
        int width = Building.widths[buyIndex];
        int height = Building.heights[buyIndex];

        highlighter.setVisible(true);
        if (selGridX + width > 9) selGridX = 10 - width;
        if (selGridY + height > 9) selGridY = 10 - height;
        highlighter.setX(60 * selGridX); highlighter.setY(60 * selGridY);
        highlighter.setWidth(width * 60); highlighter.setHeight(height * 60);
        if (valid(selGridX, selGridY, width, height)) highlighter.setFill(Color.web("rgba(0,255,0,0.5)"));
        else highlighter.setFill(Color.web("rgba(255,0,0,0.5)"));
    }

    public void addItem(int index, int x, int y, int level) {
        Building item = new Building(getClass().getResource(images[index]).toString(),
                index, x, y, level, () -> { refreshCoin(); refreshPopulation(); });
        GridPane.setColumnSpan(item, item.width);
        GridPane.setRowSpan(item, item.height);
        item.imageView.setFitWidth(60 * item.width);
        item.imageView.setFitHeight(60 * item.height);
        grid.add(item, x, y);
        items.add(item);
        refreshPopulation();
    }

    void refreshPopulation() {
        population = 0;
        for (Building item : items) population += ppl[item.index] * (0.5 + item.level * 0.5);
        status.setText(String.format("You currently own %d people", population));
    } void refreshCoin() {
        coinButton.setText("Coins: " + coins);
    }

    boolean valid(int x, int y, int width, int height) {
        if (coins - costs[buyIndex] < 0) return false;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (cellOccupied(i+x, j+y) != null) return false;
            }
        } return true;
    }
    Building cellOccupied(int x, int y) {
        for (Building item : items) {
            if (x >= item.x && x < item.width + item.x &&
                y >= item.y && y < item.height + item.y)
                return item;
        } return null;
    }

    static void writeFile() throws FileNotFoundException {
        File outfile = new File("base.dat");
        PrintWriter pw = new PrintWriter(outfile);
        pw.println(coins);
        for (int i = 0; i < 4; i++) {
            for (Building item : items) {
                if (item.index == i) pw.printf("%d %d %d ", item.x, item.y, item.level);
            } pw.println();
        } pw.close();
    }

    public static void music(String url) {
        new MediaPlayer(new Media(Controller.class.getResource(url).toString())).play();
    }

    public static boolean deductCoin(int amt) {
        if (amt > coins) return false;
        coins -= amt;
        return true;
    }
}