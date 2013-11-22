/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableClassException;
import bigbrother.gui.tasks.treeview.TreeViewTask;
import bigbrother.gui.tasks.treeview.TreeNode;
import bigbrother.gui.tasks.scanner.ScannerTask;
import bigbrother.gui.tasks.treechart.TreeChartTask;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

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
    public TreeView classesList;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label bottomMessage;
    @FXML
    public HBox arianeBox;
    @FXML
    public HBox implementsBox;
    @FXML
    public Label elementName;
    
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
        //this.classesList.getPanes().clear();
        this.unloadTreeChart();
        
        ScannerTask scannerBuilder = new ScannerTask(jarFilePath);
        this.progressBar.progressProperty().bind(scannerBuilder.progressProperty());
        scannerBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                Scanner scanner = (Scanner) t.getSource().getValue();

                if(scanner.encouredError()){
                    BigBrotherGuiController.this.bottomMessage.setTextFill(Color.DARKORANGE);
                    BigBrotherGuiController.this.bottomMessage.setText("Exploration incomplète : Certaines classes n'ont pas pu être chargées.");
                }

                TreeViewTask accordionBuilder = new TreeViewTask(scanner.getClasses(), scanner.getJarName());
                accordionBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        TreeItem<TreeNode> root = (TreeItem<TreeNode>) t.getSource().getValue();
                        BigBrotherGuiController.this.classesList.setRoot(root);
                
                        BigBrotherGuiController.this.loading.set(false);
                    }
                });
                accordionBuilder.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        BigBrotherGuiController.this.bottomMessage.setTextFill(Color.DARKORANGE);
                        BigBrotherGuiController.this.bottomMessage.setText("Erreur de construction de la liste des packages.");

                        BigBrotherGuiController.this.progressBar.setProgress(1d);
                        BigBrotherGuiController.this.loading.set(false);
                    }
                });
                new Thread(accordionBuilder).start();
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
        
        this.scrollPane.setScaleX(1d);
        this.scrollPane.setScaleY(1d);
        this.scrollPane.setScaleZ(1d);
        
        this.classesList.disableProperty().bind(this.loading);
        this.classesList.setShowRoot(false);
        this.classesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TreeNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TreeNode>> observable, TreeItem<TreeNode> oldValue, TreeItem<TreeNode> newValue) {
                if(newValue != null){
                    if(newValue.getValue().isClass()){
                        BigBrotherGuiController.this.loadTreeChart(newValue.getValue().getObservableClass());
                    }
                }
                else{
                    BigBrotherGuiController.this.unloadTreeChart();
                }
            }
        });
        this.classesList.setCellFactory(new Callback<TreeView<TreeNode>, TreeCell<TreeNode>>(){
            private Image packageImage = new Image(BigBrotherGuiController.class.getResourceAsStream("folder.png"));
            private Image classImage = new Image(BigBrotherGuiController.class.getResourceAsStream("file.png"));
            
            @Override
            public TreeCell<TreeNode> call(TreeView<TreeNode> p) {
                final Tooltip tooltip = new Tooltip();
                final TreeCell<TreeNode> cell = new TreeCell<TreeNode>() {
                    @Override
                    public void updateItem(TreeNode item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty) {
                            ImageView iv = new ImageView();
                            iv.setFitHeight(16);
                            iv.setFitWidth(16);
                            iv.setPreserveRatio(true);
                                
                            if(item.isPackage()){
                                this.setText(item.getPackageName());
                                iv.setImage(packageImage);
                            }
                            else{
                                this.setText(item.getObservableClass().getSimpleName());
                                iv.setImage(classImage);
                                iv.setOpacity(0.5d);

                                switch(item.getObservableClass().getType()){
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

                                tooltip.setText(item.getObservableClass().getType().getName());
                                this.setTooltip(tooltip);
                                this.setCursor(Cursor.HAND);
                            }
                            this.setGraphic(iv);
                        }
                    }
                }; // ListCell
                return cell;
            }
        });
        
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
    
    public void loadTreeChart(final ObservableClass classe){
        this.loading.set(true);
        
        this.bottomMessage.setText("");
        this.unloadTreeChart();
        
        this.elementName.setText(classe.getSimpleName());
        
        TreeChartTask treeBuilder = new TreeChartTask(classe, this);
        this.progressBar.progressProperty().bind(treeBuilder.progressProperty());
        treeBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                TreeChartTask.BuildedTreeChart treeChart = (TreeChartTask.BuildedTreeChart) t.getSource().getValue();

                BigBrotherGuiController.this.scrollPane.setContent(treeChart.getTreePane());
                BigBrotherGuiController.this.bottomMessage.setTextFill(treeChart.getMessageColor());
                BigBrotherGuiController.this.bottomMessage.setText(treeChart.getMessage());
                
                BigBrotherGuiController.this.cleanAriane();
                BigBrotherGuiController.this.buildAriane(classe);
                BigBrotherGuiController.this.buildImplements(classe);

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
    
    private void buildArianeStep(ObservableClass classe){
        try {
            final ObservableClass superClass = classe.getSuperClass();
            if(superClass != null){
                this.buildArianeStep(superClass);
                Label label = new Label(superClass.getSimpleName());
                label.setCursor(Cursor.HAND);
                label.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent t) {
                        BigBrotherGuiController.this.loadTreeChart(superClass);
                    }
                });
                this.arianeBox.getChildren().add(label);
            }
            else{
                return;
            }
        } catch (ObservableClassException ex) {
                this.arianeBox.getChildren().add(new Label("?"));
        }
        this.arianeBox.getChildren().add(new Label(" > "));
    }
    
    private void buildAriane(ObservableClass classe){
        this.buildArianeStep(classe);
        
        Label label = new Label(classe.getSimpleName());
        label.setUnderline(true);
        this.arianeBox.getChildren().add(label);
    }
    private void cleanAriane(){
        this.arianeBox.getChildren().clear();
    }
    
    private void buildImplements(ObservableClass classe){
        try {
            for(Iterator<ObservableClass> it = classe.getInterfaces().iterator(); it.hasNext();){
                final ObservableClass impl = it.next();
                Label label = new Label(impl.getSimpleName());
                label.setCursor(Cursor.HAND);
                label.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent t) {
                        BigBrotherGuiController.this.loadTreeChart(impl);
                    }
                });
                this.implementsBox.getChildren().add(label);
                if(it.hasNext()){
                    this.implementsBox.getChildren().add(new Label(", "));
                }
            }
            
        } catch (ObservableClassException ex) {
            this.implementsBox.getChildren().add(new Label("?"));
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, null, ex);
        }
    }
    private void clearImplements(){
        this.implementsBox.getChildren().clear();
    }
    
    /**
     * Clear the tree chart view.
     */
    public void unloadTreeChart(){
        this.cleanAriane();
        this.clearImplements();
        this.elementName.setText("Element");
        this.scrollPane.setContent(new Pane());
    }
}
