/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Karl
 */
public class BigBrotherStarter{
    public void start(){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BigBrother.fxml"));
            
            Scene scene = new Scene(root);
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(BigBrotherStarter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
