package GUI.Components;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import Logic.Course;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

public class CoursesTable extends VBox{
    TableColumn<Item,CheckBox> take;
    TableColumn<Item,String> name;
    TableColumn<Item,String> study;
    TableColumn<Item,CheckBox> pref;
    TableColumn<Item,Label> connections;
    TableView<Item> table;
    FilteredList<Item> filteredList;
    Manager manager;

    public CoursesTable(Manager parent) {
        this.getStylesheets().add("/coursetable_style.css");
        manager = parent;
        table = new TableView<>();
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
        study.setCellFactory(TextFieldTableCell.forTableColumn());
        study.setOnEditCommit(event -> {
            try {
                event.getRowValue().setStudy(event.getNewValue());
                manager.courseloader.getCourse(event.getRowValue().getCourseID()).setDaysBefore(Integer.parseInt(event.getNewValue()));
            } catch (Exception e) {}
        });
        pref = new TableColumn<>("העדפת שיבוץ");
        pref.setCellValueFactory(new PropertyValueFactory<>("pref"));
        pref.setStyle("-fx-alignment: CENTER-RIGHT");
        pref.setPrefWidth(100);
        connections = new TableColumn<>("קשרים");
        connections.setCellValueFactory(new PropertyValueFactory<>("connections"));
        connections.setStyle("-fx-alignment: CENTER-RIGHT");
        connections.setPrefWidth(56);
        table.setEditable(true);
        table.setPrefWidth(500);
        table.setPrefHeight(726);
        table.setItems(getData());
        table.getColumns().addAll(connections,pref,study,name,take);
        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if(take.getCellData(row.getIndex()).isSelected()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    String course_str = (name.getCellData(row.getIndex())).split(" - ")[0];
                    content.putString(course_str);
                    db.setContent(content);
                    Course course = manager.courseloader.getCourse(Integer.parseInt(course_str));
                    Scene scene = new Scene(new Test(course));
                    db.setDragView(scene.snapshot(null));
                }
                event.consume();
            });
            return row ;
        });
        filteredList = new FilteredList<>(getData());

        TextField filterInput = new TextField();
        filterInput.setPromptText("חפש קורס...");
        filterInput.textProperty().addListener(obs->{
            String filter = filterInput.getText();
            if(filter == null || filter.length() == 0) {
                filteredList.setPredicate(s -> true);
            }
            else {
                filteredList.setPredicate(s -> s.name.contains(filter));
            }
        });
        filterInput.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        filterInput.setStyle("-fx-focus-color: transparent; -fx-background-color: -fx-text-box-border, -fx-control-inner-background;");//"; -fx-text-box-border: transparent");
        table.setItems(filteredList);
        table.setStyle("-fx-focus-color: lightgrey; -fx-faint-focus-color: transparent;");
        this.getChildren().addAll(filterInput,table);
    }
    public ObservableList<Item> getData() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        for (Logic.Course course:manager.courseloader.getCourses().values()) {
            items.add(new Item(manager,course));
        }
        return items;
    }

}
