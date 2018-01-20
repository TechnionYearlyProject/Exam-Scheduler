package GUI.Components;
import Logic.Course;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;

/**
 * @author dorbartov
 * @date 16/01/2018
 * The class displays a custom window in which the user can create and insert a new course to the system.
 */
public class AddCourse {
	private ImageView X_icon;
	private ImageView X_hover_icon;
	CoursesTable coursestable;
    TextField course_id;
    TextField course_name;
    TextField weight;
    AddSemester add_semester1;
    AddSemester add_semester2;
    AddSemester add_semester3;
    AddSemester add_semester4;
    Label label;

    /**
     * @author dorbartov
     * @date 16/01/2018
     * @param parent used to connect the window to the CoursesTable and so the entire system.
     */
	public AddCourse(CoursesTable parent) {
	    coursestable = parent;
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		HBox hbox_title = new HBox();
		hbox_title.setPadding(new Insets(15, 15, 0, 15)); //0 on bottom
		String temp = "הוספת קורס למסד הנתונים";
		stage.getIcons().add(new Image("/app_icon.png"));
		Label title_label = new Label(temp);
		title_label.setFont(Font.font(18));
		title_label.setAlignment(Pos.CENTER_RIGHT);
		title_label.setMinWidth(440);
		title_label.setStyle("-fx-font-weight: bold;");
		Label close_label = new Label();
		close_label.setPrefWidth(30);
		close_label.setPrefHeight(30);
		X_icon = new ImageView(new Image("/X_icon.png"));
		X_hover_icon = new ImageView(new Image("/X_hover_icon.png"));
		close_label.setGraphic(X_icon);
		close_label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				if (mouse_event.getButton()!= MouseButton.PRIMARY)
					return;
				stage.close();
			}
		});
		close_label.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				close_label.setGraphic(X_hover_icon);
			}
		});
		close_label.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				close_label.setGraphic(X_icon);

			}
		});
		hbox_title.getChildren().addAll(close_label, title_label);
		VBox body = new VBox();
		body.setPadding(new Insets(20, 20, 0, 20)); //0 on bottom
		body.setAlignment(Pos.CENTER_RIGHT);
		body.setSpacing(10);
        course_id = new TextField();
        course_id.setFocusTraversable(false);
        course_id.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        course_id.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        course_id.setPromptText("מספר הקורס");
        course_name = new TextField();
        course_name.setFocusTraversable(false);
        course_name.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        course_name.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        course_name.setPromptText("שם הקורס");
        weight = new TextField();
        weight.setFocusTraversable(false);
        weight.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        weight.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        weight.setPromptText("נקודות זכות");

        label = new Label("חלק מהשדות אינם מלאים כדרוש.");
        label.setAlignment(Pos.CENTER_RIGHT);
        label.setStyle("-fx-text-fill: white");

        add_semester1 = new AddSemester(coursestable);
        add_semester2 = new AddSemester(coursestable);
        add_semester3 = new AddSemester(coursestable);
        add_semester4 = new AddSemester(coursestable);
        body.getChildren().addAll(course_id,course_name,weight,add_semester1,add_semester2,add_semester3,add_semester4,label);

		HBox hbox_button = new HBox();
		hbox_button.setPadding(new Insets(20, 0, 15, 15)); //0 on bottom
		hbox_button.setAlignment(Pos.CENTER_LEFT);
		hbox_button.setSpacing(10);
		Label first_button = new Label();
		first_button.setTextFill(Color.WHITE);
		first_button.setAlignment(Pos.CENTER);
		first_button.setText("הוסף");
		first_button.setPrefWidth(75);
		first_button.setPrefHeight(40);
		Label second_button = new Label();
		second_button.setTextFill(Color.WHITE);
		second_button.setAlignment(Pos.CENTER);
		second_button.setText("ביטול");
		second_button.setVisible(true);
		second_button.setPrefWidth(75);
		second_button.setPrefHeight(40);
		hbox_button.getChildren().addAll(first_button, second_button);
		first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #607D8B;");
		second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #607D8B;");
		first_button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
			    Integer new_course_id;
                Double new_weight;
				if (mouse_event.getButton() != MouseButton.PRIMARY)
					return;
				try {
                    new_course_id = Integer.valueOf(course_id.getText());
                    new_weight = Double.valueOf(weight.getText());
                }
				catch (Exception e) {
                    label.setStyle("-fx-text-fill: red");
                    return;
                }
                Boolean flag = false;
				if (   ((add_semester1.program.getValue()!= "מסלול פקולטי") && (add_semester1.semester.getValue()!="סמסטר"))
				    || ((add_semester2.program.getValue()!= "מסלול פקולטי") && (add_semester2.semester.getValue()!="סמסטר"))
                    || ((add_semester3.program.getValue()!= "מסלול פקולטי") && (add_semester3.semester.getValue()!="סמסטר"))
                    || ((add_semester4.program.getValue()!= "מסלול פקולטי") && (add_semester4.semester.getValue()!="סמסטר")) )
				    flag = true;
				if (   ((add_semester1.program.getValue()!= "מסלול פקולטי") != (add_semester1.semester.getValue()!="סמסטר"))
                    || ((add_semester2.program.getValue()!= "מסלול פקולטי") != (add_semester2.semester.getValue()!="סמסטר"))
                    || ((add_semester3.program.getValue()!= "מסלול פקולטי") != (add_semester3.semester.getValue()!="סמסטר"))
                    || ((add_semester4.program.getValue()!= "מסלול פקולטי") != (add_semester4.semester.getValue()!="סמסטר")) )
                    flag = false;
				if ( (course_name.getText().equals("")) || (!flag) || ((new_weight % 0.5) != 0) || (coursestable.manager.courseloader.getCourse(new_course_id) != null) ) {
                    label.setStyle("-fx-text-fill: red");
                    return;
                }
                HashMap<String,Integer> semesters = new HashMap<String,Integer>();
                if (add_semester1.program.getValue()!= "מסלול פקולטי")
                    semesters.put(add_semester1.program.getValue(),Integer.valueOf(add_semester1.semester.getValue()));
                if (add_semester2.program.getValue()!= "מסלול פקולטי")
                    semesters.put(add_semester2.program.getValue(),Integer.valueOf(add_semester2.semester.getValue()));
                if (add_semester3.program.getValue()!= "מסלול פקולטי")
                    semesters.put(add_semester3.program.getValue(),Integer.valueOf(add_semester3.semester.getValue()));
                if (add_semester4.program.getValue()!= "מסלול פקולטי")
                    semesters.put(add_semester4.program.getValue(),Integer.valueOf(add_semester4.semester.getValue()));
                Course new_course = new Course(course_name.getText(), new_course_id,true,new_weight,semesters);
                coursestable.manager.courseloader.addNewCourse(new_course);
                coursestable.items.add(new Item(coursestable.manager,new_course));
				stage.close();
			}
		});
		first_button.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #455A64;");
			}
		});
		first_button.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #607D8B;");
			}
		});
		second_button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				if (mouse_event.getButton() != MouseButton.PRIMARY)
					return;
				stage.close();
			}
		});
		second_button.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #455A64;");
			}
		});
		second_button.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouse_event) {
				second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; -fx-background-color: #607D8B;");
			}
		});

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(1, 1, 1, 1));
		vbox.setStyle("-fx-background-color: white;");
		vbox.getChildren().addAll(hbox_title, body, hbox_button);
		FlowPane border = new FlowPane();
		border.setStyle("-fx-border-color: #CFD8DC;");
		border.getChildren().add(vbox);
		Scene scene = new Scene(border, 504, 460);
		stage.setScene(scene);
		stage.showAndWait();

	}
}
		
