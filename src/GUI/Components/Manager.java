package GUI.Components;
import db.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;

public class Manager extends HBox {
    public Moed A;
    public Moed B;
    private CoursesTable courses;
    public Manager() {

        courses = new CoursesTable();
        A = new Moed(courses,"מועד א'");
        B = new Moed(courses,"מועד ב'");
        this.getChildren().addAll(B,A,courses);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
    }
    public void cleanData() {
        this.getChildren().remove(0);
        B = new Moed(courses,"מועד ב'");
        this.getChildren().add(0, B);
        this.getChildren().remove(1);
        A = new Moed(courses,"מועד א'");
        this.getChildren().add(1, A);
    }
}
