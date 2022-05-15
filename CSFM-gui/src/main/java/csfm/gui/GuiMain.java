package csfm.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class GuiMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),640,640);
        stage.setTitle("CSFM-Gui-1.2.0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}