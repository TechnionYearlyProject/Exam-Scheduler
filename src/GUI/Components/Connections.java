package GUI.Components;
import Logic.Course;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.*;

public class Connections extends VBox{
    ListView<String> courses;
    TextField newcourse;
    Integer courseid;
    Manager manager;
    public Connections(Manager manager, Integer new_courseid) {
        courseid = new_courseid;
        Map<Integer,String> conflicts = manager.courseloader.getCourse(courseid).getConflictCourses();
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Map.Entry<Integer,String> entry:conflicts.entrySet()) {
            items.add(String.format("%06d",entry.getKey()) + " - " + entry.getValue());
        }
        courses = new ListView<>();
        courses.setItems(items);
        courses.setStyle("-fx-focus-color: transparent");
        courses.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        newcourse = new TextField();
        newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        newcourse.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        newcourse.setPromptText("להוספה הקלידו מס' קורס...");
        newcourse.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (!(newcourse.getText().matches("\\d*"))) {
                        newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
                    }
                    else {
                        Course course = manager.courseloader.getCourse(Integer.parseInt(newcourse.getText()));
                        if (course == null) {
                            newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: #F44336;");//"; -fx-text-box-border: transparent");
                        }
                        else
                        {
                            Course curr_course = manager.courseloader.getCourse(courseid);
                            if (curr_course.getConflictCourses().containsKey(course.getCourseID()))
                                return;
                            curr_course.addConflictCourse(course.getCourseID(), course.getCourseName());
                            course.addConflictCourse(curr_course.getCourseID(), curr_course.getCourseName());
                            items.add(String.format("%06d",course.getCourseID()) + " - " + course.getCourseName());
                            courses.setItems(items);
                            newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: white;");//"; -fx-text-box-border: transparent");

                        }
                    }
                }
            }});
        this.getChildren().addAll(courses,newcourse);
        this.setPadding(new Insets(1,1,1,1));
        this.setStyle("-fx-background-color: lightgrey");
    }
}
