package GUI.Components;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;

public class Manager extends HBox {
    private Moed A;
    private Moed B;
    public Manager() {
        A = new Moed("מועד א'");
        B = new Moed("מועד ב'");
        ObservableList<String> courseNames = FXCollections.observableArrayList(
                "קומבינטוריקה למדעי המחשב-234141",
                "מערכות הפעלה-234123",
                "מבוא לתכנות מערכות-234122",
                "מבוא לחישוביות-236343",
                "מבוא למדעי המחשב-234114",
                "פרויקט שנתי א'-234311",
                "מערכות ספרתיות-044145"   ,
                "ארגון ותכנות המחשב-234118",
                "הסתברות מ'-094412",
                "אלגברה מודרנית ח'-104034",
                "מבני נתונים 1-234218",
                "תכן לוגי-234262",
                "לוגיקה ותורת הקבוצות-234293",
                "אלגוריתמים 1-234247",
                "מבנה מחשבים ספרתיים-234267",
                "אוטומטים ושפות פורמליות-236353",
                "אלגוריתמים נומריים-234125",
                "תורת הקומפילציה-236360",
                "חשבון אינפיניטסימלי 2מ'-104032");
        ListView<String> courseListView = new ListView<>(courseNames);
        Region r = new Region();
        r.setMinHeight(150);
        VBox courseInterface = new VBox(r, courseListView);

        courseListView.setCellFactory(param -> new CourseCell());

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


