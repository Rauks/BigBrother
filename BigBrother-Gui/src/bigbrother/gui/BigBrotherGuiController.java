/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class BigBrotherGuiController implements Initializable {
    @FXML
    public ListView classesList;
    
    @FXML
    public TreeView classesData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Scanner scanner = new Scanner("test-jars/TP-AIHM.jar");

            List<Class> classes = scanner.getClasses();
            ObservableList<Class> observablesClasses = FXCollections.observableList(classes);
            this.classesList.setItems(observablesClasses);
            for(Class classe : classes){
                System.out.println(classe);
            }
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
