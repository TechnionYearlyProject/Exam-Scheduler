package GUI.Components;

import db.Course;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;


public class Item {
    CheckBox take;
    String name;
    Integer study;
    ChoiceBox<String> pref;
    Label connections;
    public Item(Logic.Course course) {
        take = new CheckBox();
        take.setSelected(true);
        name = String.format("%06d",course.getCourseID()) + " - " + course.getCourseName();
        study = course.getCreditPoints().intValue();
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
    public Integer getStudy() {
        return study;
    }
    public ChoiceBox<String> getPref() {
        return pref;
    }
    public Label getConnections() {
        return connections;
    }


}
