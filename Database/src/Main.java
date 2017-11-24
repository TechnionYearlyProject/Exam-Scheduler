package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setTitle("בחירת סמסטר");
        Scene scene = new Scene(root,800,500);
        primaryStage.getIcons().add(new Image("resources/Technion-logo2.png"));
        //scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
