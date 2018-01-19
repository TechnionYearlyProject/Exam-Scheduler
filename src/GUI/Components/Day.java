package GUI.Components;
import Logic.Course;
import Logic.Exceptions.IllegalRange;
import db.Constraint;
import db.ConstraintList;
import db.exception.DuplicateConstraints;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dorbartov
 * @date 03/01/2018
 * This class creates a single instance of a day appearing in the GUI's schedule, including the particular
 * date and the tests taking place.
 */
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
    List<Test> testList = new ArrayList<>();

    /**
     * @author dorbartov
     * @date 03/01/2018
     * @param parent used to access the schedule in which the day appears and so the entire system
     * @param input_date the date to which the day corresponds.
     */
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
            Integer course_id;
            Dragboard db = event.getDragboard();
            if (db.hasString() && !isBlocked) {
                if (db.getString().split("~")[0].equals("DAY")) {
                    Course course = schedule.moed.manager.courseloader.getCourse(Integer.parseInt(db.getString().split("~")[1]));
                    LocalDate old_date = LocalDate.of(Integer.parseInt(db.getString().split("~")[4]), Integer.parseInt(db.getString().split("~")[3]), Integer.parseInt(db.getString().split("~")[2]));
                    if (old_date.isBefore(schedule.start) || old_date.isAfter(schedule.finish)) {
                        new AlertBox(AlertType.ERROR, "לא ניתן להעביר מבחנים בין מועדים", null);
                        return;
                    }
                    if (schedule.moed.manager.been_scheduled) {
                        if (schedule.moed.moedType == Moed.MoedType.A)
                            if (!schedule.moed.manager.scheduleA.isMovePossible(course, date,schedule.moed.manager.courseloader)) {
                                new AlertBox(AlertType.ERROR, "השיבוץ שניסית לבצע לא מקיים את ההגבלות שהגדרת", null);
                                return;
                            }
                        if (schedule.moed.moedType == Moed.MoedType.B)
                            if (!schedule.moed.manager.scheduleB.isMovePossible(course, date,schedule.moed.manager.courseloader)) {
                                new AlertBox(AlertType.ERROR, "השיבוץ שניסית לבצע לא מקיים את ההגבלות שהגדרת", null);
                                return;
                            }
                    }
                    Day old_day = schedule.days.get(old_date);
                    old_day.removeCourse(course);
                    course_id = course.getCourseID();
                } else
                    course_id = Integer.parseInt(db.getString());

                Course curr_course = schedule.moed.manager.courseloader.getCourse(course_id);
                if (!schedule.moed.manager.been_scheduled) {
                    ConstraintList cl = null;
                    Logic.Schedule temp_schedule = null;
                    if (schedule.moed.moedType == Moed.MoedType.A) {
                        cl = schedule.moed.manager.constraintlistA;
                        try {
                            temp_schedule = new Logic.Schedule(schedule.start, schedule.finish, schedule.moed.manager.occupiedA);
                        } catch (IllegalRange e) {}
                    }
                    else {
                        cl = schedule.moed.manager.constraintlistB;
                        try {
                            temp_schedule = new Logic.Schedule(schedule.start, schedule.finish, schedule.moed.manager.occupiedB);
                        } catch (IllegalRange e) {}
                    }
                    for (Map.Entry<Integer, List<Constraint>> curr_map : cl.constraints.entrySet()) {
                        if (curr_course.getConflictCourses().containsKey(curr_map.getKey())) {
                            for (Constraint curr_cons : curr_map.getValue()) {
                                if (curr_cons.forbidden)
                                    continue;
                                LocalDate date1 = date;
                                LocalDate date2 = curr_cons.date;
                                Course course1 = curr_course;
                                Course course2 = schedule.moed.manager.courseloader.getCourse(curr_map.getKey());
                                if (date1.isAfter(date2)) {
                                    LocalDate date_temp = date1;
                                    Course course_temp = course1;
                                    date1 = date2;
                                    course1 = course2;
                                    date2 = date_temp;
                                    course2 = course_temp;
                                }
                                int between = temp_schedule.daysBetween(date1, date2);
                                if (between == 0 || course2.getDaysBefore() > between) {
                                    new AlertBox(AlertType.INFO, "מועדי הבחינות שקבעת לא מקיימים את ימי הלמידה שהוגדרו.", null);
                                }
                            }
                        }
                    }
                }

                if (schedule.moed.moedType == Moed.MoedType.A) {
                    if (schedule.moed.manager.constraintlistA.getConstraints(course_id) != null) {
                        if (schedule.moed.manager.constraintlistA.getConstraints(course_id).size() != 0) {
                            return;
                        }
                    }
                try {
                        schedule.moed.manager.constraintlistA.addConstraint(course_id, date);
                    } catch (DuplicateConstraints e) {}

                } else {
                    if (schedule.moed.manager.constraintlistB.getConstraints(course_id) != null) {
                        if (schedule.moed.manager.constraintlistB.getConstraints(course_id).size() != 0) {
                            return;
                        }
                    }
                    try {
                        schedule.moed.manager.constraintlistB.addConstraint(course_id, date);
                    } catch (DuplicateConstraints e) {}
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

    /**
     * @author dorbartov
     * @date 03/01/2018
     * blocks the day from being scheduled into, and undos manual changes made prior
     */
    private void Block() {
        if(!schedule.moed.manager.been_scheduled) {
            schedule.moed.manager.blockDay(date);
            this.setStyle("-fx-background-color: #ECEFF1");
            tests.setStyle("-fx-background-color: #ECEFF1");
            isBlocked = true;
            if (schedule.moed.moedType == Moed.MoedType.A)
                schedule.moed.manager.constraintlistA.removeConstraint(date);
            else
                schedule.moed.manager.constraintlistB.removeConstraint(date);
            this.getChildren().remove(1);
            tests = new VBox();
            tests.setSpacing(1);
            tests.setStyle("-fx-background-color: white");
            tests.setAlignment(Pos.TOP_CENTER);
            this.getChildren().add(tests);
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
        Test test = new Test(this,course,true);
        tests.getChildren().add(test);
        testList.add(test);
    }

    public void removeCourse(Course course){
        List<Test> temp = new ArrayList<>(testList);
        int i=0;
        for (Test test:temp)
        {
            if (test.course.getCourseID().equals(course.getCourseID())) {
                testList.remove(i);
                tests.getChildren().remove(i);
                if (schedule.moed.moedType == Moed.MoedType.A)
                    schedule.moed.manager.constraintlistA.removeConstraint(course.getCourseID(), date);
                else
                    schedule.moed.manager.constraintlistB.removeConstraint(course.getCourseID(), date);
                break;
            }
            i+=1;
        }
    }

    /**
     * @author dorbartov
     * @date 17/01/2018
     * @param courseID id of scheduled course to be removed from this day.
     */
    public void removeTest(Integer courseID) {
        List<Test> temp = new ArrayList<>(testList);
        int i=0;
        for (Test test:temp) {
            if (test.course.getCourseID().equals(courseID)) {
                testList.remove(i);
                tests.getChildren().remove(i);
                break;
            }
            i+=1;
        }
    }

}
