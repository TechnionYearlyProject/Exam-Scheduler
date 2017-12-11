package Screens.AddConstraint;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private JFXButton addButton;
    @FXML
    private JFXButton cancelButton;
    private List<String> courseNameList = new ArrayList<>();
    private String courseName;
    private String courseID;
    private Stage stage;

    public AddConstraintController(Stage stage){
        courseNameList.add("Algorithms");
        courseNameList.add("Yearly Project");
        this.stage = stage;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseNameList.forEach(name->courseNameChoice.getItems().add(name));
        cancelButton.setOnMouseClicked(e->onPrevButtonClick());
        addButton.setOnMouseClicked(e->onNextButtonClick());
    }

    private void onPrevButtonClick(){
        stage.close();
    }
    private void onNextButtonClick(){
        courseName = courseNameChoice.getValue();
        courseID = courseIDChoice.getText();
        if (courseName == null || courseName.equals("") || courseID == null || courseID.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "אנא בחר/י שם ומספר קורס", ButtonType.OK);
            alert.setHeaderText("");
            alert.setTitle("שגיאה");
            alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            alert.showAndWait();
        }
        else {
            stage.close();
        }
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseID() {
        return courseID;
    }
}

