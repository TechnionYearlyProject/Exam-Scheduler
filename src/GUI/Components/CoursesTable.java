package GUI.Components;

import Logic.CourseLoader;
import db.Course;
import db.Database;
import db.Semester;
import db.exception.InvalidDatabase;
import db.exception.SemesterFileMissing;
import db.exception.SemesterNotFound;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.logging.Filter;

public class CoursesTable extends javafx.scene.control.TableView<Item> {
    CourseLoader courses;
    Database db;
    TableColumn<Item,CheckBox> take;
    TableColumn<Item,String> name;
    TableColumn<Item,Integer> study;
    TableColumn<Item,CheckBox> pref;
    TableColumn<Item,Label> connections;
    public CoursesTable() {
        this.getStylesheets().add("/coursetable_style.css");
        try {
            db = new Database();
            courses = new CourseLoader(db.loadSemester(2017, "winter_test"),null);
        }
        catch (Exception e) {
        }
        take = new TableColumn<>("");
        take.setCellValueFactory(new PropertyValueFactory<>("take"));
        take.setStyle("-fx-alignment: CENTER-RIGHT");
        take.setPrefWidth(40);
        name = new TableColumn<>("שם הקורס");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setStyle("-fx-alignment: CENTER-RIGHT");
        name.setPrefWidth(212);
        study = new TableColumn<>("ימי למידה");
        study.setCellValueFactory(new PropertyValueFactory<>("study"));
        study.setStyle("-fx-alignment: CENTER-RIGHT");
        study.setPrefWidth(72);
        pref = new TableColumn<>("העדפת שיבוץ");
        pref.setCellValueFactory(new PropertyValueFactory<>("pref"));
        pref.setStyle("-fx-alignment: CENTER-RIGHT");
        pref.setPrefWidth(100);
        connections = new TableColumn<>("קשרים");
        connections.setCellValueFactory(new PropertyValueFactory<>("connections"));
        connections.setStyle("-fx-alignment: CENTER-RIGHT");
        connections.setPrefWidth(56);
        this.setPrefWidth(500);
        this.setPrefHeight(726);
        this.setItems(getData());
        this.getColumns().addAll(connections,pref,study,name,take);
        this.setRowFactory(tv -> {
            CourseRow<Item> row = new CourseRow<>();
            row.setOnDragDetected(event -> {
                if(take.getCellData(row.getIndex()).isSelected()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(name.getCellData(row.getIndex()));
                    db.setContent(content);
                    db.setDragView(row.snapshot(null, null));
                }
                event.consume();
            });
            row.setOnDragOver(event -> {
                if (event.getGestureSource() != row && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });
            return row ;
        });
    }
    public ObservableList<Item> getData() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        for (Logic.Course course:courses.getCourses().values()) {
            items.add(new Item(course));
        }
        return items;
    }

}
