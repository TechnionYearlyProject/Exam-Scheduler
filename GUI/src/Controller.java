package src;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    ChoiceBox<String> yearChoice;
    public void test(){
        System.out.println("bla bla bla");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(Integer i=2010;i<2020;i++){
            yearChoice.getItems().add(i.toString()); 
        }

    }
}
