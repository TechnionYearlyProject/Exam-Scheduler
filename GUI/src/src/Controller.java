package src;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import static src.SEMESTER.SEMESTER_A;
import static src.SEMESTER.SEMESTER_B;
import static src.SEMESTER.SEMESTER_K;

enum SEMESTER{ SEMESTER_A, SEMESTER_B, SEMESTER_K }
public class Controller implements Initializable {
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

    private void createAlert(String text, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        alert.setHeaderText("");
        alert.setTitle(title);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
    public void onNextButtonClick(){
        wantedYear = yearChoice.getValue();
        if (wantedYear == null) {
            createAlert("אנא בחר/י שנת לימודים.","שגיאה");
            return;
        }
        RadioButton pickedButton = (RadioButton)semesterToggleGroup.getSelectedToggle();
        if(pickedButton == null){
            createAlert("אנא בחר/י סמסטר.","שגיאה");
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
