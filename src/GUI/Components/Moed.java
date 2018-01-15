package GUI.Components;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class Moed extends VBox{
    public Schedule schedule;
    Picker picker1;
    Picker picker2;
    Boolean start_set;
    Boolean end_set;
    public Moed(String title,LocalDate start, LocalDate end) {
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        start_set = false;
        end_set = false;
        Label label = new Label(title);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt; -fx-underline: true;");
        picker1 = new Picker("תאריך התחלה:");
        picker1.getPicker().setValue(start);
        picker2 = new Picker("תאריך סיום:");
        picker2.getPicker().setValue(end);
        schedule = new Schedule(start,end);
        this.getChildren().addAll(label,picker1,picker2,schedule);
    }

}
