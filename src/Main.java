
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import GUI.Components.*;
import javafx.scene.text.Font;


import java.time.LocalDate;
import java.util.Locale;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Super super1 = new Super();

        /*VBox vbox = new VBox();
        LocalDate start = LocalDate.of(2018, 01, 01);
        LocalDate end = LocalDate.of(2018, 03, 01);
        Schedule schedule = new Schedule(start,end);
        schedule.addTest(start,"קומבי");
        vbox.getChildren().addAll(new Picker("תאריך התחלה:"),schedule );*/
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-image: url(\"/GUI/resources/background.jpg\")");
        pane.getChildren().add(super1);
        Scene scene = new Scene(pane,1366,768);
        primaryStage.setTitle("Exam Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
