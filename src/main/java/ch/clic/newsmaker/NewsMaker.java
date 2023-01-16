package ch.clic.newsmaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NewsMaker extends Application {

    private static final String STYLE_PATH = "/style/stylesheet.css";
    private static final int STAGE_WIDTH = 1500;
    private static final int STAGE_HEIGHT = 800;
    private MainController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), STAGE_WIDTH, STAGE_HEIGHT);
        controller = fxmlLoader.getController();

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLE_PATH)).toExternalForm());
        stage.setTitle("NewsMaker");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws IOException {

        if (controller.hasNotChanged()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "You file is not saved. Do you want to save it?",
                ButtonType.YES, ButtonType.NO);

        alert.setTitle("File not saved");
        alert.showAndWait();

        if (alert.getResult() != ButtonType.YES) return;
        controller.save();
    }

    public static void main(String[] args) {
        launch();
    }
}