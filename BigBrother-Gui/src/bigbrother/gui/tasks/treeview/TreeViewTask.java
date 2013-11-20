/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.treeview;

import bigbrother.core.model.ObservableClass;
import bigbrother.gui.BigBrotherGuiController;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Karl
 */
public class TreeViewTask extends Task<TreeItem<TreeNode>>{
    private final List<ObservableClass> classes;
    private final BigBrotherGuiController caller;
    private final TreeItem<TreeNode> root;
    
    private final HashMap<String, TreeItem<TreeNode>> nodesDictionary = new HashMap<>();
    
    public TreeViewTask(BigBrotherGuiController caller, List<ObservableClass> classes, String rootName) {
        this.classes = classes;
        this.caller = caller;
        this.root = new TreeItem<>(new TreeNode(rootName));
    }
    
    private void addItemToTree(ObservableClass observable){
        StringBuilder packageNameStep = new StringBuilder();
        StringTokenizer st = new StringTokenizer(observable.getPackageName(), ".");
        String previousPackage = "";
        
        while(st.hasMoreTokens()){
            String stepName = st.nextToken();
            packageNameStep.append(stepName);
            String currentPackage = packageNameStep.toString();
            if(st.hasMoreTokens()){
                packageNameStep.append(".");
            }
            if(!this.nodesDictionary.containsKey(currentPackage)){
                TreeItem<TreeNode> node = new TreeItem<>(new TreeNode(stepName));
                if(previousPackage.isEmpty()){
                    this.root.getChildren().add(node);
                }
                else{
                    this.nodesDictionary.get(previousPackage).getChildren().add(node);
                }
                this.nodesDictionary.put(currentPackage, node);
            }
            previousPackage = currentPackage;
        }
        
        String targetPackage = packageNameStep.toString();
        TreeItem<TreeNode> node = new TreeItem<>(new TreeNode(observable));
        if(targetPackage.isEmpty()){
            this.root.getChildren().add(node);
        }
        else{
            this.nodesDictionary.get(targetPackage).getChildren().add(node);
        }
    }

    @Override
    protected TreeItem<TreeNode> call() throws Exception {
        try{
            for(ObservableClass classe : this.classes){
                this.addItemToTree(classe);
            }
            this.updateProgress(1, 1);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return this.root;
    }
}