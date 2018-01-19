package GUI.Components;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

/**
 * @author dorbartov
 * @date 03/01/2018
 * This class creates an instance of a single moed schedule, including the table of days and the date pickers.
 */
public class Moed extends VBox{
    public Schedule schedule;
    Picker picker1;
    Picker picker2;
    Boolean start_set;
    Boolean end_set;
    Manager manager;
    enum MoedType {
        A,B
    }
    MoedType moedType;

    /**
     * @author dorbartov
     * @date 03/01/2018
     * @param parent used to access the manager and so the entire system.
     * @param title name of current moed
     * @param start start date of moed
     * @param end end date of moed
     */
    public Moed(Manager parent, String title,LocalDate start, LocalDate end) {
        if (title == "מועד א'")
            moedType = MoedType.A;
        else
            moedType = MoedType.B;
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
