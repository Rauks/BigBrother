/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableField;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.RotateTransitionBuilder;
import javafx.animation.TimelineBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

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
    public ScrollPane scrollPane;
    
    private FileChooser jarFileChooser;
    private ObservableList<ObservableClass> observablesClasses;

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
        this.observablesClasses.clear();
        try {
            Scanner scanner = new Scanner(jarFilePath);
            this.observablesClasses.addAll(scanner.getClasses());
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.WARNING, null, ex);
        }
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
        this.scrollPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                BigBrotherGuiController.this.scrollPane.requestFocus();
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
    }
    
    /**
     * Load the complete tree builded from a {@link ObservableClass} into the gui.
     * 
     * @param classe The classe as root of the tree.
     */
    private void loadTreeChart(ObservableClass classe){
        this.unloadTreeChart();
        TreePane treePane = new TreePane();
        treePane.yAxisSpacingProperty().set(5.0);

        //Tree root
        treePane.addChild(this.loadTreeNode(classe), NodePosition.ROOT);
        
        //Tree branches
        //TODO: Change tree limits to options.
        this.loadTreeNodeChildren(classe, treePane, NodePosition.ROOT, 7, 3);

        this.scrollPane.setContent(treePane);
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
    private void loadTreeNodeChildren(ObservableClass classe, TreePane treePane, NodePosition parentPosition, int maxChildren, int maxLevel) {
        if (parentPosition.getLevel() >= maxLevel) {
            final Node node = this.loadEllipsisTreeNode();
            treePane.addChild(node, parentPosition.getChild(0));
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, "TreeBuilding: Max level reached.");
        }
        else{
            int childIndex = 0;
            for (ObservableField field : classe.getFields()) {
                NodePosition position = parentPosition.getChild(childIndex);
                if(childIndex >= maxChildren){
                    final Node node = this.loadEllipsisTreeNode();
                    treePane.addChild(node, position);
                    Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, "TreeBuilding: Max children reached.");
                    return;
                }
                else{
                    Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, "TreeBuilding: Building child ({0}.{1}).", new Object[]{parentPosition.getLevel(), childIndex});
                    ObservableClass fieldType = field.getType();
                    final Node node = this.loadTreeNode(fieldType);
                    treePane.addChild(node, position);
                    Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, "TreeBuilding: Child builded.");
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
