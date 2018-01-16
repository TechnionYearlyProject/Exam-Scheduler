package GUI.Components;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Item {
    CheckBox take;
    String name;
    SimpleStringProperty study;
    ChoiceBox<String> pref;
    Label connections;
    public Item(Logic.Course course) {
        take = new CheckBox();
        take.setSelected(true);
        name = String.format("%06d",course.getCourseID()) + " - " + course.getCourseName();
        study = new SimpleStringProperty((new Integer(course.getCreditPoints().intValue()).toString()));
        pref = new ChoiceBox<String>();
        pref.getItems().addAll("אוטומטי","סוף תקופה","תחילת תקופה");
        pref.setValue("אוטומטי");
        pref.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pref.setPadding(new Insets(0,-7,0,0));
        connections = new Label();
        connections.setGraphic(new ImageView(new Image("/connection_icon.png")));
        connections.addEventFilter(MouseEvent.MOUSE_CLICKED, mouse_event -> {
            if (mouse_event.getButton()!= MouseButton.PRIMARY)
                return;
            Connections connections = new Connections(course.getCourseID());
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(connections, 150, 200);
            stage.setScene(scene);
            stage.setX(mouse_event.getScreenX());
            stage.setY(mouse_event.getScreenY());
            stage.getIcons().add(new Image("/app_icon.png"));
            stage.focusedProperty().addListener(event -> {
                if (!stage.isFocused()) {
                    stage.close();
                }
            });

            stage.show();
        });
    }
    public String getName() {
        return name;
    }
    public CheckBox getTake() {
        return take;
    }
    public String getStudy() {
        return study.get();
    }
    public void setStudy(String new_study) {
        study.set(new_study);
    }
    public ChoiceBox<String> getPref() {
        return pref;
    }
    public Label getConnections() {
        return connections;
    }

    public Integer getCourseID()
    {
        return Integer.parseInt(name.split(" - ")[0]);
    }


}
