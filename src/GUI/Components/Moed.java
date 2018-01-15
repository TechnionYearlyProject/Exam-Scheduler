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
    Manager manager;
    public Moed(Manager parent, String title,LocalDate start, LocalDate end) {
        manager = parent;
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
        schedule = new Schedule(this,start,end);
        this.getChildren().addAll(label,picker1,picker2,schedule);
    }

    public void cleanData(LocalDate start, LocalDate end) {
        picker1.getPicker().setValue(start);
        picker2.getPicker().setValue(end);
        this.getChildren().remove(3);
        schedule = new Schedule(this,start,end);
        this.getChildren().add(schedule);

    }

}
