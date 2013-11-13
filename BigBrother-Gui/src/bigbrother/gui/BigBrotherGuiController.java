/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class BigBrotherGuiController implements Initializable {
    @FXML
    public Pane rootPane;
    @FXML
    public ListView classesList;
    @FXML
    public Pane dataPane;
    
    private FileChooser jarFileChooser;
    private ObservableList<Class> observablesClasses;

    @FXML
    public void handleOpen(){
        File jarFile = this.jarFileChooser.showOpenDialog(this.getScene().getWindow());
        this.doScan(jarFile.getPath());
    }
    
    public void doScan(String jarFilePath){
        this.observablesClasses.clear();
        try {
            Scanner scanner = new Scanner(jarFilePath);
            this.observablesClasses.addAll(scanner.getClasses());
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    @FXML
    public void handleClose(){
        System.exit(0);
    }
    
    protected Scene getScene(){
        return this.rootPane.getScene();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.jarFileChooser = new FileChooser();
        this.jarFileChooser.setTitle("Explorer");
        this.jarFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.jarFileChooser.getExtensionFilters().add(
                new ExtensionFilter("Executables Java", "*.jar")
        );
        
        this.observablesClasses = FXCollections.observableArrayList();
        this.classesList.setItems(this.observablesClasses);
    }    
    
}
