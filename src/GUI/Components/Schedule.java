package GUI.Components;

import db.Semester;
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

public class Schedule extends GridPane{
    static ArrayList<String > weekdays = new ArrayList<String>(Arrays.asList("ו'","ה'","ד'","ג'","ב'","א'"));
    LocalDate start;
    LocalDate finish;
    HashMap<LocalDate,Day> days;

    public Schedule(LocalDate input_start, LocalDate input_finish) {
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
            //label.setStyle("-fx-font-weight: bold");
            vbox.getChildren().add(label);
            this.add(vbox, i,0);
        }
        LocalDate current;
        if (input_start.getDayOfWeek().name() == "SUNDAY")
            current = start;
        else
            current = input_start.minusDays(input_start.getDayOfWeek().ordinal()+1);

        for (int i = 5; i >= 0; i--, current = current.plusDays(1)) {
            if (current.isBefore(input_start)) {
                Day day = new Day(current);
                day.Disable();
                this.add(day, i, 1);
            } else {
                if (!current.isAfter(input_finish)) {
                    Day day = new Day(current);
                    days.put(current, day);
                    this.add(day, i, 1);
                } else {
                    Day day = new Day(current);
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
                    Day day = new Day(current);
                    day.Disable();
                    this.add(day, j, i);
                    current = current.plusDays(1);
                    continue;
                }
                Day day = new Day(current);
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
    /*public void addTest(LocalDate date, Course course)
    {
        days.get(date).addTest(course);
    }*/
    public void updateSchedule(Logic.Schedule schedule) {
        for (Logic.Day day:schedule.getSchedulableDays()) {
            Day curr_day = days.get(day.getDate());
            for (Integer course_number : day.getCoursesScheduledToTheDay()) {
                curr_day.addTest(course_number);
            }
        }

    }
}
