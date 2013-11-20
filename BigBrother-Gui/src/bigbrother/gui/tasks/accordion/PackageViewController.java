/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.accordion;

import bigbrother.core.model.ObservableClass;
import bigbrother.gui.BigBrotherGuiController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class PackageViewController implements Initializable {
    @FXML
    public ListView classesList;
    
    @FXML
    public TitledPane pane;
    
    private ObservableList<ObservableClass> observablesClasses;
    private SimpleStringProperty title;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.observablesClasses = FXCollections.observableArrayList();
        this.classesList.setItems(this.observablesClasses);
        
        this.title = new SimpleStringProperty();
        this.pane.textProperty().bind(this.title);
    }
    
    public void setParentController(final BigBrotherGuiController controler){
        this.classesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    System.out.println(newValue);
                    controler.loadTreeChart((ObservableClass) newValue);
                }
                else{
                    controler.unloadTreeChart();
                }
            }
        });
        this.classesList.setCellFactory(new Callback<ListView<ObservableClass>, ListCell<ObservableClass>>(){
            @Override
            public ListCell<ObservableClass> call(ListView<ObservableClass> p) {
                final Tooltip tooltip = new Tooltip();
                final ListCell<ObservableClass> cell = new ListCell<ObservableClass>() {
                    @Override
                    public void updateItem(ObservableClass item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            this.setText(item.getSimpleName());

                            switch(item.getType()){
                                case ANNOTATION:
                                    this.setTextFill(Color.DARKBLUE);
                                    break;
                                case ENUMERATION:
                                    this.setTextFill(Color.DARKGREEN);
                                    break;
                                case INTERFACE:
                                    this.setTextFill(Color.DARKORANGE);
                                    break;
                                case CLASS_ANONYMOUS:
                                case CLASS_SYNTHETIC:
                                    this.setTextFill(Color.GRAY);
                                    break;
                                default:
                                    this.setTextFill(Color.BLACK);
                                    break;
                            }

                            tooltip.setText(item.getType().getName());
                            this.setTooltip(tooltip);
                        }
                    }
                }; // ListCell
                return cell;
            }
        });
    }
    
    public void setTitle(String title){
        this.title.set(title);
    }
    
    public void addObservableClass(ObservableClass classe){
        this.observablesClasses.add(classe);
    }
    
    public void removeObservableClass(ObservableClass classe){
        this.observablesClasses.remove(classe);
    }
    
    public void clearObservablesClass(){
        this.observablesClasses.clear();
    }
}
