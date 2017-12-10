package GUI.Screens.Calendar;

import GUI.Screens.AddConstraint.AddConstraintController;
import javafx.fxml.FXMLLoader;
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

    // Date associated with this pane
    private LocalDate date;
    private VBox view = new VBox();
    /**
     * Create a anchor pane node. Date is not assigned in the constructor.
     * @param children children of the anchor pane
     */
    AnchorPaneNode(Node... children) {
        super(children);
/*        StackPane pane1 = new StackPane();
        pane1.setPrefSize(85,28.33);
        StackPane pane2 = new StackPane();
        pane2.setPrefSize(85,28.33);
        StackPane pane3 = new StackPane();
        pane3.setPrefSize(85,28.33);
        VBox vbox = new VBox(pane1,pane2,pane3);*/
        this.setOnMouseClicked(e->{
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Screens/AddConstraint/addConstraint.fxml"));
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
            String courseType = controller.getCourseType();

            view = (VBox)this.getChildren().get(1);
            view.setVisible(true);
            Rectangle rect1 = new Rectangle(this.getLayoutX(), this.getLayoutY(), 85, 28.33);
/*        this.getScene().getRoot().get*/
            rect1.setFill(Color.BLUE);
            rect1.setVisible(true);
            //StackPane stack = (StackPane)view.getChildren().get(0);
            StackPane stack = new StackPane();
            stack.setPrefSize(85,28.33);
            stack.getChildren().addAll(rect1,new Text(courseID));
            stack.setVisible(true);
            view.getChildren().add(stack);
            view.setVisible(true);
        });
    }
/*    public void addDates(LocalDate date){
        view.getChildren().forEach(e->((InnerNode)e).addDate(date));
    }
    private VBox getView(){
        return view;
    }*/
    private void addPreExam(){
/*        Button button = new Button("236363 ממן");
        view.getChildren().add(button);*/
        view = (VBox)this.getChildren().get(1);
        view.setVisible(true);
        StackPane stack = new StackPane();
        Rectangle rect1 = new Rectangle(this.getLayoutX(), this.getLayoutY(), 85, 28.33);
/*        this.getScene().getRoot().get*/
        rect1.setFill(Color.BLUE);
        rect1.setVisible(true);
        stack.getChildren().addAll(rect1,new Text("236363"));
        stack.setVisible(true);
        view.getChildren().addAll(null,stack);
        view.setVisible(true);
    }

    public LocalDate getDate() {
        return date;
    }

    void setDate(LocalDate date) {
        this.date = date;
    }
}
