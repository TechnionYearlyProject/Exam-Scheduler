import db.Database;
import db.exception.InvalidDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import GUI.Screens.SemesterPicking.SemesterPickingController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("GUI/Screens/SemesterPicking/semesterPicking.fxml"));
        SemesterPickingController controller = new SemesterPickingController();
        controller.setPrevStage(primaryStage);
        myLoader.setController(controller);
        Parent root = myLoader.load();
        //Parent root = myLoader.load(getClass().getResource("Screens/SemesterPicking/semesterPicking.fxml"));
        root.getStylesheets().add(getClass().getResource("GUI/Screens/SemesterPicking/semesterPicking.css").toExternalForm());


        primaryStage.setTitle("בחירת סמסטר");
        Scene scene = new Scene(root,800,500);
        primaryStage.getIcons().add(new Image("GUI/resources/Technion-logo2.png"));
        SemesterPickingController control = myLoader.getController();
        control.setPrevStage(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        Database database = new Database();
        try {
            database.loadSemester(2017, "winter");
        } catch (InvalidDatabase e) {
            e.printStackTrace();
            return;
        }
        launch(args);
    }
}
