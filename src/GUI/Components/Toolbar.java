package GUI.Components;
import Logic.Exceptions.IllegalRange;
import Logic.Schedule;
import Output.CSVFileWriter;
import Output.Exceptions.ErrorOpeningFile;
import Output.XMLFileWriter;
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
        CustomButton schedule_button = new CustomButton("שיבוץ", "/schedule_icon.png", this::scheduleFunction,40,150);
        schedule_button.setCircular();
        CustomButton clean_button = new CustomButton("ניקוי", "/clean_icon.png", this::cleanFunction,40,150);
        clean_button.setCircular();
        CustomButton save_button = new CustomButton("שמור", "/save_icon.png", this::saveFunction,40,150);
        save_button.setCircular();
        export_button = new CustomButton("ייצוא", "/export_icon.png", this::exportFunction,40,150);
        export_button.setCircular();
        this.setAlignment(Pos.TOP_RIGHT);
        this.setSpacing(10);
        this.getChildren().addAll(export_button, clean_button, save_button, schedule_button, title_box);
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

    public void exportFunction() {
        export_button.setOnMouseClicked(event->{
            if(!wrapper.manager.been_scheduled){
                new AlertBox(AlertType.ERROR, "לא ניתן לייצא לפני שיבוץ.", null);
                return;
            }
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            CustomButton CSVButton = new CustomButton("יצא בתור CSV",null, ()->{
                CSVFileWriter writer = new CSVFileWriter();
                try {
                    writer.write("output.csv",wrapper.manager.scheduleA.getSchedulableDays(),wrapper.manager.courseloader);
                    stage.close();
                } catch (ErrorOpeningFile errorOpeningFile) {
                    new AlertBox(AlertType.ERROR, "בעיה ביצירת הקובץ - אנא בדקו שהקובץ אינו פתוח", null);
                }
            }, 30,110);
            CSVButton.setRectangle();
            CustomButton XMLButton = new CustomButton("יצא בתור XML",null, ()->{
                XMLFileWriter writer = new XMLFileWriter();
                try {
                    writer.write("output.xml",wrapper.manager.scheduleA.getSchedulableDays(),wrapper.manager.courseloader);
                    stage.close();
                } catch (ErrorOpeningFile errorOpeningFile) {
                    new AlertBox(AlertType.ERROR, "בעיה ביצירת הקובץ - אנא בדקו שהקובץ אינו פתוח", null);
                }
            }, 30 ,110);
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
        new LoadingBox(()-> {
            try {
                wrapper.manager.scheduleA = new Schedule(wrapper.manager.Astart, wrapper.manager.Aend, wrapper.manager.occupiedA);
                wrapper.manager.scheduleB = new Schedule(wrapper.manager.Bstart, wrapper.manager.Bend, wrapper.manager.occupiedB);
            } catch (IllegalRange illegalRange) {
                new AlertBox(AlertType.ERROR, "טווח התאריכים אינו חוקי", null);
                return;
            }
            try {
                wrapper.manager.scheduleA.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistA, null);
                wrapper.manager.scheduleB.produceSchedule(wrapper.manager.courseloader, wrapper.manager.constraintlistB, null);
            } catch (Schedule.CanNotBeScheduledException e) {
                new AlertBox(AlertType.ERROR, "השיבוץ נכשל. נסו להסיר העדפות " +
                        "או להגדיל את טווח התאריכים.", null);
                return;
            }
            wrapper.updateSchdule(wrapper.manager.scheduleA, wrapper.manager.scheduleB);
            wrapper.manager.coursetable.setScheduled(true);
            for (Day day : wrapper.manager.A.schedule.days.values()) {
                day.disableBlocking();
            }
            for (Day day : wrapper.manager.B.schedule.days.values()) {
                day.disableBlocking();
            }
            wrapper.manager.A.picker1.disable();
            wrapper.manager.A.picker2.disable();
            wrapper.manager.B.picker1.disable();
            wrapper.manager.B.picker2.disable();
        });
    }
}
