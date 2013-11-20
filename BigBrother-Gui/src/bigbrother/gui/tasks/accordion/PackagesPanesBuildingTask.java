/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.accordion;

import bigbrother.core.model.ObservableClass;
import bigbrother.gui.BigBrotherGuiController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

/**
 *
 * @author Karl
 */
public class PackagesPanesBuildingTask extends Task<List<TitledPane>>{
    private final Accordion loadInto;
    private final List<ObservableClass> classes;
    private final BigBrotherGuiController caller;
    private final List<TitledPane> buildedPanes = new ArrayList();
    
    public PackagesPanesBuildingTask(BigBrotherGuiController caller, Accordion loadInto, List<ObservableClass> classes) {
        this.loadInto = loadInto;
        this.classes = classes;
        this.caller = caller;
    }

    @Override
    protected List<TitledPane> call() throws Exception {
        HashMap<String, List<ObservableClass>> groupedClasses = new HashMap<>();
            
        for(ObservableClass classe : this.classes){
            String packageName = classe.getPackageName();
            if(!groupedClasses.containsKey(packageName)){
                groupedClasses.put(packageName, new ArrayList<ObservableClass>());
            }
            groupedClasses.get(packageName).add(classe);
        }

        List<Map.Entry<String, List<ObservableClass>>> sortedGroupedClasses = new ArrayList<>(groupedClasses.entrySet());
        Collections.sort(sortedGroupedClasses, new Comparator<Map.Entry<String, List<ObservableClass>>>(){
            @Override
            public int compare(Map.Entry<String, List<ObservableClass>> t, Map.Entry<String, List<ObservableClass>> t1) {
                return t.getKey().compareTo(t1.getKey());
            }
        });
        for(Map.Entry<String, List<ObservableClass>> entry : sortedGroupedClasses){
            try {
                List<ObservableClass> classes = entry.getValue();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PackageView.fxml"));
                TitledPane p = (TitledPane) fxmlLoader.load();
                PackageViewController pController = fxmlLoader.<PackageViewController>getController();
                pController.setParentController(this.caller);
                pController.setTitle(entry.getKey());

                for(ObservableClass classe : classes){
                    pController.addObservableClass(classe);
                }

                this.buildedPanes.add(p);
            } catch (IOException ex) {
                Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.buildedPanes;
    }
}
