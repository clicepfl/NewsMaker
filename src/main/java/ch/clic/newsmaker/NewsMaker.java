package ch.clic.newsmaker;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NewsMaker extends Application {

    private static final String STYLE_PATH = "/style/stylesheet.css";
    private static final int STAGE_WIDTH = 1500;
    private static final int STAGE_HEIGHT = 800;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), STAGE_WIDTH, STAGE_HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLE_PATH)).toExternalForm());
        stage.setTitle("NewsMaker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}