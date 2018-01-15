package GUI.Components;
import Logic.CourseLoader;
import db.ConstraintList;
import db.Database;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class Manager extends HBox {
    Moed A;
    Moed B;
    CoursesTable coursetable;
    CourseLoader courseloader;
    Database db;
    ConstraintList constraintlistA;
    ConstraintList constraintlistB;
    public Manager() {
        try {
            db = new Database();
            courseloader = new CourseLoader(db.loadSemester(2017, "winter_test"),null);
            constraintlistA = new ConstraintList();
            constraintlistB = new ConstraintList();
        }
        catch (Exception e) {
            //handle exceptions
        }
        coursetable = new CoursesTable();
        A = new Moed("מועד א'");
        B = new Moed("מועד ב'");
        this.getChildren().addAll(B,A,coursetable);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
    }

    public void cleanData() {
        this.getChildren().remove(0);
        B = new Moed("מועד ב'");
        this.getChildren().add(0, B);
        this.getChildren().remove(1);
        A = new Moed("מועד א'");
        this.getChildren().add(1, A);
    }
}
