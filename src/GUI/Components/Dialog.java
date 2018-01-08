package GUI.Components;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Dialog {

    public Dialog() {
        VBox vbox = new VBox();
        Stage stage = new Stage();
        Scene scene = new Scene(vbox, 300,100);
        stage.setScene(scene);
        Text txt = new Text("abc");
        vbox.getChildren().add(txt);
        stage.show();


    }
}