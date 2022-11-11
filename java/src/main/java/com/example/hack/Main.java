package com.example.hack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {
    File lockfile = new File(".base.lock");
    public void start(Stage stage) throws IOException {
        if (!lockfile.createNewFile()) {
            System.err.println("There is a lock on the database. Please try again.");
            //System.exit(0);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Clash of Forest");
        stage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
        stage.show();
    }

    public static void main (String[] args) {
        launch(args);
    }

    public void stop() {
        System.out.println("saving and closing");
        lockfile.delete();
        try {
            Controller.writeFile();
        } catch (FileNotFoundException error) {
            System.out.println("Error, unable to save");
        }
    }
}
