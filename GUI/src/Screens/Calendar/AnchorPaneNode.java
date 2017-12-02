package Screens.Calendar;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;

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
        // Add action handler for mouse clicked
        this.setOnMouseClicked(e-> {
            System.out.println("This pane's date is: " + date);
            addPreExam();
        });
    }
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
        view.getChildren().addAll(stack);
        view.setVisible(true);
    }

    public LocalDate getDate() {
        return date;
    }

    void setDate(LocalDate date) {
        this.date = date;
    }
}
