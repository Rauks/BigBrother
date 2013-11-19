/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui.tasks.scanner;

import bigbrother.core.Scanner;
import javafx.concurrent.Task;

/**
 *
 * @author Karl
 */
public class ScannerTask extends Task<Scanner>{
    private final String pathToJar;
    
    public ScannerTask(String pathToJar){
        this.pathToJar = pathToJar;
    }
    
    @Override
    protected Scanner call() throws Exception {
        return new Scanner(pathToJar);
    }
}
