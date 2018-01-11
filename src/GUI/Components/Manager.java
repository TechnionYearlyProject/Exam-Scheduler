package GUI.Components;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;

public class Manager extends HBox {
    Moed A;
    Moed B;
    public Manager() {
        A = new Moed("מועד א'");
        B = new Moed("מועד ב'");
        ObservableList<String> courseNames = FXCollections.observableArrayList(
                "קומבי-234141",
                "חישוביות-236343",
                "חשבון אינפיניטסימלי 1מ'-104031",
                "כימיה כללית-125001",
                "הסתברות מ-094412",
                "ביולוגיה 1-134058");
        ListView<String> courseListView = new ListView<>(courseNames);
        VBox courseInterface = new VBox(courseListView);

        courseListView.setCellFactory(param -> new CourseCell());
        courseListView.setPrefWidth(395);

        this.getChildren().addAll(B,A,courseInterface);
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
