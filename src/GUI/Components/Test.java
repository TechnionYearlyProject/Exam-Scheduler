package GUI.Components;
import Logic.Course;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.paint.Paint;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tal
 * @date 5/12/2017
 * Implements the design and functionality of a single test in the calendar.
 */

public class Test extends Label{
    Course course;
    Day day;
    private static List<String> colors = new ArrayList<>(Arrays.asList(
            "#26A69A","#FFA726","#FFEB3B","#9CCC65","#EF5350","#AB47BC","#42A5F5",
            "#8D6E63", "#EC407A", "#66BB6A", "#78909C"));
    private static List<String> bright_colors = new ArrayList<>(Arrays.asList(
            "#82e3d9","#ffd699","#fff599","#cee6b3","#f6a3a2","#ddb4e4","#b6dcfb",
            "#cbbab4", "#f8b9ce", "#b9dfbb", "#c4cfd4"));
    private static ArrayList<String> start_digits = new ArrayList<>(Arrays.asList("234","236","238","10","11","09","12","13","04","03"));
    private static void bindTooltip(final Node node, final Tooltip tooltip){
                node.setOnMouseMoved(event -> tooltip.show(node, event.getScreenX(), event.getScreenY() + 10));
                node.setOnMouseExited(event -> tooltip.hide());
    }

    /*
     * course- the course to display in the schedule
     * setTooltip - true if we want the full course information to appear when hovering
     */
    public Test(Day parent, Course course, boolean setTooltip) {
        day = parent;
        this.course = course;
        this.setTextFill(Paint.valueOf("white"));
        if (setTooltip)
            this.setText(course.getCourseName());
        else
            this.setText(String.format("%06d",course.getCourseID()) + " - " + course.getCourseName());

        this.setPadding(new Insets(0,3,0,3));
        this.setAlignment(Pos.CENTER_RIGHT);
        if(setTooltip){
            this.setPrefWidth(86);
            this.setPrefHeight(16);
            this.setMaxHeight(16);
            Tooltip msg = new Tooltip();
            msg.setGraphic(new Test(null, course,false));
            msg.setStyle("-fx-background-color: rgba(30,30,30,0.0);\n");
            bindTooltip(this, msg);
        }
        setColor(false);

        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            String course_ID = course.getCourseID().toString();
            String date_str = day.date.format(DateTimeFormatter.ofPattern("dd~LLLL~yyyy"));
            content.putString("DAY~"+course_ID+"~"+date_str);
            db.setContent(content);
            Scene scene = new Scene(new Test(null, course,false));
            db.setDragView(scene.snapshot(null));
            event.consume();
        });
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        if (day.schedule.moed.manager.been_scheduled)
                        {
                            if (day.schedule.moed.moedType == Moed.MoedType.A)
                                day.schedule.moed.manager.scheduleA.unassignCourse(course);
                            else
                                day.schedule.moed.manager.scheduleB.unassignCourse(course);
                            day.removeTest(course.getCourseID());
                        }
                        else
                            day.removeCourse(course);
                    }
                }
            }
        });
    }
    public void setColor(boolean isBright){
        if(isBright) {
            this.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 3 3 3 3; -fx-background-color: " +
                    getCourseColor(course.getCourseID(), bright_colors));
        }
        else{
            this.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 3 3 3 3; -fx-background-color: " +
                    getCourseColor(course.getCourseID(),colors));
        }
    }
    private String getCourseColor(int course_id, List<String> colorList) {
        String course_str = String.format("%06d",course_id);
        for (int i=0; i<start_digits.size();i++)
            if (course_str.startsWith(start_digits.get(i)))
                return colorList.get(i);
        return colorList.get(start_digits.size());
    }

}
