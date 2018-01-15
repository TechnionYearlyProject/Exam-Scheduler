package GUI.Components;

import Logic.Schedule;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.time.LocalDate;

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
        title_box.setPrefWidth(996);
        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png",()->scheduleFunction());
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png",()->cleanFunction());
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png",()->saveFunction());
        CustomButton export_button = new CustomButton("ייצוא", "/export_icon.png",null);
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(export_button, clean_button, save_button, schedule_button, title_box);
    }
    public void cleanFunction() {
        AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", ()->parent.cleanData());
    }

    public void saveFunction() {
       // AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לשמור את המצב הנוכחי?", ()->parent.saveAllData());
    }

    public void scheduleFunction(){
        LoadingBox alert = new LoadingBox(()->  {
            try {
                Logic.Schedule scheduleA = new Schedule(LocalDate.of(2018, 1, 14), LocalDate.of(2018, 2, 23), null);
                Logic.Schedule scheduleB = new Schedule(LocalDate.of(2018, 1, 14), LocalDate.of(2018, 2, 23), null);
                scheduleA.produceSchedule(parent.manager.courseloader, parent.manager.constraintlistA, null);
                scheduleB.produceSchedule(parent.manager.courseloader, parent.manager.constraintlistB, scheduleA);
                parent.updateSchdule(scheduleA, scheduleB);
            } catch (Exception e){}
        });


    }
}
