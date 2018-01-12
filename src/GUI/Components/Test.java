package GUI.Components;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import Logic.Course;


public class Test extends Label{
    private static ArrayList<String> colors = new ArrayList<>(Arrays.asList("#D32F2F","#7B1FA2","#303F9F","#0288D1","#00796B","#689F38","#FBC02D","#F57C00", "#C2185B", "#5D4037", "#0097A7", "#FBC02D"));
    private static ArrayList<String> start_digits = new ArrayList<>(Arrays.asList("234","236","10","11","09","12","13","04","03"));

    private static void bindTooltip(final Node node, final Tooltip tooltip){
        node.setOnMouseMoved(event -> tooltip.show(node, event.getScreenX(), event.getScreenY() + 10));
        node.setOnMouseExited(event -> tooltip.hide());
    }
    /*
    * course- the course to display in the schedule
    * setTooltip - true if we want the full course information to appear when hovering
    */
    public Test(Course course, boolean setTooltip) {
        this.setTextFill(Paint.valueOf("white"));
        this.setText(course.getCourseName() + course.getCourseID());
        this.setPadding(new Insets(0,3,0,0));
        this.setAlignment(Pos.CENTER_RIGHT);

        if(setTooltip){
            this.setPrefWidth(86);
            this.setPrefHeight(16);
            this.setMaxHeight(16);
            Tooltip msg = new Tooltip();
            msg.setGraphic(new Test(course,false));
            msg.setStyle("-fx-background-color: rgba(30,30,30,0.0);\n");
            bindTooltip(this, msg);
        }

        this.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 3 3 3 3; -fx-background-color: "+ getCourseColor(course.getCourseID()));
    }

    private String getCourseColor(int course_int)
    {
        // int cant handle 0 digit
        String course_str = Integer.toString(course_int);
        for (int i=0; i<start_digits.size();i++)
            if (course_str.startsWith(start_digits.get(i)))
                return colors.get(i);
        return colors.get(start_digits.size()-1);
    }
}
