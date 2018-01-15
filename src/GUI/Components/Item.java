package GUI.Components;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Item {
    CheckBox take;
    String name;
    SimpleStringProperty study;
    ChoiceBox<String> pref;
    Label connections;
    public Item(Logic.Course course) {
        take = new CheckBox();
        take.setSelected(true);
        name = String.format("%06d",course.getCourseID()) + " - " + course.getCourseName();
        study = new SimpleStringProperty((new Integer(course.getCreditPoints().intValue()).toString()));
        pref = new ChoiceBox<String>();
        pref.getItems().addAll("אוטומטי","סוף תקופה","תחילת תקופה");
        pref.setValue("אוטומטי");
        pref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pref.setPadding(new Insets(0,-7,0,0));
        connections = new Label();
        connections.setGraphic(new ImageView(new Image("/connection_icon.png")));
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
