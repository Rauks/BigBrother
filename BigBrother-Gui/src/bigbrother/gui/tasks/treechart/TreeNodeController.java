/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.treechart;

import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableClassException;
import bigbrother.core.model.ObservableField;
import bigbrother.core.model.ObservableMethod;
import bigbrother.gui.BigBrotherGuiController;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class TreeNodeController implements Initializable {
    @FXML
    public TitledPane titlePane;
    @FXML
    public ListView fieldsList;
    @FXML
    public ListView methodsList;
        
    private ObservableClass classe;
    private ObservableList<ObservableField> observablesFields;
    private ObservableList<ObservableMethod> observablesMethods;
    private SimpleStringProperty title;
    private SimpleStringProperty titlePre;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.observablesMethods = FXCollections.observableArrayList();
        this.methodsList.setItems(this.observablesMethods);
        this.methodsList.setCellFactory(new Callback<ListView<ObservableMethod>, ListCell<ObservableMethod>>(){
            @Override
            public ListCell<ObservableMethod> call(ListView<ObservableMethod> p) {
                final Tooltip tooltip = new Tooltip();
                final ListCell<ObservableMethod> cell = new ListCell<ObservableMethod>() {
                    @Override
                    public void updateItem(ObservableMethod item, boolean empty){
                        super.updateItem(item, empty);
                        if(!empty){
                            this.setText(item.getVisibility().getSymbol() + " " + item.getName() + "() : " + item.getReturnType().getSimpleName());
                            switch(item.getVisibility()){
                                case PRIVATE:
                                    this.setTextFill(Color.DARKRED);
                                    break;
                                case PUBLIC:
                                    this.setTextFill(Color.DARKGREEN);
                                    break;
                                case PROTECTED:
                                    this.setTextFill(Color.DARKORANGE);
                                    break;
                            }
                            
                            this.setUnderline(item.isStatic());
                            
                            tooltip.setText((item.isStatic()?"Statique ":"") + item.getVisibility().getName());
                            this.setTooltip(tooltip);
                            this.setCursor(Cursor.HAND);
                        }
                    }
                };
                return cell;
            }
        });
        
        this.observablesFields = FXCollections.observableArrayList();
        this.fieldsList.setItems(this.observablesFields);
        this.fieldsList.setCellFactory(new Callback<ListView<ObservableField>, ListCell<ObservableField>>(){
            @Override
            public ListCell<ObservableField> call(ListView<ObservableField> p) {
                final Tooltip tooltip = new Tooltip();
                final ListCell<ObservableField> cell = new ListCell<ObservableField>() {
                    @Override
                    public void updateItem(ObservableField item, boolean empty){
                        super.updateItem(item, empty);
                        if(!empty){
                            this.setText(item.getVisibility().getSymbol() + " " + item.getName() + " : " + item.getType().getSimpleName());
                            switch(item.getVisibility()){
                                case PRIVATE:
                                    this.setTextFill(Color.DARKRED);
                                    break;
                                case PUBLIC:
                                    this.setTextFill(Color.DARKGREEN);
                                    break;
                                case PROTECTED:
                                    this.setTextFill(Color.DARKORANGE);
                                    break;
                            }
                            
                            this.setUnderline(item.isStatic());
                            
                            tooltip.setText((item.isStatic()?"Statique ":"") + item.getVisibility().getName());
                            this.setTooltip(tooltip);
                            this.setCursor(Cursor.HAND);
                        }
                    }
                };
                return cell;
            }
        });
        
        this.title = new SimpleStringProperty("");
        this.titlePre = new SimpleStringProperty("");
        this.titlePane.textProperty().bind(this.titlePre.concat(this.title));
    }    
    
    /**
     * Set the class which the informations are used in the node.
     * 
     * @param classe The class observed.
     */
    public void setObservation(ObservableClass classe){
        this.classe = classe;
        this.title.setValue(this.classe.getSimpleName());
        try {
            this.observablesMethods.addAll(this.classe.getMethods());
            Collections.sort(this.observablesMethods, new Comparator<ObservableMethod>(){
                @Override
                public int compare(ObservableMethod t, ObservableMethod t1) {
                    if(t.isStatic() && !t1.isStatic()){
                        return -1;
                    }
                    if(!t.isStatic() && t1.isStatic()){
                        return 1;
                    }
                    return t.getName().compareTo(t1.getName());
                }
            });
        } catch (ObservableClassException ex) {
            Logger.getLogger(TreeNodeController.class.getName()).log(Level.INFO, null, ex);
            this.observablesMethods.clear();
            this.methodsList.setDisable(true);
        }
        try {
            this.observablesFields.addAll(this.classe.getFields());
            Collections.sort(this.observablesFields, new Comparator<ObservableField>(){
                @Override
                public int compare(ObservableField t, ObservableField t1) {
                    if(t.isStatic() && !t1.isStatic()){
                        return -1;
                    }
                    if(!t.isStatic() && t1.isStatic()){
                        return 1;
                    }
                    return t.getName().compareTo(t1.getName());
                }
            });
        } catch (ObservableClassException ex) {
            Logger.getLogger(TreeNodeController.class.getName()).log(Level.INFO, null, ex);
            this.observablesFields.clear();
            this.fieldsList.setDisable(true);
        }
    }
    
    public void setTitlePrefix(String prefix){
        this.titlePre.set(prefix);
    }
    
    public void setParentController(final BigBrotherGuiController controler){
        this.fieldsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ObservableField>() {
            @Override
            public void changed(ObservableValue observableObject, ObservableField oldValue, ObservableField newValue) {
                controler.loadTreeChart(newValue.getType());
            }
        });
        this.methodsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ObservableMethod>() {
            @Override
            public void changed(ObservableValue observableObject, ObservableMethod oldValue, ObservableMethod newValue) {
                controler.loadTreeChart(newValue.getReturnType());
            }
        });
    }
}
