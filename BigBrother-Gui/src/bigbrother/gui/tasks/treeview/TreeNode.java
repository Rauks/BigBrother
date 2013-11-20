/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.treeview;

import bigbrother.core.model.ObservableClass;

/**
 *
 * @author Karl
 */
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
