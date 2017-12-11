package Screens.SemesterPicking;
import Screens.Calendar.CalendarController;
import Screens.Calendar.FullCalendarView;
import Screens.SEMESTER;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

import static Screens.SEMESTER.SEMESTER_A;
import static Screens.SEMESTER.SEMESTER_B;
import static Screens.SEMESTER.SEMESTER_K;


public class SemesterPickingController implements Initializable {

    private Stage prevStage;
    public VBox topBar;
    @FXML private JFXComboBox<String> yearChoice;
    @FXML private ToggleGroup semesterToggleGroup;
    @FXML private RadioButton radioButtonA;
    @FXML private RadioButton radioButtonB;
    @FXML private RadioButton radioButtonK;
    @FXML private JFXButton nextButton;
    @FXML private JFXButton prevButton;

    public void setPrevStage(Stage prevStage){
        this.prevStage = prevStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(Integer i=2010;i<2020;i++){
            yearChoice.getItems().add(i.toString());
        }
        nextButton.setOnAction(e->onNextButtonClick());
        prevButton.setOnAction(e->onPrevButtonClick());
    }
    private void createAlert(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        alert.setHeaderText("");
        alert.setTitle("שגיאה");
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
    private void onPrevButtonClick(){
        Stage stage = new Stage();
        stage.setTitle("מסך כניסה");
        stage.getIcons().add(new Image("resources/Technion-logo2.png"));

        Pane myPane = null;
        try {
            myPane = FXMLLoader.load(getClass().getResource("/Screens/Login/login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = null;
        if (myPane != null) {
            scene = new Scene(myPane);
        }
        prevStage.close();
        stage.setScene(scene);
        stage.show();
    }
    private void onNextButtonClick(){
        String wantedYear = yearChoice.getValue();
        if (wantedYear == null) {
            createAlert("אנא בחר/י שנת לימודים.");
            return;
        }
        RadioButton pickedButton = (RadioButton)semesterToggleGroup.getSelectedToggle();
        if(pickedButton == null){
            createAlert("אנא בחר/י סמסטר.");
            return;
        }
        SEMESTER wantedSemester;
        if(radioButtonA == pickedButton){
            wantedSemester = SEMESTER_A;
        }
        else if(radioButtonB == pickedButton){
            wantedSemester = SEMESTER_B;
        }
        else {
            wantedSemester = SEMESTER_K;
        }
        System.out.println("wantedYear is " + wantedYear);
        System.out.println("wantedSemester is " + wantedSemester);

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Screens/Calendar/fullCalendar.fxml"));
        stage.setTitle("לוח שנה");
        CalendarController calendarController = new CalendarController(wantedYear, wantedSemester);
        calendarController.setPrevStage(stage);
        loader.setController(calendarController);
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        calendarController.calendarPane.getChildren().add(new FullCalendarView(YearMonth.now()).getView());
        prevStage.close();
        stage.getIcons().add(new Image("resources/Technion-logo2.png"));
        stage.show();
    }
}
