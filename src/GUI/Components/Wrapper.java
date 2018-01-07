package GUI.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;


public class Wrapper extends ScrollPane {
    Toolbar toolbar;
    VBox vbox;
    public Wrapper() {
        Manager manager = new Manager();
        toolbar = new Toolbar(this);
        vbox = new VBox();
        vbox.setStyle("-fx-background-image: url(\"/background.png\");");
        vbox.getChildren().addAll(toolbar, manager);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 15, 15));
        this.setContent(vbox);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    }
    public void refreshManager() {
        vbox.getChildren().remove(1);
        vbox.getChildren().add(1, new Manager());

    }
}
