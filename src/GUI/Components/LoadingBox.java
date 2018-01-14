package GUI.Components;
// http://htmlcolorcodes.com/color-chart/ 
// https://icons8.com/icon/new-icons/all

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.util.Duration;


public class LoadingBox {
    ImageView X_icon;
    ImageView X_hover_icon;
    Stage stage;
    public LoadingBox(Runnable func) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        HBox hbox_title = new HBox();
        hbox_title.setPadding(new Insets(15, 15, 0, 15)); //0 on bottom
        stage.getIcons().add(new Image("/app_icon.png"));
        Label title_label = new Label("משבץ לוח מבחנים");
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
        HBox hbox_body = new HBox();
        hbox_body.setPadding(new Insets(20, 20, 0, 20)); //0 on bottom
        hbox_body.setAlignment(Pos.CENTER_RIGHT);
        hbox_body.setSpacing(10);
        Label icon_label = new Label();
        icon_label.setPrefWidth(50);
        icon_label.setPrefHeight(50);
        Image icon_image = new Image("/loading.gif");
        icon_label.setGraphic(new ImageView(icon_image));
        hbox_body.getChildren().addAll(icon_label);
        hbox_body.setAlignment(Pos.CENTER);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(1, 1, 1, 1));
        vbox.setStyle("-fx-background-color: white;");
        vbox.getChildren().addAll(hbox_title, hbox_body);
        vbox.setPrefHeight(248);
        FlowPane border = new FlowPane();
        border.setStyle("-fx-border-color: #CFD8DC;");
        border.getChildren().add(vbox);
        Scene scene = new Scene(border, 504, 250);
        stage.setScene(scene);
        stage.show();





        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {stage.show();}),
                new KeyFrame(Duration.seconds(5), e -> {stage.close(); func.run();})
        );
        timeline.play();

    }
    public void close() {
        stage.close();
    }
}
		
