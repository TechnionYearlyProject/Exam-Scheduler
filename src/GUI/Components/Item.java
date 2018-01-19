

package GUI.Components;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


/**
 * @author Tal
 * @date 28/12/2017
 * Represent a single row type for the CourseTable.
 */

public class Item {
    private CheckBox take;
    String name;
    private SimpleStringProperty study;
    private ChoiceBox<String> pref;
    private Label connections;
    Manager manager;
    Item(Manager parent, Logic.Course course) {
        manager = parent;
        take = new CheckBox();
        take.setSelected(course.hasExam());
        take.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            manager.courseloader.getCourse(course.getCourseID()).setHasExam(take.isSelected());
            if (!take.isSelected()) {
                if (manager.been_scheduled) {
                    manager.scheduleA.unassignCourse(course);
                    manager.scheduleB.unassignCourse(course);
                }
                else {
                    manager.constraintlistA.removeConstraint(course.getCourseID());
                    manager.constraintlistB.removeConstraint(course.getCourseID());
                }
            }
            manager.A.schedule.removeTest(course.getCourseID());
            manager.B.schedule.removeTest(course.getCourseID());
        });
        name = String.format("%06d",course.getCourseID()) + " - " + course.getCourseName();
        study = new SimpleStringProperty(Integer.toString(course.getDaysBefore()));
        pref = new ChoiceBox<>();
        pref.getItems().addAll("אוטומטי","סוף תקופה","תחילת תקופה");
        if (course.isFirst())
            pref.setValue("תחילת תקופה");
        else if (course.isLast())
            pref.setValue("סוף תקופה");
        else
            pref.setValue("אוטומטי");
        pref.setOnAction(event -> {
            switch (pref.getValue()) {
                case "תחילת תקופה":
                    course.setAsFirst(true);
                    break;
                case "סוף תקופה":
                    course.setAsLast(true);
                    break;
                default:
                    course.setAsFirst(false);
                    course.setAsLast(false);
                    break;
            }
        });
        pref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pref.setPadding(new Insets(0,-7,0,0));
        connections = new Label();
        connections.setGraphic(new ImageView(new Image("/connection_icon.png")));
        connections.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
            new AddConnection(manager,course.getCourseID());
        });
    }
    public String getName() {
        return name;
    }
    public CheckBox getTake() {
        return take;
    }
    public String getStudy() {
        return study.get();
    }
    public void setStudy(String new_study) {
        study.set(new_study);
    }
    public ChoiceBox<String> getPref() {
        return pref;
    }
    public Label getConnections() {
        return connections;
    }
    public Integer getCourseID()
    {
        return Integer.parseInt(name.split(" - ")[0]);
    }


}