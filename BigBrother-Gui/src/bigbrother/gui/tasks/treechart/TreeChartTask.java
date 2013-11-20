/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.treechart;

import bigbrother.core.model.ObservableClass;
import bigbrother.core.model.ObservableClassException;
import bigbrother.core.model.ObservableField;
import bigbrother.gui.BigBrotherGuiController;
import de.chimos.ui.treechart.layout.NodePosition;
import de.chimos.ui.treechart.layout.TreePane;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author Karl
 */
public class TreeChartTask extends Task<TreeChartTask.BuildedTreeChart>{
    public class BuildedTreeChart{
        private TreePane pane;
        private String message = "";
        private Color messageColor = Color.BLACK;
        
        public BuildedTreeChart(TreePane pane){
            this.pane = pane;
        }
        
        public BuildedTreeChart(TreePane pane, String message){
            this(pane);
            this.message = message;
        }
        
        public BuildedTreeChart(TreePane pane, String message, Color messageColor){
            this(pane, message);
            this.messageColor = messageColor;
        }

        public String getMessage() {
            return this.message;
        }

        public Color getMessageColor() {
            return this.messageColor;
        }
        
        public TreePane getTreePane(){
            return this.pane;
        }
    }
    
    
    private final ObservableClass rootClasse;
    
    private static final double X_SPACING = 80d;
    private static final double Y_SPACING = 100d;
    
    public TreeChartTask(ObservableClass rootClasse){
        this.rootClasse = rootClasse;
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeNode.fxml"));
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeNodeEllipsis.fxml"));
            element = (Node) fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return element;
    }
    
    /**
     * Build the children of a {@link TreeNode} using {@link ObservableClasse} fields informations. 
     * Only the non static fields are followed.
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
                if(!field.isStatic()){
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
    }

    @Override
    protected TreeChartTask.BuildedTreeChart call() throws Exception {
        TreePane treePane = new TreePane();
        treePane.setXAxisSpacing(X_SPACING);
        treePane.setYAxisSpacing(Y_SPACING);

        //Tree root
        treePane.addChild(this.loadTreeNode(this.rootClasse), NodePosition.ROOT);
        
        //Tree branches
        try {
            this.loadTreeNodeChildren(this.rootClasse, treePane, NodePosition.ROOT, 7, 3);
            return new BuildedTreeChart(treePane);
        } 
        catch (ObservableClassException ex) {
            Logger.getLogger(BigBrotherGuiController.class.getName()).log(Level.INFO, null, ex);
            return new BuildedTreeChart(treePane, "Arbre partiel : Classe indéfinie trouvée.", Color.DARKORANGE);
        }
    }
}
