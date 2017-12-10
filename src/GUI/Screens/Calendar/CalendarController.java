package GUI.Screens.Calendar;

import GUI.Screens.SEMESTER;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CalendarController {

    // Get the pane to put the calendar on
    @FXML public Pane calendarPane;
    private Stage prevStage;
    private Scene currScene;
    private String wantedYear;
    private SEMESTER wantedSemester;
    public void setPrevStage(Stage prevStage){
        this.prevStage = prevStage;
    }
    public CalendarController(String wanterYear, SEMESTER wantedSemester){
        this.wantedSemester = wantedSemester;
        this.wantedYear = wanterYear;
    }

    public void setCurrScene(Scene currScene) {
        this.currScene = currScene;
    }
}
