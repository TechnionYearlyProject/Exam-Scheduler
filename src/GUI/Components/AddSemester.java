package GUI.Components;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

public class AddSemester extends HBox{
	ChoiceBox<String> program;
    ChoiceBox<String> semester;
    CoursesTable coursestable;
	public AddSemester(CoursesTable parent) {
	    coursestable = parent;
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("מסלול פקולטי");
        temp.addAll(coursestable.manager.semester.getStudyProgramCollection());
	    program = new ChoiceBox<String>();
	    program.getItems().addAll(temp);
	    program.setPrefWidth(350);
        program.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        program.setStyle("-fx-focus-color: transparent; -fx-background-color: -fx-control-inner-background");//", -fx-control-inner-background;");
        program.getStylesheets().add("/choicebox_style.css");
        program.setValue("מסלול פקולטי");
        semester = new ChoiceBox<String>();
        semester.getItems().addAll("סמסטר","1","2","3","4","5","6","7","8");
        semester.setPrefWidth(100);
        semester.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        semester.setStyle("-fx-focus-color: transparent; -fx-background-color: -fx-control-inner-background");//", -fx-control-inner-background;");
        semester.getStylesheets().add("/choicebox_style.css");
        semester.setValue("סמסטר");
        this.setSpacing(10);
	    this.getChildren().addAll(semester,program);
	}
}
		
