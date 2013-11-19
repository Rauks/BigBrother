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
import bigbrother.gui.tasks.ScannerTask;
import bigbrother.gui.treechart.TreeNodeController;
import de.chimos.ui.treechart.layout.NodePosition;
import de.chimos.ui.treechart.layout.TreePane;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
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
    public ListView classesList;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label bottomMessage;
    
    private FileChooser jarFileChooser;
    private ObservableList<ObservableClass> observablesClasses;
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
        this.observablesClasses.clear();
        
        ScannerTask scannerBuilder = new ScannerTask(jarFilePath);
        scannerBuilder.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                
            Scanner scanner = (Scanner) t.getSource().getValue();
            BigBrotherGuiController.this.observablesClasses.addAll(scanner.getClasses());

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
        scannerBuilder.setOnCancelled(new EventHandler<WorkerStateEvent>(){
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
        
        this.observablesClasses = FXCollections.observableArrayList();
        this.classesList.setItems(this.observablesClasses);

        //Class selection
        this.classesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    System.out.println(newValue);
                    BigBrotherGuiController.this.loadTreeChart((ObservableClass) newValue);
                }
                else{
                    BigBrotherGuiController.this.unloadTreeChart();
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
                        this.setText(item.getName());
                        
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
    
    /**
     * Load the complete tree builded from a {@link ObservableClass} into the gui.
     * 
     * @param classe The classe as root of the tree.
     */
    private void loadTreeChart(ObservableClass classe){
        this.loading.set(true);
        this.bottomMessage.setText("");
        this.unloadTreeChart();
        TreePane treePane = new TreePane();
        treePane.setXAxisSpacing(80d);
        treePane.setYAxisSpacing(100d);

        //Tree root
        treePane.addChild(this.loadTreeNode(classe), NodePosition.ROOT);
        
        //Tree branches
        try {
            this.loadTreeNodeChildren(classe, treePane, NodePosition.ROOT, 7, 3);
        } catch (ObservableClassException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, null, ex);
            this.bottomMessage.setTextFill(Color.DARKRED);
            this.bottomMessage.setText("Arbre partiel : Classe indéfinie trouvée.");
        }

        this.scrollPane.setContent(treePane);
        this.loading.set(false);
    }
    
    /**
     * Build a node with informations of a {@link ObservableClass}.
     * 
     * @param classe The classe used to build the node.
     * @return A Node with class informations.
     */
    private Node loadTreeNode(ObservableClass classe){
        Node element = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("treechart/TreeNode.fxml"));
            element = (Node) fxmlLoader.load();
            TreeNodeController elementController = fxmlLoader.<TreeNodeController>getController();
            elementController.setObservation(classe);
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return element;
    }
    
    /**
     * Build a ellipsis node.
     * 
     * @return A ellipsis Node.
     */
    private Node loadEllipsisTreeNode(){
        Node element = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("treechart/TreeNodeEllipsis.fxml"));
            element = (Node) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return element;
    }
    
    /**
     * Build the children of a {@link TreeNode} using {@link ObservableClasse} fields informations.
     * 
     * @param classe The class which fields are used to build the children nodes.
     * @param treePane The tree where the children are added.
     * @param parentPosition The parent node.
     * @param maxChildren Children number limit.
     * @param maxLevel Tree level limit.
     */
    private void loadTreeNodeChildren(ObservableClass classe, TreePane treePane, NodePosition parentPosition, int maxChildren, int maxLevel) throws ObservableClassException {
        List<ObservableField> fields = classe.getFields();
        if (parentPosition.getLevel() >= maxLevel && !fields.isEmpty()) {
            final Node node = this.loadEllipsisTreeNode();
            treePane.addChild(node, parentPosition.getChild(0));
        }
        else{
            int childIndex = 0;
            for (ObservableField field : fields) {
                NodePosition position = parentPosition.getChild(childIndex);
                if(childIndex >= maxChildren){
                    final Node node = this.loadEllipsisTreeNode();
                    treePane.addChild(node, position);
                    return;
                }
                else{
                    ObservableClass fieldType = field.getType();
                    final Node node = this.loadTreeNode(fieldType);
                    treePane.addChild(node, position);
                    this.loadTreeNodeChildren(fieldType, treePane, position, maxChildren, maxLevel);
                }
                childIndex++;
            }
        }
    }
    
    /**
     * Clear the tree chart view.
     */
    public void unloadTreeChart(){
        this.scrollPane.setContent(new Pane());
    }
}
