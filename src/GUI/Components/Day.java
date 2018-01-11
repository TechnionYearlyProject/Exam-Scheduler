package GUI.Components;
import Logic.Course;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;


public class Day extends VBox{
    private static DateTimeFormatter disp_date = DateTimeFormatter.ofPattern("dd/MM");
    private HBox hbox;
    private Label lock_label;
    private Label label;
    private VBox tests;
    private Boolean isBlocked;
    private List<Integer> displayedCourseIDs = new LinkedList<>();
    public Day(LocalDate input_date, List<Course> courses) {
        isBlocked = false;
        label = new Label(input_date.format(disp_date));
        label.setPadding(new Insets(2,0,0,2));
        label.setFont(Font.font(14));
        label.setPrefWidth(75);
        label.setPrefHeight(15);

        Image image = new Image("/lock_day.png");
        lock_label = new Label();
        lock_label.setGraphic(new ImageView(image));
        lock_label.setMinHeight(20);
        lock_label.setMinWidth(20);
        lock_label.setPrefHeight(20);
        lock_label.setPrefWidth(20);
        lock_label.setAlignment(Pos.TOP_RIGHT);
        lock_label.setPadding(new Insets(3,2,0,0));
        lock_label.setVisible(false);

        lock_label.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
            if (isBlocked)
                Enable();
            else
                Block();

        });
        hbox = new HBox();
        hbox.getChildren().addAll(label,lock_label);
        tests = new VBox();
        tests.setSpacing(1);
        tests.setStyle("-fx-background-color: white");
        tests.setAlignment(Pos.TOP_CENTER);

         if (courses != null)
             for (Course course:courses)
                 this.addTest(course);

        this.setSpacing(2);
        this.getChildren().add(hbox);
        this.getChildren().add(tests);
        this.setPrefWidth(90);
        this.setPrefHeight(100);
        this.setStyle("-fx-background-color: white");

        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> lock_label.setVisible(true));
        this.setOnDragDropped(event->{
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString() && !isBlocked) {
                String courseName = db.getString().split("_")[0];
                String courseNum = db.getString().split("_")[1];
                addTest(new Course(courseName, Integer.parseInt(courseNum), true, 3.0 ));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event -> {
            if (!isBlocked)
                lock_label.setVisible(false);
        });
    }
    private void Block() {
        this.setStyle("-fx-background-color: #ECEFF1");
        tests.setStyle("-fx-background-color: #ECEFF1");
        isBlocked = true;
    }
    private void Enable() {
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
        if(!displayedCourseIDs.contains(course.getCourseID())){
            displayedCourseIDs.add(course.getCourseID());
            tests.getChildren().add(new Test(course));
        }
    }
}
