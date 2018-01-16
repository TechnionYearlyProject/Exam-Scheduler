package GUI.Components;
import Logic.Course;
import javafx.geometry.Pos;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Day extends VBox{
    static DateTimeFormatter disp_date = DateTimeFormatter.ofPattern("dd/MM");
    HBox hbox;
    Label lock_label;
    Label label;
    VBox tests;
    Boolean isBlocked;
    Schedule schedule;
    LocalDate date;
    boolean blockingAllowed;
    public Day(Schedule parent, LocalDate input_date) {
        date = input_date;
        schedule = parent;
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
        this.setSpacing(2);
        this.getChildren().add(hbox);
        this.getChildren().add(tests);
        this.setPrefWidth(90);
        this.setPrefHeight(100);
        this.setStyle("-fx-background-color: white");
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> lock_label.setVisible(true));
        this.setOnDragDropped(event->{
            Dragboard db = event.getDragboard();
            if (db.hasString() && !isBlocked) {
                Integer course_id = Integer.parseInt(db.getString());
                GregorianCalendar calendar = new GregorianCalendar(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
                if (schedule.moed.moedType == Moed.MoedType.A) {
                    if (schedule.moed.manager.constraintlistA.getConstraints(course_id)!=null) {
                        return;
                    }
                    else {
                        try {
                            schedule.moed.manager.constraintlistA.addConstraint(course_id, calendar, calendar);
                        } catch (Exception e) {}
                    }
                }
                else {
                    if (schedule.moed.manager.constraintlistB.getConstraints(course_id)!=null) {
                        return;
                    }
                    else {
                        try {
                            schedule.moed.manager.constraintlistB.addConstraint(course_id, calendar, calendar);
                        } catch (Exception e) {}
                    }
                }
                this.addTest(schedule.moed.manager.courseloader.getCourse(course_id));
            }
            event.setDropCompleted(true);
            event.consume();
        });
        this.addEventFilter(MouseEvent.MOUSE_EXITED, mouse_event -> {
            if (!isBlocked)
                lock_label.setVisible(false);
        });
    }
    private void Block() {
        if(blockingAllowed) {
            schedule.moed.manager.blockDay(date);
            this.setStyle("-fx-background-color: #ECEFF1");
            tests.setStyle("-fx-background-color: #ECEFF1");
            isBlocked = true;
        }
    }
    private void Enable() {
        schedule.moed.manager.unblockDay(date);
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
    public void disableBlocking(){
        blockingAllowed = false;
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> lock_label.setVisible(false));
    }
    public void enableBlocking(){
        blockingAllowed = true;
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouse_event -> lock_label.setVisible(true));
    }

    public VBox getTests() {
        return tests;
    }

    public void addTest(Course course) {
        tests.getChildren().add(new Test(course,true));
    }
}
