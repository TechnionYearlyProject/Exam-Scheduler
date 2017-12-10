package GUI.Screens.Calendar;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class InnerNode extends StackPane {
    private LocalDate date;
    //private StackPane stack = new StackPane();

    public InnerNode(Node... children) {
        super(children);
        this.setPrefSize(85,28.33);
    }
    public void addRect(double x, double y){
        Rectangle rect1 = new Rectangle(x,y, 85, 28.33);
/*        this.getScene().getRoot().get*/
        rect1.setFill(Color.BLUE);
        rect1.setVisible(true);
        this.getChildren().addAll(rect1,new Text("236363"));
        this.setVisible(true);
    }
    private void addDate(LocalDate date){
        this.date = date;
        this.setOnMouseClicked(e->
            System.out.println("the date is: " + date));
        this.setVisible(true);
    }
}
