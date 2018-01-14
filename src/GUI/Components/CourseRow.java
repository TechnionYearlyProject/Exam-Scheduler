package GUI.Components;

import javafx.scene.control.TableRow;

public class CourseRow<Item> extends TableRow<Item> {
    private Integer counter = 0;
    public Integer getCounter(){
        return counter;
    }
    public void increaseCounter(){
        counter++;
    }
    public void decreaseCounter(){
        if(counter!=0){
            counter--;
        }
    }
}
