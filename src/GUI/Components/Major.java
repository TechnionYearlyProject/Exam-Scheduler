package GUI.Components;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;

public class Major extends HBox {
    Moed A;
    Moed B;
    Rectangle courses;
    public Major() {
        A = new Moed("מועד א'");
        B = new Moed("מועד ב'");
        courses = new Rectangle(400,600);
        this.getChildren().addAll(B,A,courses);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
    }
}
