/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import bigbrother.core.AnnotationScanner;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Karl
 */
public class BigBrotherController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AnnotationScanner scan = new AnnotationScanner();
        Set<String> classes = scan.scanAnnotatedClasses();
        for(String classe : classes){
            System.out.println(classe);
        }
    }    
    
}
