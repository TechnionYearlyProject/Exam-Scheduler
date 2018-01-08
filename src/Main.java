
import GUI.Components.Dialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import GUI.Components.*;
import javafx.scene.text.Font;


import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Super super1 = new Super();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-image: url(\"/GUI/resources/background2.png\")");
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.getChildren().add(super1);
        ScrollPane pane = new ScrollPane();
        pane.setContent(vbox);
        Scene scene = new Scene(pane);
        primaryStage.setTitle("Exam Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
        Dialog dlg = new Dialog();

        System.out.println(pane.getHeight());
        System.out.println(pane.getWidth());
    }


}
