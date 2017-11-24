package src;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static src.SEMESTER.SEMESTER_A;
import static src.SEMESTER.SEMESTER_B;
import static src.SEMESTER.SEMESTER_K;

enum SEMESTER{ SEMESTER_A, SEMESTER_B, SEMESTER_K }
public class Controller implements Initializable {
    public VBox topBar;
    @FXML
    ChoiceBox<String> yearChoice;
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

    public void onNextButtonClick(){
        wantedYear = yearChoice.getValue();
        if (wantedYear == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please pick a year.", ButtonType.OK);
            alert.setHeaderText("Year");
            alert.showAndWait();
            return;
        }
        RadioButton pickedButton = (RadioButton)semesterToggleGroup.getSelectedToggle();
        if(pickedButton == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please pick a semester.", ButtonType.OK);
            alert.setHeaderText("Semester");
            alert.showAndWait();
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
