/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.accordion;

import bigbrother.core.model.ObservableClass;
import bigbrother.gui.BigBrotherGuiController;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Karl
 */
public class PackagesPanesBuildingTask extends Task<TreeItem<PackagesPanesBuildingTask.TreeNode>>{
    private final List<ObservableClass> classes;
    private final BigBrotherGuiController caller;
    private final TreeItem<TreeNode> root;
    
    private HashMap<String, TreeItem<TreeNode>> nodesDictionary = new HashMap<>();
    
    public PackagesPanesBuildingTask(BigBrotherGuiController caller, List<ObservableClass> classes, String rootName) {
        this.classes = classes;
        this.caller = caller;
        this.root = new TreeItem<>(new TreeNode(rootName));
    }
    
    public class TreeNode{
        private final boolean isPackage;
        private final String packageName;
        private final ObservableClass observableClass;

        /**
         *
         * @param packageName
         */
        public TreeNode(String packageName) {
            this.packageName = packageName;
            this.isPackage = true;
            this.observableClass = null;
        }
            
        public TreeNode(ObservableClass observableClass) {
            this.observableClass = observableClass;
            this.isPackage = false;
            this.packageName = observableClass.getPackageName();
        }

        public boolean isPackage() {
            return this.isPackage;
        }
        public boolean isClass() {
            return !this.isPackage;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public ObservableClass getObservableClass() {
            return this.observableClass;
        }
    }
    
    private void addItemToTree(ObservableClass observable){
        StringBuilder packageNameStep = new StringBuilder();
        StringTokenizer st = new StringTokenizer(observable.getPackageName(), ".");
        
        while(st.hasMoreTokens()){
            String previousPackage = packageNameStep.toString();
            packageNameStep.append(st.nextToken());
            String currentPackage = packageNameStep.toString();
            if(!this.nodesDictionary.containsKey(currentPackage)){
                TreeItem<TreeNode> node = new TreeItem<>(new TreeNode(currentPackage));
                if(previousPackage.isEmpty()){
                    this.root.getChildren().add(node);
                }
                else{
                    this.nodesDictionary.get(previousPackage).getChildren().add(node);
                }
                this.nodesDictionary.put(currentPackage, node);
            }
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
