package GUI.Components;

import db.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class CoursesTable extends javafx.scene.control.TableView<Item> {
    TableColumn<Item,CheckBox> take;
    TableColumn<Item,String> name;
    TableColumn<Item,Integer> study;
    TableColumn<Item,CheckBox> pref;
    TableColumn<Item,Button> connections;


    public CoursesTable() {
        take = new TableColumn<>("");
        take.setCellValueFactory(new PropertyValueFactory<>("take"));
        take.setStyle("-fx-alignment: CENTER-RIGHT");
        take.setPrefWidth(40);
        name = new TableColumn<>("שם הקורס");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setStyle("-fx-alignment: CENTER-RIGHT");
        study = new TableColumn<>("ימי למידה");
        study.setCellValueFactory(new PropertyValueFactory<>("study"));
        study.setStyle("-fx-alignment: CENTER-RIGHT");
        pref = new TableColumn<>("העדפת שיבוץ");
        pref.setCellValueFactory(new PropertyValueFactory<>("pref"));
        pref.setStyle("-fx-alignment: CENTER-RIGHT");
        connections = new TableColumn<>("קשרים");
        connections.setCellValueFactory(new PropertyValueFactory<>("connections"));
        connections.setStyle("-fx-alignment: CENTER-RIGHT");
        this.setPrefWidth(395);
        this.setItems(getData());
        this.getColumns().addAll(connections,pref,study,name,take);
        this.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                Dragboard db = row.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(name.getCellData(row.getIndex()));
                db.setContent(content);
                db.setDragView(row.snapshot(null,null));
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
        items.add(new Item(new Course(234141, "קומבינטוריקה למדעי המחשב", 3)));
        items.add(new Item(new Course(234123, "מערכות הפעלה", 4)));
        items.add(new Item(new Course(236353, "אוטומטים ושפות פורמליות", 3)));
        items.add(new Item(new Course(104032, "חשבון אינפיניטסימלי 2מ'", 5)));
        return items;
    }

}
