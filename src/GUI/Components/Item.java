package GUI.Components;

import db.Course;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;


public class Item {
    CheckBox take;
    String name;
    Integer study;
    ChoiceBox<String> pref;
    Button connections;
    public Item(Course course) {
        take = new CheckBox();
        take.setSelected(true);
        name = course.id + " - "+ course.name;
        study = (int)(course.weight);
        pref = new ChoiceBox<String>();
        pref.getItems().addAll("אוטומטי","סוף תקופה","תחילת תקופה");
        pref.setValue("אוטומטי");
        pref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        connections = new Button();
        connections.setText("קשרים");
    }
    public String getName() {
        return name;
    }
    public CheckBox getTake() {
        return take;
    }
    public Integer getStudy() {
        return study;
    }
    public ChoiceBox<String> getPref() {
        return pref;
    }
    public Button getConnections() {
        return connections;
    }


}
