/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableClassException;
import bigbrother.core.model.ObservableField;
import bigbrother.gui.tasks.scanner.ScannerTask;
import bigbrother.gui.tasks.treechart.TreeChartTask;
import bigbrother.gui.tasks.treechart.TreeNodeController;
import de.chimos.ui.treechart.layout.NodePosition;
import de.chimos.ui.treechart.layout.TreePane;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.RotateTransitionBuilder;
import javafx.animation.TimelineBuilder;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class BigBrotherGuiController implements Initializable {
    private static final double ZOOM_MAX_SCALE = 1d;
    private static final double ZOOM_MIN_SCALE = .5d;
    private static final double ZOOM_DELTA = .1d;
        
    @FXML
    public Pane rootPane;
    @FXML
    public Accordion classesList;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label bottomMessage;
    
    private FileChooser jarFileChooser;
    private SimpleBooleanProperty loading;

    /**
     * Prompt the a open dialog and call {@link BigBrotherGuiController#doScan(java.lang.String)} with the selected file.
     */
    @FXML
    public void handleOpen(){
        File jarFile = this.jarFileChooser.showOpenDialog(this.getScene().getWindow());
        if(jarFile != null && jarFile.canRead()){
            this.doScan(jarFile.getPath());
        }
    }
    
    /**
     * Scan a jar file in order to retrieve all classes.
     * @param jarFilePath The jar file path.
     */
    public void doScan(String jarFilePath){
        this.loading.set(true);
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.classesList.getPanes().clear();
        
        ScannerTask scannerBuilder = new ScannerTask(jarFilePath);
        scannerBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            Scanner scanner = (Scanner) t.getSource().getValue();
            
            HashMap<String, List<ObservableClass>> sortedClasses = new HashMap<>();
            
            for(ObservableClass classe : scanner.getClasses()){
                String packageName = classe.getPackageName();
                if(!sortedClasses.containsKey(packageName)){
                    sortedClasses.put(packageName, new ArrayList<ObservableClass>());
                }
                sortedClasses.get(packageName).add(classe);
            }
            
            for(Entry<String, List<ObservableClass>> entry : sortedClasses.entrySet()){
                try {
                    List<ObservableClass> classes = entry.getValue();
                    
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PackageView.fxml"));
                    TitledPane p = (TitledPane) fxmlLoader.load();
                    PackageViewController pController = fxmlLoader.<PackageViewController>getController();
                    pController.setParentController(BigBrotherGuiController.this);
                    pController.setTitle(entry.getKey());
                    
                    for(ObservableClass classe : classes){
                        pController.addObservableClass(classe);
                    }
                    
                    BigBrotherGuiController.this.classesList.getPanes().add(p);
                } catch (IOException ex) {
                    Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            BigBrotherGuiController.this.classesList.autosize();

            if(scanner.encouredError()){
                BigBrotherGuiController.this.bottomMessage.setTextFill(Color.DARKORANGE);
                BigBrotherGuiController.this.bottomMessage.setText("Exploration incomplète : Certaines classes n'ont pas pu être chargées.");
            }
            
            BigBrotherGuiController.this.progressBar.setProgress(1d);
            BigBrotherGuiController.this.loading.set(false);
            }
        });
        scannerBuilder.setOnFailed(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent t) {
                BigBrotherGuiController.this.progressBar.setProgress(1d);
                BigBrotherGuiController.this.loading.set(false);
            }
        });
        new Thread(scannerBuilder).start();
    }
    
    /**
     * Close the application.
     */
    @FXML
    public void handleClose(){
        System.exit(0);
    }
    
    /**
     * Get the main scene.
     * 
     * @return The main scene. 
     */
    protected Scene getScene(){
        return this.rootPane.getScene();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.loading = new SimpleBooleanProperty(false);
        
        this.classesList.disableProperty().bind(this.loading);
        this.scrollPane.disableProperty().bind(this.loading);
            
        this.scrollPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                BigBrotherGuiController.this.scrollPane.requestFocus();
            }
        });
        this.scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>(){
            @Override
            public void handle(ScrollEvent t) {
                if (t.isControlDown()) {
                    if(t.getDeltaY() > 0){
                        BigBrotherGuiController.this.handleZoomIn();
                    }
                    else{
                        BigBrotherGuiController.this.handleZoomOut();
                    }
                    t.consume();
                }
            }
        });
        
        this.jarFileChooser = new FileChooser();
        this.jarFileChooser.setTitle("Explorer");
        this.jarFileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        this.jarFileChooser.getExtensionFilters().add(
                new ExtensionFilter("Executables Java", "*.jar")
        );
        
    }
    
    @FXML
    public void handleZoomIn(){
        this.doZoom(ZOOM_DELTA);
    }
    
    @FXML
    public void handleZoomOut(){
        this.doZoom(-ZOOM_DELTA);
    }
    
    private void doZoom(double delta) {
        final Node scrollContent = this.scrollPane.getContent();
        
        double scale = scrollContent.getScaleX() + delta;

        if (scale <= ZOOM_MIN_SCALE) {
            scale = ZOOM_MIN_SCALE;
        }
        else if (scale >= ZOOM_MAX_SCALE) {
            scale = ZOOM_MAX_SCALE;
        }
        
        scrollContent.setScaleX(scale);
        scrollContent.setScaleY(scale);
    }
    
    void loadTreeChart(ObservableClass classe){
        this.loading.set(true);
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        
        this.bottomMessage.setText("");
        this.unloadTreeChart();
        
        TreeChartTask treeBuilder = new TreeChartTask(classe);
        treeBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                TreeChartTask.BuildedTreeChart treeChart = (TreeChartTask.BuildedTreeChart) t.getSource().getValue();

                BigBrotherGuiController.this.scrollPane.setContent(treeChart.getTreePane());
                BigBrotherGuiController.this.bottomMessage.setTextFill(treeChart.getMessageColor());
                BigBrotherGuiController.this.bottomMessage.setText(treeChart.getMessage());

                BigBrotherGuiController.this.progressBar.setProgress(1.0d);
                BigBrotherGuiController.this.loading.set(false);
            }
        });
        treeBuilder.setOnFailed(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent t) {
                BigBrotherGuiController.this.bottomMessage.setTextFill(Color.DARKRED);
                BigBrotherGuiController.this.bottomMessage.setText("Erreur de construction de l'arbre.");
                
                BigBrotherGuiController.this.progressBar.setProgress(1.0d);
                BigBrotherGuiController.this.loading.set(false);
            }
        });
        new Thread(treeBuilder).start();
    }
    
    /**
     * Clear the tree chart view.
     */
    public void unloadTreeChart(){
        this.scrollPane.setContent(new Pane());
    }
}
