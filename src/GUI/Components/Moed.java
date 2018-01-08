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
    Schedule schedule;
    Boolean start_set;
    Boolean end_set;
    public Moed(String title) {
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        start_set = false;
        end_set = false;
        Label label = new Label(title);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt; -fx-underline: true;");

        Picker picker1 = new Picker("תאריך התחלה:");
        Picker picker2 = new Picker("תאריך סיום:");
        if (title == "מועד א'")
            schedule = new Schedule(LocalDate.now(),LocalDate.now().plusDays(30));
        else
            schedule = new Schedule(LocalDate.now().plusDays(31),LocalDate.now().plusDays(61));

        this.getChildren().addAll(label,picker1,picker2,schedule);
        picker1.getPicker().setOnAction(event -> {
            start_set = true;
            picker1.setDate(picker1.getPicker().getValue());
            if (start_set && end_set) {
                if (!picker1.getPicker().getValue().isBefore(picker2.getPicker().getValue())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "תאריכים לא חוקיים", ButtonType.OK);
                    alert.showAndWait();
                }
                else {
                    this.getChildren().remove(3);
                    schedule = new Schedule(picker1.getDate(), picker2.getDate());
                    this.getChildren().add(schedule);
                }
            }
        });
        picker2.getPicker().setOnAction(event -> {
            end_set = true;
            picker2.setDate(picker2.getPicker().getValue());
            if (start_set && end_set) {
                if (!picker1.getPicker().getValue().isBefore(picker2.getPicker().getValue())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "תאריכים לא חוקיים", ButtonType.OK);
                    alert.showAndWait();
                }
                else {
                    this.getChildren().remove(3);
                    schedule = new Schedule(picker1.getDate(), picker2.getDate());
                    this.getChildren().add(schedule);
                }
            }
        });

    }
}
