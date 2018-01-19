package GUI.Components;
import Logic.CourseLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author dorbartov
 * @date 03/01/2018
 * Creates an instance of the time table of days representing a single moed.
 */
public class Schedule extends GridPane{
    private static ArrayList<String > weekdays =
            new ArrayList<>(Arrays.asList("ו'","ה'","ד'","ג'","ב'","א'"));
    LocalDate start;
    LocalDate finish;
    HashMap<LocalDate,Day> days;
    Moed moed;

    /**
     * @author dorbartov
     * @date 03/01/2018
     * @param parent used to access the containing moed and so the entire system.
     * @param input_start start date of schedule
     * @param input_finish end date of schedule
     */
    public Schedule(Moed parent, LocalDate input_start, LocalDate input_finish) {
        moed = parent;
        start = input_start;
        finish = input_finish;
        days = new HashMap<>();
        for (int i=0; i<6; i++) {
            VBox vbox = new VBox();
            vbox.setStyle("-fx-background-color: #CFD8DC");
            vbox.setPrefHeight(30);
            vbox.setPrefWidth(75);
            vbox.setAlignment(Pos.CENTER_RIGHT);
            Label label = new Label(weekdays.get(i));
            label.setPadding(new Insets(2,10,0,0));
            label.setFont(Font.font(17));
            vbox.getChildren().add(label);
            this.add(vbox, i,0);
        }
        LocalDate current;
        switch (input_start.getDayOfWeek().name()) {
            case "SUNDAY":
                current = start;
                break;
            case "SATURDAY":
                current = input_start.plusDays(1);
                break;
            default:
                current = input_start.minusDays(input_start.getDayOfWeek().ordinal() + 1);
                break;
        }

        for (int i = 5; i >= 0; i--, current = current.plusDays(1)) {
            if (current.isBefore(input_start)) {
                Day day = new Day(this,current);
                day.Disable();
                this.add(day, i, 1);
            } else {
                if (!current.isAfter(input_finish)) {
                    Day day = new Day(this,current);
                    days.put(current, day);
                    this.add(day, i, 1);
                } else {
                    Day day = new Day(this,current);
                    day.Disable();
                    this.add(day, i, 1);
                }
            }
        }
        current = current.plusDays(1);
        int i=2;
        while (!current.isAfter(input_finish))
        {
            for (int j=5;j>=0;j--) {
                if (current.isAfter(input_finish)) {
                    Day day = new Day(this,current);
                    day.Disable();
                    this.add(day, j, i);
                    current = current.plusDays(1);
                    continue;
                }
                Day day = new Day(this,current);
                days.put(current,day);
                this.add(day,j,i);
                current = current.plusDays(1);
            }
            current = current.plusDays(1);
            i++;
        }
        this.setStyle("-fx-background-color: #CFD8DC");
        this.setHgap(1);
        this.setVgap(1);
        this.setPadding(new Insets(1,1,1,1));
        this.setMaxWidth(548);
    }

    /**
     * @author dorbartov
     * @date 03/01/2018
     * Updated the GUI schedule to display the results given in the LOGIC schedule.
     * @param schedule a logic.schedule type containing a schedule created by our algorithm.
     * @param courseloader used to get actual Course typed with a given course id.
     */
    public void updateSchedule(Logic.Schedule schedule, CourseLoader courseloader) {
        for (Logic.Day day:schedule.getSchedulableDays()) {
            Day curr_day = days.get(day.getDate());
            for (Integer course_number : day.getCoursesScheduledToTheDay()) {
                Boolean flag = true;
                for (Test curr_test:curr_day.testList) {
                    if (curr_test.course.getCourseID().equals(course_number)) {
                        flag = false;
                    }
                }
                if (flag)
                    curr_day.addTest(courseloader.getCourse(course_number));
            }
        }
    }

    /**
     * @author dorbartov
     * @date 07/01/2018
     * removes a course with the given id from the current GUI schedule.
     * @param CourseID the course id of the course to be removed.
     */
    public void removeTest(Integer CourseID) {
        Day day_to_remove = null;
        for (Day day: days.values())
            for (Test test:day.testList)
                if (test.course.getCourseID().equals(CourseID))
                    day_to_remove = day;
        if (day_to_remove != null)
            day_to_remove.removeTestGraphically(CourseID);
    }

}
