package GUI.Components;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

/**
 * @author dorbartov
 * @date 04/01/2018
 * The class diaplays an alert for the user. there are 3 types that appear in that AlertType ENUM, each with its'
 * matching design and functionality.
 */
public class AlertBox {
	ImageView X_icon;
	ImageView X_hover_icon;

    /**
     * @author dorbartov
     * @date 18/01/2017
     * Another constructor, that defaults the AlertBox to Show instead of ShowAndWait.
     */
    public AlertBox(AlertType type, String msg, Runnable func) {
        this(type,msg,func, false);
    }

    /**
     * @author dorbartov
     * @date 04/01/2018
     * @param type type of AlertBox.
     * @param msg to be displayed to the user.
     * @param func function to be ran if the user chooses Okay. doesn't work for errorType.
     * @param wait whether to Show or ShowAndWait.
     */
	public AlertBox(AlertType type, String msg, Runnable func, Boolean wait) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);

		HBox hbox_title = new HBox();
		hbox_title.setPadding(new Insets(15, 15, 0, 15)); //0 on bottom
		String temp;
		stage.getIcons().add(new Image("/app_icon.png"));
		if (type == AlertType.ERROR) {
			temp = "שגיאה";
		}
		else if (type == AlertType.INFO) {
			temp = "מידע";
		}
		else {
			temp = "אישור";
		}
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
		close_label.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
            stage.close();
        });
		close_label.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event ->
				close_label.setGraphic(X_hover_icon));
		close_label.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event ->
				close_label.setGraphic(X_icon));
		hbox_title.getChildren().addAll(close_label, title_label);


		HBox hbox_body = new HBox();
		hbox_body.setPadding(new Insets(20, 20, 0, 20)); //0 on bottom
		hbox_body.setAlignment(Pos.CENTER_RIGHT);
		hbox_body.setSpacing(10);
		Label icon_label = new Label();
		icon_label.setPrefWidth(50);
		icon_label.setPrefHeight(50);
		Image icon_image;
		if (type == AlertType.ERROR)
			icon_image = new Image("/error_icon.png");
		else if (type == AlertType.INFO)
			icon_image = new Image("/info_icon.png");
		else
			icon_image = new Image("/confirm_icon.png");

		icon_label.setGraphic(new ImageView(icon_image));
		Label msg_label = new Label(msg);
		msg_label.setAlignment(Pos.CENTER_RIGHT);
		hbox_body.getChildren().addAll(msg_label, icon_label);

		HBox hbox_button = new HBox();
		hbox_button.setPadding(new Insets(20, 0, 15, 15)); //0 on bottom
		hbox_button.setAlignment(Pos.CENTER_LEFT);
		hbox_button.setSpacing(10);
		Label first_button = new Label();
		first_button.setTextFill(Color.WHITE);
		first_button.setAlignment(Pos.CENTER);
		first_button.setText("אישור");
		first_button.setPrefWidth(75);
		first_button.setPrefHeight(40);
		Label second_button = new Label();
		second_button.setTextFill(Color.WHITE);
		second_button.setAlignment(Pos.CENTER);
		second_button.setText("ביטול");
		second_button.setPrefWidth(75);
		second_button.setPrefHeight(40);
		second_button.setVisible(false);
		hbox_button.getChildren().addAll(first_button, second_button);
		if (type == AlertType.ERROR) {
			first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
					"-fx-background-color: #F44336;");
			first_button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse_event -> {
                if (mouse_event.getButton() != MouseButton.PRIMARY)
                    return;
                stage.close();
            });
			first_button.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #E53935;"));
			first_button.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #F44336;"));
		}
		else if (type == AlertType.INFO)
		{
			first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
					"-fx-background-color: #FFC107;");
			first_button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse_event -> {
                if (mouse_event.getButton() != MouseButton.PRIMARY)
                    return;
                stage.close();
            });
			first_button.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #FFB300;"));
			first_button.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #FFC107;"));
		}
		else {
			second_button.setVisible(true);
			second_button.setText("ביטול");
			first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
					"-fx-background-color: #2196F3;");
			second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
					"-fx-background-color: #2196F3;");
			first_button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse_event -> {
                if (mouse_event.getButton() != MouseButton.PRIMARY)
                    return;
                func.run();
                stage.close();
            });
			first_button.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #1E88E5;"));
			first_button.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event ->
					first_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #2196F3;"));
			second_button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse_event -> {
                if (mouse_event.getButton() != MouseButton.PRIMARY)
                    return;
                stage.close();
            });
			second_button.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event ->
					second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #1E88E5;"));
			second_button.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event ->
					second_button.setStyle("-fx-background-radius: 6,6,6,6; -fx-border-radius: 6,6,6,6; " +
							"-fx-background-color: #2196F3;"));
		}
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(1, 1, 1, 1));
		vbox.setStyle("-fx-background-color: white;");
		vbox.getChildren().addAll(hbox_title, hbox_body, hbox_button);
		FlowPane border = new FlowPane();
		border.setStyle("-fx-border-color: #CFD8DC;");
		border.getChildren().add(vbox);
		Scene scene = new Scene(border, 504, 194);
		stage.setScene(scene);
		if (wait)
			stage.showAndWait();
		else
			stage.show();
	}
}
		
