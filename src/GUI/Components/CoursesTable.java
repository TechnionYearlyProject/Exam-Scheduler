package GUI.Components;
import Logic.Course;
import Logic.EMoed;
import Logic.Exceptions.IllegalDaysBefore;
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

/**
 * @author dorbartov, roeyashkenazy
 * @date 10/01/2018
 * This class creates the courses management interface, including the buttons.
 * The course table supports dragging courses to the calendars (to force an exam),
 * highlighting the course's exams by hovering its name and more.
 */
public class CoursesTable extends VBox{
    private TableColumn<Item,CheckBox> takeCol;
    private TableColumn<Item,String> nameCol;
    private TableColumn<Item,String> studyCol;
    private TableColumn<Item,CheckBox> prefCol;
    private TableColumn<Item,Label> connectionsCol;
    private TableView<Item> table = new TableView<>();
    private FilteredList<Item> filteredList;
    private TextField filterInput;
    Manager manager;
    private boolean scheduled;
    HBox hbox;
    ObservableList<Item> items;

    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the row factory for the table- this factory
     * is used by JavaFX to make our course rows with dragging and hovering.
     */
    private void initTableRowFactory(){
        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (row.getItem() == null)
                    return;
                if(!scheduled) {
                    if (takeCol.getCellData(row.getIndex()).isSelected()) {
                        Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        String course_str = (nameCol.getCellData(row.getIndex())).split(" - ")[0];
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
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the table parameters.
     */
    private void initTableParameters(){
        table.setEditable(true);
        table.setPrefWidth(500);
        table.setPrefHeight(666);
        table.setItems(getData());
        table.getColumns().addAll(connectionsCol,prefCol,studyCol,nameCol,takeCol);
        initTableRowFactory();
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the study days column in the table
     */
    private void initStudyDayColumn(){
        studyCol = new TableColumn<>("ימי למידה");
        studyCol.setCellValueFactory(new PropertyValueFactory<>("study"));
        studyCol.setStyle("-fx-alignment: CENTER-RIGHT");
        studyCol.setPrefWidth(72);
        studyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        studyCol.setOnEditCommit(event -> {
            try {
                manager.courseloader.getCourse(event.getRowValue().getCourseID()).setDaysBefore(
                        Integer.parseInt(event.getNewValue()));
                event.getRowValue().setStudy(event.getNewValue());
            } catch (IllegalDaysBefore e) {
                new AlertBox(AlertType.ERROR,"מספר ימי הלמידה שהוזן אינו חוקי.",null);
                table.refresh();
            }
        });
    }
    /**
     * @author roeyashkenazy
     * @date 15/01/2018
     * builds a filtered list and a search button for the table.
     */
    private void initFilteredList(){
        filteredList = new FilteredList<>(getData());
        filterInput = new TextField();
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
        filterInput.setStyle("-fx-focus-color: transparent; -fx-background-color:" +
                " -fx-text-box-border, -fx-control-inner-background;");
    }
    /**
     * @author dorbartov
     * @date 16/01/2018
     * initializes the course buttons (save,add,remove) for the table.
     */
    private void initCourseButtons(){
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png",
                this::saveFunction,40,160);
        save_button.setCircular();
        CustomButton add_button = new CustomButton("הוסף קורס", "/add_icon.png",
                this::AddFunction,40,160);
        add_button.setCircular();
        CustomButton remove_button = new CustomButton("הסר קורס", "/remove_icon.png",
                this::removeFunction,40,160);
        remove_button.setCircular();
        hbox = new HBox();
        hbox.setAlignment(Pos.TOP_RIGHT);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(save_button, remove_button, add_button);
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the name column in the table
     */
    private void initNameColumn(){
        nameCol = new TableColumn<>("שם הקורס");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER-RIGHT");
        nameCol.setPrefWidth(212);
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the 'take exam' column in the table
     */
    private void initTakeExamColumn(){
        takeCol = new TableColumn<>("");
        takeCol.setCellValueFactory(new PropertyValueFactory<>("take"));
        takeCol.setStyle("-fx-alignment: CENTER-RIGHT");
        takeCol.setPrefWidth(40);
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the preference column in the table
     */
    private void initPreferenceColumn(){
        prefCol = new TableColumn<>("העדפת שיבוץ");
        prefCol.setCellValueFactory(new PropertyValueFactory<>("pref"));
        prefCol.setStyle("-fx-alignment: CENTER-RIGHT");
        prefCol.setPrefWidth(100);
    }
    /**
     * @author roeyashkenazy
     * @date 19/01/2018
     * initializes the connections column in the table
     */
    private void initConnectionsColumn(){
        connectionsCol = new TableColumn<>("קשרים");
        connectionsCol.setCellValueFactory(new PropertyValueFactory<>("connections"));
        connectionsCol.setStyle("-fx-alignment: CENTER-RIGHT");
        connectionsCol.setPrefWidth(56);
    }
    /**
     * @author dorbartov, roeyashkenazy
     * @date 10/01/2018
     * @param parent used to connect the table to the manager and so the entire system.
     * initializes the course table columns.
     */
    public CoursesTable(Manager parent) {
        this.getStylesheets().add("/coursetable_style.css");
        this.setSpacing(10);
        table.setStyle("-fx-focus-color: lightgrey; -fx-faint-focus-color: transparent;");
        manager = parent;
        scheduled = false;
        initTakeExamColumn();
        initNameColumn();
        initStudyDayColumn();
        initPreferenceColumn();
        initConnectionsColumn();
        initTableParameters();
        initFilteredList();
        table.setItems(filteredList);
        initCourseButtons();
        this.getChildren().addAll(filterInput,table,hbox);
    }

    /**
     * @author roeyashkenazy
     * @date 14/01/2018
     * supports hovering - moving the mouse over a course in the table should highlight
     * that course in the calendars.
     */
    private void hover(Moed moed, boolean brighten, TableRow<Item> row){
        if (manager.been_scheduled && (row.getItem() != null)) {
            String courseNum = (nameCol.getCellData(row.getIndex())).split(" - ")[0];
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

    /**
     * @author dorbartov
     * @date 10/01/2018
     * @return the initial data to be inserted into the courses table
     */
    private ObservableList<Item> getData() {
        items = FXCollections.observableArrayList();
        for (Logic.Course course:manager.courseloader.getCourses().values()) {
            items.add(new Item(manager,course));
        }
        return items;
    }

    /**
     * @author roeyashkenazy
     * @date 13/01/2018
     * signals that the scheduling algorithm was executed.
     */
    public void setScheduled(boolean value){
        scheduled = value;
    }

    /**
     * @author dorbartov
     * @date 11/01/2018
     */
    private void removeFunction() {
        if (table.getSelectionModel().getSelectedItem() == null)
            return;
        Integer courseID = table.getSelectionModel().getSelectedItem().getCourseID();
        new AlertBox(AlertType.CONFIRM, "האם אתה בטוח שברצונך למחוק את קורס מספר " +
                String.format("%06d",courseID) + "?", () -> {
            if (manager.scheduleA != null) {
                manager.scheduleA.unassignCourse(manager.courseloader.getCourse(courseID));
            }
            if (manager.scheduleB != null) {
                manager.scheduleB.unassignCourse(manager.courseloader.getCourse(courseID));
            }
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

    /**
     * @author dorbartov
     * @date 20/01/2018
     * saves changes made to coursestable to the DB
     */
    public void saveFunction() {
        new AlertBox(AlertType.CONFIRM, "האם ברצונך לשמור את מצב הקורסים הנוכחי למסד הנתונים?", ()->{
            if (manager.scheduleA != null)
                manager.dbnotifier.save(manager.courseloader,manager.semester);
            else
                manager.dbnotifier.save(manager.courseloader,manager.semester);
        });
        /*Semester to_write = wrapper.manager.semester;
        wrapper.manager.db.saveSemester(wrapper.manager.semesterYear, wrapper.manager.semesterName);*/
    }

}
