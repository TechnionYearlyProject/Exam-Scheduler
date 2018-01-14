package GUI.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Moed extends VBox{
    public Schedule schedule;
    Picker picker1;
    Picker picker2;
    Boolean start_set;
    Boolean end_set;
    public Moed(CoursesTable table, String title) {
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        start_set = false;
        end_set = false;
        Label label = new Label(title);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt; -fx-underline: true;");

        picker1 = new Picker("תאריך התחלה:");
        picker2 = new Picker("תאריך סיום:");
        if (title == "מועד א'")
            schedule = new Schedule(table,LocalDate.now(),LocalDate.now().plusDays(35));
        else
            schedule = new Schedule(table,LocalDate.now().plusDays(36),LocalDate.now().plusDays(72));

        this.getChildren().addAll(label,picker1,picker2,schedule);
        picker1.getPicker().setOnAction(event -> {
            start_set = true;
            picker1.setDate(picker1.getPicker().getValue());
            if (start_set && end_set) {
                if (!picker1.getPicker().getValue().isBefore(picker2.getPicker().getValue())) {
                    AlertBox alert = new AlertBox(AlertType.ERROR, "תאריכים לא חוקיים" + "\n" + "אנא הזינו שוב תאריכים", null);
                }
                else {
                    this.getChildren().remove(3);
                    schedule = new Schedule(table,picker1.getDate(), picker2.getDate());
                    this.getChildren().add(schedule);
                }
            }
        });
        picker2.getPicker().setOnAction(event -> {
            end_set = true;
            picker2.setDate(picker2.getPicker().getValue());
            if (start_set && end_set) {
                if (!picker1.getPicker().getValue().isBefore(picker2.getPicker().getValue())) {
                    AlertBox alert = new AlertBox(AlertType.ERROR, "תאריכים לא חוקיים" + "\n" + "אנא הזינו שוב תאריכים", null);
                }
                else {
                    this.getChildren().remove(3);
                    schedule = new Schedule(table,picker1.getDate(), picker2.getDate());
                    this.getChildren().add(schedule);
                }
            }
        });

    }
}
