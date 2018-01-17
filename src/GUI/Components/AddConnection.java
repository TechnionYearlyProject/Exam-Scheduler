package GUI.Components;
import Logic.Course;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

import java.util.Map;

public class AddConnection {
	ImageView X_icon;
	ImageView X_hover_icon;
	ListView<String> courses;
	TextField newcourse;
	Integer courseid;
	Manager manager;
	public AddConnection(Manager parent, Integer new_courseid) {
		manager = parent;
		courseid = new_courseid;

		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		HBox hbox_title = new HBox();
		hbox_title.setPadding(new Insets(15, 15, 0, 15)); //0 on bottom
		stage.getIcons().add(new Image("/app_icon.png"));
		Label title_label = new Label("הוספת קישורים לקורס " + courseid);
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
		body.setPadding(new Insets(20, 20, 10, 20)); //0 on bottom
		body.setAlignment(Pos.CENTER_RIGHT);
		body.setSpacing(10);
		Map<Integer,String> conflicts = manager.courseloader.getCourse(courseid).getConflictCourses();
		ObservableList<String> items = FXCollections.observableArrayList();
		for (Map.Entry<Integer,String> entry:conflicts.entrySet()) {
			items.add(String.format("%06d",entry.getKey()) + " - " + entry.getValue());
		}
		courses = new ListView<>();
		courses.setItems(items);
		courses.setStyle("-fx-focus-color: transparent");
		courses.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		courses.setPrefHeight(234);
		courses.setFocusTraversable(false);
		newcourse = new TextField();
		newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
		newcourse.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		newcourse.setPromptText("להוספה הקלידו מס' קורס...");
		newcourse.setFocusTraversable(false);

		newcourse.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					if (!(newcourse.getText().matches("\\d*"))) {
						newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
					}
					else {
						if (newcourse.getText() == null || newcourse.getText().trim().isEmpty()){
							newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
							return;
						}
						Course course = manager.courseloader.getCourse(Integer.parseInt(newcourse.getText()));
						if (course == null) {
							newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
						}
						else
						{
							Course curr_course = manager.courseloader.getCourse(courseid);
							if (curr_course.getConflictCourses().containsKey(course.getCourseID()) || Integer.parseInt(newcourse.getText()) == courseid) {
								newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
								return;
							}
							curr_course.addConflictCourse(course.getCourseID(), course.getCourseName());
							course.addConflictCourse(curr_course.getCourseID(), curr_course.getCourseName());
							items.add(String.format("%06d",course.getCourseID()) + " - " + course.getCourseName());
							courses.setItems(items);
							newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #9CCC65;");//"; -fx-text-box-border: transparent");

						}
					}
				}
			}});
		newcourse.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
								String oldValue, String newValue) {

				newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
			}
		});
		body.getChildren().addAll(courses,newcourse);



		VBox vbox = new VBox();
		vbox.setPadding(new Insets(1, 1, 1, 1));
		vbox.setStyle("-fx-background-color: white;");
		vbox.getChildren().addAll(hbox_title, body);
		FlowPane border = new FlowPane();
		border.setStyle("-fx-border-color: #CFD8DC;");
		border.getChildren().add(vbox);
		Scene scene = new Scene(border, 504, 354);
		stage.setScene(scene);
		stage.show();

	}
}
		
