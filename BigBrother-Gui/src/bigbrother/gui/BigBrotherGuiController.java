/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.Scanner;
import bigbrother.core.model.ObservableClass;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    public Pane dataPane;
    
    private FileChooser jarFileChooser;
    private ObservableList<ObservableClass> observablesClasses;

    @FXML
    public void handleOpen(){
        File jarFile = this.jarFileChooser.showOpenDialog(this.getScene().getWindow());
        if(jarFile != null && jarFile.canRead()){
            this.doScan(jarFile.getPath());
        }
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
        
        this.initializeTreeChart();
    }    
    
    private void initializeTreeChart(){
        TreePane treePane = new TreePane();
        treePane.yAxisSpacingProperty().set(10.0);

        treePane.addChild(new Label("Root Node"), NodePosition.ROOT);
        generateTreeItems(treePane, NodePosition.ROOT, 3, 7);

        this.dataPane.getChildren().add(treePane);

        TimelineBuilder.create()
                .keyFrames(
                        new KeyFrame(
                                Duration.seconds(5),
                                new KeyValue(
                                        treePane.yAxisSpacingProperty(),
                                        65.0,
                                        Interpolator.LINEAR
                                )
                        )
                )
                .cycleCount(1)
                .build()
                .play();
    }
    
    private static Random random = new Random();
    private static void generateTreeItems(TreePane treePane, NodePosition parentPosition, int maxChildren, int maxLevel) {
        if (parentPosition.getLevel() >= maxLevel) {
            return;
        }

        int maxChildrenBias1 = parentPosition.getLevel();
        int maxChildrenBias2 = maxChildren - maxChildrenBias1;

        for (int i = 0, j = random.nextInt(maxChildrenBias1 + 1) + maxChildrenBias2; i < j; ++i) {
            NodePosition position = parentPosition.getChild(i);

            String labelText = "Node ";

            for (int pathElement : position.getPath()) {
                labelText += "." + pathElement;
            }
            if (random.nextInt() % 3 == 0) {
                labelText += "\nYes!";
            }
            if (random.nextInt() % 3 == 0) {
                labelText += "\nNo!";
            }
            final Label label = new Label(labelText);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setOnMouseEntered(new EventHandler<MouseEvent>() {
                private RotateTransition rotation = RotateTransitionBuilder.create()
                        .byAngle(180).autoReverse(true).cycleCount(2)
                        .duration(Duration.seconds(1))
                        .node(label)
                        .build();

                @Override
                public void handle(MouseEvent event) {
                    rotation.play();
                }
            });

            treePane.addChild(label, position);

            generateTreeItems(treePane, position, maxChildren, maxLevel);
        }
    }
}
