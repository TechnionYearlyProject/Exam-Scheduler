package GUI.Components;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class CourseCell extends ListCell<String> {
    private HBox hbox = new HBox();
    private Label courseName = new Label("(empty)");
    private Label courseNum = new Label("(empty)");
    private CheckBox checkExamNeeded = new CheckBox();
    private Button button = new Button("(>)");
    private String lastItem;

    public CourseCell() {
        super();
        Pane pane = new Pane();
        checkExamNeeded.setSelected(true);
        hbox.getChildren().addAll(checkExamNeeded, pane, courseNum, courseName);
        HBox.setHgrow(pane, Priority.ALWAYS);
        checkExamNeeded.setOnMouseClicked(event-> System.out.println("here"));
        button.setOnAction(event ->
                System.out.println(lastItem + " : " + event));
        hbox.setOnDragDetected(event->{
            Dragboard db = hbox.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(courseName.getText() + "_" + courseNum.getText());
            db.setContent(content);
            event.consume();
        });
        hbox.setOnDragOver(event -> {
            if (event.getGestureSource() != hbox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        hbox.setOnDragEntered(Event::consume);


    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            String courseNameInput = item.split("-")[0];
            String courseNumInput = item.split("-")[1];
            courseName.setText(courseNameInput + "-");
            courseNum.setText(courseNumInput);
            setGraphic(hbox);
        }
    }
}

