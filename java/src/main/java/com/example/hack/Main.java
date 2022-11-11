package com.example.hack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {
    public void start(Stage stage) throws IOException {
        File lockfile = new File(".base.lock");
        if (!lockfile.createNewFile()) {
            System.err.println("There is a lock on the database. Please try again.");
            //System.exit(0);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Qml Runtime");
        stage.show();
    }

    public void stop() {
        System.out.println("saving and closing");
        try {
            Controller.writeFile();
        } catch (FileNotFoundException error) {
            System.out.println("Error, unable to save");
        }
    }
}
