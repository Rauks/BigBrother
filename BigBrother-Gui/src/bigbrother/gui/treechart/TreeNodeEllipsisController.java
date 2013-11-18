/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.treechart;

import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableMethod;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class TreeNodeEllipsisController implements Initializable {
    @FXML
    public TitledPane titlePane;
    
    @FXML
    public ListView methodsList;
        
    private ObservableClass classe;
    private ObservableList<ObservableMethod> observablesMethods;
    private SimpleStringProperty title;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.observablesMethods = FXCollections.observableArrayList();
        this.methodsList.setItems(this.observablesMethods);
        
        this.title = new SimpleStringProperty("Element");
        this.titlePane.textProperty().bind(this.title);
    }    
    
    public void setObservation(ObservableClass classe){
        this.classe = classe;
        this.title.setValue(this.classe.getName());
        this.observablesMethods.addAll(this.classe.getMethods());
    }
}
