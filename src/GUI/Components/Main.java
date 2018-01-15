
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.scene.Scene;
import GUI.Components.*;

public class Main extends Application {

    public static void main(String[] args) { launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene main_window = new Scene(new Wrapper());
        main_window.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        });
        primaryStage.setTitle("Exam Scheduler");
        primaryStage.setScene(main_window);
        primaryStage.getIcons().add(new Image("/app_icon.png"));
        //primaryStage.setResizable(false);
        primaryStage.show();

    }


}
