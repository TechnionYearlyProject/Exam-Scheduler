package GUI.Components;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.function.Function;


public class CustomButton extends HBox {
    Runnable function;
    public CustomButton(String title, String url, Runnable func) {
        function = func;
        Image image = new Image(url);
        Label image_label = new Label();
        image_label.setGraphic(new ImageView(image));
        Text text = new Text(title);
        text.setFill(Color.WHITE);

        this.getChildren().addAll(text,image_label);
        this.setPrefHeight(40);
        this.setPrefWidth(150);
        this.setMaxHeight(40);
        this.setMaxWidth(150);
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-radius: 6 6 6 6;  -fx-background-color: #607D8B");


        this.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                ChangeBackground("#455A64");
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                ChangeBackground("#607D8B");

            }
        });
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                ChangeBackground("#455A64");
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                if (mouse_event.getButton()!= MouseButton.PRIMARY)
                    return;
                    function.run();
            }
        });

    }
    public void ChangeBackground (String color)
    {
        this.setStyle("-fx-background-radius: 6 6 6 6; -fx-background-color:" + color);
    }

    }


