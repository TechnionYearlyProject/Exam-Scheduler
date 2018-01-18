package GUI.Components;
import Logic.Course;
import Logic.Exceptions.IllegalDaysBefore;
import Logic.Exceptions.IllegalRange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CoursesTable extends VBox{
    private TableColumn<Item,CheckBox> take;
    private TableColumn<Item,String> name;
    private TableColumn<Item,String> study;
    private TableColumn<Item,CheckBox> pref;
    private TableColumn<Item,Label> connections;
    TableView<Item> table;
    private FilteredList<Item> filteredList;
    Manager manager;
    private boolean scheduled;
    HBox hbox;
    ObservableList<Item> items;
    public CoursesTable(Manager parent) {
        this.getStylesheets().add("/coursetable_style.css");
        manager = parent;
        scheduled = false;
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
                manager.courseloader.getCourse(event.getRowValue().getCourseID()).setDaysBefore(Integer.parseInt(event.getNewValue()));
                event.getRowValue().setStudy(event.getNewValue());
            } catch (IllegalDaysBefore e) {
                new AlertBox(AlertType.ERROR,"מספר ימי הלמידה שהוזן אינו חוקי.",null);
                table.refresh();
            }
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
        table.setPrefHeight(666);
        table.setItems(getData());
        table.getColumns().addAll(connections,pref,study,name,take);
        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (row.getItem() == null)
                    return;
                if(!scheduled) {
                    if (take.getCellData(row.getIndex()).isSelected()) {
                        Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        String course_str = (name.getCellData(row.getIndex())).split(" - ")[0];
                        content.putString(course_str);
                        db.setContent(content);
                        Course course = manager.courseloader.getCourse(Integer.parseInt(course_str));
                        Scene scene = new Scene(new Test(null, course,false));
                        db.setDragView(scene.snapshot(null));
                    }
                }
                event.consume();
            });
            row.setOnMouseEntered(event->{
                hover(manager.A,true,row);
                hover(manager.B,true,row);
            });
            row.setOnMouseExited(event->{
                hover(manager.A,false,row);
                hover(manager.B,false,row);
            });
            row.setOnScroll(event->{
                hover(manager.A,false,row);
                hover(manager.B,false,row);
            });
            return row;
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
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png", null,40,160);
        save_button.setCircular();
        CustomButton add_button = new CustomButton("הוסף קורס", "/add_icon.png", this::AddFunction,40,160);
        add_button.setCircular();
        CustomButton remove_button = new CustomButton("הסר קורס", "/remove_icon.png", this::removeFunction,40,160);
        remove_button.setCircular();
        hbox = new HBox();
        hbox.setAlignment(Pos.TOP_RIGHT);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(save_button, remove_button, add_button);
        //hbox.setPadding(new Insets(10,0,0,0));
        this.setSpacing(10);
        this.getChildren().addAll(filterInput,table,hbox);
    }
    private void hover(Moed moed, boolean brighten, TableRow<Item> row){
        if (row.getItem() == null)
            return;
        if (manager.been_scheduled) {
            String courseNum = (name.getCellData(row.getIndex())).split(" - ")[0];
            for (Day day : moed.schedule.days.values()) {
                for (Test test : day.testList) {
                    if (!test.course.getCourseID().equals(
                            Integer.parseInt(courseNum))) {
                        test.setColor(brighten);
                    }
                }
            }
        }
    }
    private ObservableList<Item> getData() {
        items = FXCollections.observableArrayList();
        for (Logic.Course course:manager.courseloader.getCourses().values()) {
            items.add(new Item(manager,course));
        }
        return items;
    }
    public void setScheduled(boolean value){
        scheduled = value;
    }

    private void removeFunction() {
        Integer courseID = table.getSelectionModel().getSelectedItem().getCourseID();
        new AlertBox(AlertType.CONFIRM, "האם אתה בטוח שברצונך למחוק את קורס מספר " + courseID.toString() + "?", () -> {
            manager.A.schedule.removeTest(courseID);
            manager.B.schedule.removeTest(courseID);
            manager.constraintlistA.removeConstraint(courseID);
            manager.constraintlistB.removeConstraint(courseID);
            manager.courseloader.removeCourseCompletely(courseID);
            Item to_remove = null;
            for (Item item : items) {
                if (item.getCourseID().equals(courseID)) {
                    to_remove = item;
                    break;
                }
            }
            items.remove(to_remove);
        });
    }


    private void AddFunction() {
        new AddCourse(this);
    }

}
