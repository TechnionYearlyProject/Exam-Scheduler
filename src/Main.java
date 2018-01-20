import GUI.Components.AlertType;
import GUI.Components.Wrapper;
import GUI.Components.AlertBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Scene main_window = new Scene(new Wrapper());
            main_window.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            });
            primaryStage.setTitle("Exam Scheduler");
            primaryStage.setScene(main_window);
            primaryStage.getIcons().add(new Image("/app_icon.png"));
            primaryStage.setResizable(false);
            primaryStage.setMaxHeight(927);
            primaryStage.setMinHeight(927);
            primaryStage.setMaxWidth(1691);
            primaryStage.setMinWidth(1691);
            primaryStage.show();
        } catch (Exception e) {
            new AlertBox(AlertType.ERROR,"התוכנית נתקלה בשגיאה ולכן תיסגר.",null,true);
            System.exit(1);
        }
    }
}
