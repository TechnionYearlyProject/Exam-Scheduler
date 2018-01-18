package GUI.Components;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CustomButton extends HBox {
    private Runnable function;
    private boolean isCircle;
    public CustomButton(String title, String url, Runnable func, int height, int width) {
        function = func;
        Text text = new Text(title);
        text.setFill(Color.WHITE);
        this.getChildren().addAll(text);
        if(url != null){
            Image image = new Image(url);
            Label image_label = new Label();
            image_label.setGraphic(new ImageView(image));
            this.getChildren().addAll(image_label);
        }
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.setMaxHeight(height);
        this.setMaxWidth(width);
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);

        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> ChangeBackground("#455A64"));
        this.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event -> ChangeBackground("#607D8B"));
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> ChangeBackground("#455A64"));
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
                if(function!=null){
                    function.run();
                }
        });

    }
    public void setCircular(){
        this.setStyle("-fx-background-radius: 6 6 6 6;  -fx-background-color: #607D8B");
        isCircle = true;
    }
    public void setRectangle(){
        this.setStyle("-fx-background-color: #607D8B");
        isCircle = false;
    }
    private void ChangeBackground (String color)
    {
        if(isCircle){
            this.setStyle("-fx-background-radius: 6 6 6 6; -fx-background-color:" + color);
        }
        else {
            this.setStyle("-fx-background-color:" + color);
        }

    }
}


