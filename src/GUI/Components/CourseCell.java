package GUI.Components;

import javafx.event.Event;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CourseCell extends ListCell<String> {
    private HBox midHBox = new HBox();
    private HBox bottomHBox = new HBox();
    private VBox vbox = new VBox();
    private Label courseName = new Label("(empty)");
    private Label courseNum = new Label("(empty)");
    private CheckBox checkExamNeeded = new CheckBox();
    private ChoiceBox<String> studyDayChoice = new ChoiceBox<>();
    private RadioButton prefEarly = new RadioButton("מוקדם");
    private RadioButton prefLate = new RadioButton("מאוחר");
    private RadioButton prefAuto = new RadioButton("אוטומטי");
    private ToggleGroup group = new ToggleGroup();

    private void initRadioButtons(Pane pane){
        prefEarly.setToggleGroup(group);
        prefLate.setToggleGroup(group);
        prefAuto.setToggleGroup(group);
        prefAuto.setSelected(true);
        VBox radioVBox = new VBox(prefEarly,prefLate,prefAuto);
        radioVBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        bottomHBox.getChildren().addAll(pane, radioVBox, new Label("העדפת שיבוץ: "));
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
    }
    CourseCell() {
        super();
        HBox topHBox = new HBox();
        Pane pane = new Pane();
        studyDayChoice.getItems().addAll("אוטומטי","0","1","2","3","4","5");
        studyDayChoice.setValue("אוטומטי");
        checkExamNeeded.setSelected(false);
        HBox.setHgrow(pane, Priority.ALWAYS);
        topHBox.getChildren().addAll(checkExamNeeded, pane, courseNum,courseName);
        Label studyDayLabel = new Label("מס' ימים ללמידה: ");
        midHBox.getChildren().addAll(pane, studyDayChoice, studyDayLabel);
        midHBox.setAlignment(Pos.CENTER_RIGHT);
        vbox.setOnDragDetected(event->{
            Dragboard db = vbox.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(courseName.getText() + "_" + courseNum.getText());
            db.setContent(content);
            event.consume();
        });
        vbox.setOnDragOver(event -> {
            if (event.getGestureSource() != vbox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        vbox.getChildren().addAll(topHBox);
        checkExamNeeded.setOnAction(event-> {
            if (checkExamNeeded.isSelected()) {
                vbox.getChildren().addAll(midHBox,bottomHBox);
            }
            else {
                vbox.getChildren().removeAll(midHBox,bottomHBox);
            }
        });
        initRadioButtons(pane);

    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            setGraphic(null);
        } else {
            String courseNameInput = item.split("-")[0];
            String courseNumInput = item.split("-")[1];
            courseName.setText(courseNameInput + "-");
            courseNum.setText(courseNumInput);
            setGraphic(vbox);
            //bottomHBox.setVisible(false);
            //setGraphic(bottomHBox);
        }
    }
}

