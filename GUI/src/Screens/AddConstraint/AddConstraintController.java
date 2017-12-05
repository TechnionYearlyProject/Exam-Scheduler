package Screens.AddConstraint;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddConstraintController implements Initializable {

    @FXML
    private JFXComboBox<String> courseNameChoice;
    @FXML
    private JFXTextField courseIDChoice;
    @FXML
    private JFXComboBox<String> courseTypeChoice;
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton cancelButton;
    private List<String> courseNameList = new ArrayList<>();
    private String courseName;
    private String courseID;
    private String courseType;
    private Stage stage;
    public AddConstraintController(Stage stage){
        courseNameList.add("Algorithms");
        courseNameList.add("Yearly Project");
        this.stage = stage;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseNameList.forEach(name->courseNameChoice.getItems().add(name));
        courseTypeChoice.getItems().add(0,"מבחן בוקר 9:30");
        courseTypeChoice.getItems().add(1,"מבחן צהריים 13:00");
        courseTypeChoice.getItems().add(2,"מבחן ערב 17:30");
        cancelButton.setOnMouseClicked(e->onPrevButtonClick());
        addButton.setOnMouseClicked(e->onNextButtonClick());
    }

    private void onPrevButtonClick(){
        stage.close();
    }
    private void onNextButtonClick(){
        //add error msgs
        courseName = courseNameChoice.getValue();
        courseID = courseIDChoice.getText();
        courseType = courseTypeChoice.getValue();
        stage.close();
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getCourseType() {
        return courseType;
    }
}

