package GUI.Components;
import Logic.Course;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        ObservableList<String> items = FXCollections.observableArrayList("שלום","היי","ביי","word","stuff","bleach");
        courses = new ListView<>();
        courses.setItems(items);
        courses.setStyle("-fx-focus-color: transparent");
        courses.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        newcourse = new TextField();
        newcourse.setStyle("-fx-focus-color: transparent;-fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        newcourse.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        this.getChildren().addAll(courses,newcourse);
        this.setPadding(new Insets(1,1,1,1));
        this.setStyle("-fx-background-color: lightgrey");
    }
}
