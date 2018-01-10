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
                "מערכות הפעלה-234123",
                "מתם-234122",
                "חישוביות-236343",
                "מבוא למדעי המחשב-234114",
                "פרויקט שנתי א'-234311");
        ListView<String> courseListView = new ListView<>(courseNames);
        VBox courseInterface = new VBox(courseListView);

        courseListView.setCellFactory(param -> new CourseCell());

        this.getChildren().addAll(B,A,courseInterface);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
    }
}
