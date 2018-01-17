package GUI.Components;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

public class AddSemester extends HBox{
	ChoiceBox<String> program;
    ChoiceBox<String> semester;
    CoursesTable coursestable;
	public AddSemester(CoursesTable parent) {
	    coursestable = parent;
	    program = new ChoiceBox<String>();
	    program.getItems().addAll(coursestable.manager.semester.getStudyProgramCollection());
	    program.setPrefWidth(370);
        program.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        program.setStyle("-fx-focus-color: transparent; -fx-background-color: -fx-control-inner-background");//", -fx-control-inner-background;");
        program.getStylesheets().add("/choicebox_style.css");
        semester = new ChoiceBox<String>();
        semester.getItems().addAll("1","2","3","4","5","6","7","8");
        semester.setPrefWidth(80);
        semester.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        semester.setStyle("-fx-focus-color: transparent; -fx-background-color: -fx-control-inner-background");//", -fx-control-inner-background;");
        semester.getStylesheets().add("/choicebox_style.css");
        this.setSpacing(10);
	    this.getChildren().addAll(semester,program);
	}
}
		
