package GUI.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

/**
 * @author Tal
 * @date 5/12/2017
 * Class that wraps all the GUI components to a single pane
 */



public class Wrapper extends ScrollPane {
    Toolbar toolbar;
    Manager manager;
    public Wrapper() {
        manager = new Manager(this);
        toolbar = new Toolbar(this);
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-image: url(\"/background.png\");");
        vbox.getChildren().addAll(toolbar, manager);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));
        this.setStyle("-fx-focus-color: lightgrey;");//"-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");
        this.setContent(vbox);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }
    public void updateSchdule(Logic.Schedule scheduleA, Logic.Schedule scheduleB) {
            manager.A.schedule.updateSchedule(scheduleA,manager.courseloader);
            manager.B.schedule.updateSchedule(scheduleB,manager.courseloader);
        }
}
