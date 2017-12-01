package Screens.SemesterPicking;
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
import java.util.ResourceBundle;

import static Screens.SemesterPicking.SEMESTER.SEMESTER_A;
import static Screens.SemesterPicking.SEMESTER.SEMESTER_B;
import static Screens.SemesterPicking.SEMESTER.SEMESTER_K;

enum SEMESTER{ SEMESTER_A, SEMESTER_B, SEMESTER_K }
public class SemesterPickingController implements Initializable {

    public VBox topBar;
    @FXML
    JFXComboBox<String> yearChoice;
    @FXML
    ToggleGroup semesterToggleGroup;
    @FXML
    RadioButton radioButtonA;
    @FXML
    RadioButton radioButtonB;
    @FXML
    RadioButton radioButtonK;

    private String wantedYear;
    private SEMESTER wantedSemester;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(Integer i=2010;i<2020;i++){
            yearChoice.getItems().add(i.toString()); 
        }
    }
    private void createAlert(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        alert.setHeaderText("");
        alert.setTitle("שגיאה");
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
    public void onPrevButtonClick(){
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
        stage.setScene(scene);
        stage.show();
    }
    public void onNextButtonClick(){
        wantedYear = yearChoice.getValue();
        if (wantedYear == null) {
            createAlert("אנא בחר/י שנת לימודים.");
            return;
        }
        RadioButton pickedButton = (RadioButton)semesterToggleGroup.getSelectedToggle();
        if(pickedButton == null){
            createAlert("אנא בחר/י סמסטר.");
            return;
        }
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
    }
}
