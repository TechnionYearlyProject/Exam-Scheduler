package GUI.Components;
import Logic.Course;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Connections extends VBox{
    ListView<String> courses;
    TextField newcourse;
    Integer courseid;
    public Connections(Integer new_courseid) {
        courseid = new_courseid;
        ObservableList<String> items = FXCollections.observableArrayList("hello","bye","talk","word","stuff","bleach");
        courses = new ListView<>();
        courses.setItems(items);
        newcourse = new TextField();
        this.getChildren().addAll(courses,newcourse);
    }
}
