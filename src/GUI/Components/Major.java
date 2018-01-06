package GUI.Components;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.util.List;

public class Major extends HBox {
    Moed A;
    Moed B;
    ListView<String> chosenCoursesView;
    ListView<String> coursesView;
    ObservableList<String> courseNames;
    ObservableList<String> chosenCourseNames;
    private void addCourse(){
        ObservableList<String> chosenCoursesToAdd;
        chosenCoursesToAdd = coursesView.getSelectionModel().getSelectedItems();
        chosenCourseNames.addAll(chosenCoursesToAdd);
    }
    public Major() {
        A = new Moed("מועד א'");
        B = new Moed("מועד ב'");
        chosenCourseNames = FXCollections.observableArrayList();
        chosenCoursesView = new ListView<>(chosenCourseNames);
        chosenCoursesView.setMaxHeight(150);
        courseNames = FXCollections.observableArrayList(
                "קומבי", "מערכות הפעלה", "מתם", "ממשקי אדם-מחשב", "מבוא למדעי המחשב", "פרויקט שנתי א'");
        coursesView = new ListView<>(courseNames);
        Text courseTitle = new Text("הוספת קורסים");

        Button addCourse = new Button("הוספה");
        addCourse.setOnMouseClicked(e->addCourse());
        HBox courseControls = new HBox(addCourse);

        VBox courseInterface = new VBox(chosenCoursesView,courseTitle,courseControls,coursesView);
        this.getChildren().addAll(B,A,courseInterface);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
    }
}
