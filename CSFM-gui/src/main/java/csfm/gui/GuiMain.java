package csfm.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class GuiMain extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        new FXMLLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(new File(Paths.get("").toAbsolutePath().toString() + File.separator + "main-view.fxml").toURI().toURL());
        Scene scene = new Scene(fxmlLoader.load(),1024,640);
        stage.setTitle("CSFM-Gui-1.2.0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}