package GUI.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Super extends VBox{
    public Super() {
        Major major = new Major();
        VBox title_box = new VBox();
        title_box.setAlignment(Pos.TOP_RIGHT);

        //title_box.setSpacing(5);
        //title_grid.setPadding(new Insets(10,10,10,10));
        Text title = new Text("מערכת שיבוץ מבחנים");
        title.setTextAlignment(TextAlignment.RIGHT);
        Text semi_title = new Text("הפקולטה למדעי המחשב בטכניון");
        semi_title.setTextAlignment(TextAlignment.RIGHT);
        title_box.getChildren().addAll(title, semi_title);

        HBox image_box = new HBox();
        image_box.setAlignment(Pos.TOP_RIGHT);
        image_box.setSpacing(10);
        image_box.setPadding(new Insets(10, 10, 10, 10));
        Image image = new Image("/technion_logo.png");
        Label image_label = new Label();
        image_label.setGraphic(new ImageView(image));
        image_box.getChildren().addAll(title_box, image_label);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png");
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png");
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png");
        CustomButton export_button = new CustomButton("ייצוא", "/export_icon.png");

        hbox.getChildren().addAll(export_button, clean_button, save_button, schedule_button);
        title.setStyle("-fx-font-size: 30pt");
        semi_title.setStyle("-fx-font-size: 15pt");
        this.getChildren().addAll(image_box, major, hbox);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(20);
        this.setPadding(new Insets(20, 10, 20, 20));
        this.setMaxWidth(1536);
    }
}
