package GUI.Components;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Item {
    CheckBox take;
    String name;
    SimpleStringProperty study;
    ChoiceBox<String> pref;
    Label connections;
    Manager manager;
    public Item(Manager parent, Logic.Course course) {
        manager = parent;
        take = new CheckBox();
        take.setSelected(course.hasExam());
        take.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            manager.courseloader.getCourse(course.getCourseID()).setHasExam(take.isSelected());
        });
        name = String.format("%06d",course.getCourseID()) + " - " + course.getCourseName();
        study = new SimpleStringProperty(new Integer(course.getDaysBefore()).toString());
        pref = new ChoiceBox<String>();
        pref.getItems().addAll("אוטומטי","סוף תקופה","תחילת תקופה");
        if (course.isFirst())
            pref.setValue("תחילת תקופה");
        else if (course.isLast())
                pref.setValue("סוף תקופה");
        else
            pref.setValue("אוטומטי");
        pref.setOnAction(event -> {
            if (pref.getValue() == "תחילת תקופה")
                course.setAsFirst(true);
            else if (pref.getValue() == "סוף תקופה")
                    course.setAsLast(true);
            else {
                course.setAsFirst(false);
                course.setAsLast(false);
            }
        });
        pref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pref.setPadding(new Insets(0,-7,0,0));
        connections = new Label();
        connections.setGraphic(new ImageView(new Image("/connection_icon.png")));
        connections.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
            AddConnection connections = new AddConnection(manager,course.getCourseID());
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
