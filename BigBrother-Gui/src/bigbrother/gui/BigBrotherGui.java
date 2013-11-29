/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Karl
 */
public class BigBrotherGui extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BigBrotherGui.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        BigBrotherGuiController controller = fxmlLoader.<BigBrotherGuiController>getController();
        controller.setStage(stage);
        
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(BigBrotherGui.class.getResource("style.css").toURI().toString());
        stage.setScene(scene);
        stage.setTitle("BigBrother");
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
