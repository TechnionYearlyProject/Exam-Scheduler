package GUI.Components;

import Logic.Course;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import java.util.ArrayList;
import java.util.Arrays;

public class Test extends Label{
    private static ArrayList<String> colors = new ArrayList<>(Arrays.asList("#D32F2F","#7B1FA2","#303F9F","#0288D1","#00796B","#689F38","#FBC02D","#F57C00", "#C2185B", "#5D4037", "#0097A7", "#FBC02D"));
    private static ArrayList<String> start_digits = new ArrayList<>(Arrays.asList("234","236","10","11","09","12","13","04","03"));
    public Test(Course course) {
        this.setTextFill(Paint.valueOf("white"));
        this.setText(course.getCourseName());
        this.setPadding(new Insets(0,3,0,0));
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setPrefWidth(86);
        this.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 3 3 3 3; -fx-background-color: " + getCourseColor(course.getCourseID()));
    }
    private String getCourseColor(int course_id) {
        String course_str = String.format("%06d",course_id);
        for (int i=0; i<start_digits.size();i++)
            if (course_str.startsWith(start_digits.get(i)))
                return colors.get(i);
        return colors.get(start_digits.size()-1);
    }
}
