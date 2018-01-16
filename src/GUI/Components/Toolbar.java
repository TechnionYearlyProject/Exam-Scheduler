package GUI.Components;
import Logic.Schedule;
import Logic.WriteScheduleToDB;
import Output.CSVFileWriter;
import Output.CSVFileWriter;
import Output.Exceptions.ErrorOpeningFile;
import db.Semester;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        title_box.setPrefWidth(956);
        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png", this::scheduleFunction,40,160);
        schedule_button.setCircular();
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png", this::cleanFunction,40,160);
        clean_button.setCircular();
        CustomButton guide_button = new CustomButton("מדריך", "/guide_icon.png", this::guideFunction,40,160);
        guide_button.setCircular();
        export_button = new CustomButton("ייצוא", "/export_icon.png", this::exportFunction,40,160);
        export_button.setCircular();
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(guide_button, export_button, clean_button, schedule_button, title_box);
    }
    public void cleanFunction() {
        new AlertBox(AlertType.CONFIRM, "האם ברצונך לנקות את התוכנית?", () -> wrapper.manager.cleanData());
        for (Day day : wrapper.manager.A.schedule.days.values()){
            day.enableBlocking();
        }
        for (Day day : wrapper.manager.B.schedule.days.values()){
            day.enableBlocking();
        }
        wrapper.manager.A.picker1.enable();
        wrapper.manager.A.picker2.enable();
        wrapper.manager.B.picker1.enable();
        wrapper.manager.B.picker2.enable();
    }

    public void saveFunction() {
        // AlertBox alert = new AlertBox(AlertType.CONFIRM, "האם ברצונך לשמור את המצב הנוכחי?", ()->parent.saveAllData());
        Semester to_write = wrapper.manager.semester;
        wrapper.manager.db.saveSemester(wrapper.manager.semesterYear, wrapper.manager.semesterName);
    }

    public void guideFunction() {
        Path curr = Paths.get("");
        String s = curr.toAbsolutePath().toString() + "\\documentation\\Manual.docx";
        File file = new File(s);
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e){
            System.out.print(e);
        }
    }

    public void exportFunction() {
        export_button.setOnMouseClicked(event->{
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            CustomButton CSVButton = new CustomButton("יצא בתור CSV",null, ()->{
                CSVFileWriter writer = new CSVFileWriter();
                try {
                    writer.write("temp.csv",wrapper.manager.scheduleA.getSchedulableDays(),wrapper.manager.courseloader);
                } catch (ErrorOpeningFile errorOpeningFile) {
                    errorOpeningFile.printStackTrace();
                }
            }, 30,110);
            CSVButton.setRectangle();
            CustomButton XMLButton = new CustomButton("יצא בתור XML",null, stage::close, 30 ,110);
            XMLButton.setRectangle();
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
        new LoadingBox(()->  {
            try {
                wrapper.manager.scheduleA = new Schedule(wrapper.manager.Astart,wrapper.manager.Aend,wrapper.manager.occupiedA);
                wrapper.manager.scheduleB = new Schedule(wrapper.manager.Bstart,wrapper.manager.Bend,wrapper.manager.occupiedB,5);
                wrapper.manager.scheduleA.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistA, null);
                //wrapper.manager.scheduleB.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistB, wrapper.manager.scheduleB);
                wrapper.updateSchdule(wrapper.manager.scheduleA, wrapper.manager.scheduleB);
            } catch (Exception ignored) {
                System.out.println(ignored);
            }});
        wrapper.manager.coursetable.setScheduled(true);
        for (Day day : wrapper.manager.A.schedule.days.values()){
            day.disableBlocking();
        }
        for (Day day : wrapper.manager.B.schedule.days.values()){
            day.disableBlocking();
        }
        wrapper.manager.A.picker1.disable();
        wrapper.manager.A.picker2.disable();
        wrapper.manager.B.picker1.disable();
        wrapper.manager.B.picker2.disable();
    }
}
