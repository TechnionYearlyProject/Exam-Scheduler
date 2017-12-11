package Screens.Calendar;

import Screens.AddConstraint.AddConstraintController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Create an anchor pane that can store additional data.
 */
public class AnchorPaneNode extends AnchorPane {

    private LocalDate date;
    private VBox view;
    private void displayCourse(String courseName,String courseID){
        view = (VBox)this.getChildren().get(2);
        StackPane stack = new StackPane();
        Rectangle rect1 = new Rectangle(this.getLayoutX(), this.getLayoutY(), 171, 26);
        rect1.setStyle("-fx-fill: #0369cd; -fx-stroke: black");
        stack.setPrefSize(85,12.22);
        Text text = new Text(courseName + "-" + courseID);
        text.setStyle("-fx-fill: white");
        stack.getChildren().addAll(rect1,text);
        view.getChildren().add(stack);
    }
    AnchorPaneNode(Node... children) {
        super(children);
        this.setOnMouseClicked(e->{
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Screens/AddConstraint/addConstraint.fxml"));
            stage.setTitle("הוספת קורס");
            AddConstraintController controller = new AddConstraintController(stage);
            loader.setController(controller);
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            stage.showAndWait();
            String courseName = controller.getCourseName();
            String courseID = controller.getCourseID();
            if(courseName!=null && courseID!=null){
                displayCourse(courseName,courseID);
            }
        });
    }

    void setDate(LocalDate date) {
        this.date = date;
    }
}
