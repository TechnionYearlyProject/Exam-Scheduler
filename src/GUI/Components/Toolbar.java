package GUI.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Toolbar extends HBox{
    Wrapper parent;
    public Toolbar(Wrapper new_parent) {
        parent = new_parent;
        Text main_title = new Text("מערכת שיבוץ לוח מבחנים");
        main_title.setTextAlignment(TextAlignment.RIGHT);
        main_title.setStyle("-fx-font-size: 34pt; -fx-font-weight: bold");

        Text sub_title = new Text("הפקולטה למדעי המחשב בטכניון");
        sub_title.setTextAlignment(TextAlignment.RIGHT);
        sub_title.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold");

        VBox title_box = new VBox();
        title_box.setAlignment(Pos.TOP_RIGHT);
        title_box.getChildren().addAll(main_title, sub_title);
        title_box.setPrefWidth(890);

        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png",null);
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png",()->cleanFunction());
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png",null);
        CustomButton export_button = new CustomButton("ייצוא", "/export_icon.png",null);

        //this.setPadding(new Insets(10, 10, 10, 10));
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(export_button, clean_button, save_button, schedule_button, title_box);
    }
    public void cleanFunction()
    {
        AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", ()->parent.refreshManager());

    }
}
