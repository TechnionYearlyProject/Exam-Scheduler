package GUI.Components;
import Logic.Schedule;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Toolbar extends HBox{
    Wrapper wrapper;
    CustomButton export_button;
    public Toolbar(Wrapper parent) {
        wrapper = parent;
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
        export_button = new CustomButton("ייצוא", "/export_icon.png",()->exportFunction());
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(export_button, clean_button, save_button, schedule_button, title_box);
    }
    public void cleanFunction() {
        AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", ()->wrapper.manager.cleanData());
    }

    public void saveFunction() {
       // AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לשמור את המצב הנוכחי?", ()->parent.saveAllData());
    }

    public void exportFunction() {
        export_button.setOnMouseClicked(event->{
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            CustomButton CSVButton = new CustomButton("יצא בתור CSV","/export_icon.png", stage::close);
            CustomButton XMLButton = new CustomButton("יצא בתור XML","/export_icon.png",null);

            Scene scene = new Scene(new VBox(CSVButton,XMLButton));
            stage.setScene(scene);
            stage.setX(event.getScreenX());
            stage.setY(event.getScreenY());
            stage.getIcons().add(new Image("/app_icon.png"));
            stage.focusedProperty().addListener(event2 -> {
                if (!stage.isFocused()) {
                    stage.close();
                }
            });

            stage.show();
        });
    }

    public void scheduleFunction(){
        if (wrapper.manager.been_scheduled) {
            new AlertBox(AlertType.INFO,"לא ניתן לשבץ על לוח קיים. לחצו ניקוי ונסו שוב.",null);
            return;
        }
        wrapper.manager.been_scheduled = true;
        LoadingBox alert = new LoadingBox(()->  {
            try {
                Logic.Schedule scheduleA = new Schedule(wrapper.manager.Astart,wrapper.manager.Aend,wrapper.manager.occupiedA);
                Logic.Schedule scheduleB = new Schedule(wrapper.manager.Bstart,wrapper.manager.Bend,wrapper.manager.occupiedB);
                scheduleA.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistA, null);
                //scheduleB.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistB, scheduleA);
                wrapper.updateSchdule(scheduleA, scheduleB);
            } catch (Exception e){}
        });


    }
}
