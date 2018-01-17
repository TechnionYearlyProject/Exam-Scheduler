package GUI.Components;
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

public class AddCourse {
	ImageView X_icon;
	ImageView X_hover_icon;
	CoursesTable coursestable;
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
        TextField course_id = new TextField();
        course_id.setFocusTraversable(false);
        course_id.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        course_id.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        course_id.setPromptText("מספר הקורס");
        TextField course_name = new TextField();
        course_name.setFocusTraversable(false);
        course_name.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        course_name.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        course_name.setPromptText("שם הקורס");
        TextField weight = new TextField();
        weight.setFocusTraversable(false);
        weight.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        weight.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        weight.setPromptText("נקודות זכות");

        Label label = new Label("בחירת מסלולים וסימסטרים:");
        label.setAlignment(Pos.CENTER_RIGHT);

        AddSemester add_semester1 = new AddSemester(coursestable);
        AddSemester add_semester2 = new AddSemester(coursestable);
        AddSemester add_semester3 = new AddSemester(coursestable);
        AddSemester add_semester4 = new AddSemester(coursestable);
        body.getChildren().addAll(course_id,course_name,weight,label,add_semester1,add_semester2,add_semester3,add_semester4);

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
				if (mouse_event.getButton() != MouseButton.PRIMARY)
					return;
				//func.run();
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
		
