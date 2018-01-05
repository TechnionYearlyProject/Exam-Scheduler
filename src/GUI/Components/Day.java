package GUI.Components;
import Logic.Course;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Day extends VBox{
    static DateTimeFormatter disp_date = DateTimeFormatter.ofPattern("dd/MM");
    HBox hbox;
    Label lock_label;
    Label label;
    VBox tests;
    Boolean isBlocked;
    public Day(LocalDate input_date) {
        isBlocked = false;
        label = new Label(input_date.format(disp_date));
        //label.setStyle("-fx-font-weight: bold");
        label.setPadding(new Insets(2,0,0,2));
        label.setFont(Font.font(14));
        label.setPrefWidth(60);
        label.setPrefHeight(15);

        Image image = new Image("/GUI/resources/lock_day.png");
        lock_label = new Label();
        lock_label.setGraphic(new ImageView(image));
        lock_label.setMinHeight(20);
        lock_label.setMinWidth(20);
        lock_label.setPrefHeight(20);
        lock_label.setPrefWidth(20);
        lock_label.setAlignment(Pos.TOP_RIGHT);
        lock_label.setPadding(new Insets(3,2,0,0));
        lock_label.setVisible(false);

        lock_label.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                if (isBlocked)
                    Enable();
                else
                    Block();

            }
        });
        hbox = new HBox();
        hbox.getChildren().addAll(label,lock_label);

        tests = new VBox();
        tests.setSpacing(1);
        tests.setStyle("-fx-background-color: white");
        tests.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(2);
        this.getChildren().add(hbox);
        this.getChildren().add(tests);
        this.setPrefWidth(90);
        this.setPrefHeight(100);
        this.setStyle("-fx-background-color: white");

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                addTest(new Course("קומבי", 234123, true, 3.0 ));
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                lock_label.setVisible(true);
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse_event) {
                if (!isBlocked)
                    lock_label.setVisible(false);
            }
        });
    }
    public void Block() {
        this.setStyle("-fx-background-color: #ECEFF1");
        tests.setStyle("-fx-background-color: #ECEFF1");
        isBlocked = true;
    }
    public void Enable() {
        this.setStyle("-fx-background-color: white");
        tests.setStyle("-fx-background-color: white");
        isBlocked = false;
    }
    public void Disable() {
        this.setStyle("-fx-background-color: #ECEFF1");
        tests.setStyle("-fx-background-color: #ECEFF1");
        isBlocked = true;
        this.setDisable(true);
    }
    public void addTest(Course course) {
        tests.getChildren().add(new Test(course));
    }
}
