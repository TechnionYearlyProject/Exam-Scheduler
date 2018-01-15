package GUI.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

public class Wrapper extends ScrollPane {
    Toolbar toolbar;
    Manager manager;
    public Wrapper() {
        manager = new Manager();
        toolbar = new Toolbar(this);
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-image: url(\"/background.png\");");
        vbox.getChildren().addAll(toolbar, manager);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));
        this.setContent(vbox);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }
    public void cleanData() {
        manager.cleanData();
    }
    public void updateSchdule(Logic.Schedule scheduleA, Logic.Schedule scheduleB) {
            manager.A.schedule.updateSchedule(scheduleA);
            manager.B.schedule.updateSchedule(scheduleB);
        }
}
/*
    public void saveAllData() {
        manager.A.schedule.

    }

}*/

