/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core;

import bigbrother.core.annotation.BigBrother;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

/**
 *
 * @author Karl
 */
public class AnnotationScanner {
    public Set<String> scanAnnotatedClasses() {
        Set<String> entityClasses = null;
        try {
            URL[] urls = ClasspathUrlFinder.findClassPaths(); // scan java.class.path
            AnnotationDB db = new AnnotationDB();
            db.scanArchives(urls);
            entityClasses = db.getAnnotationIndex().get(BigBrother.class.getName());
        } catch (IOException ex) {
            Logger.getLogger(AnnotationScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entityClasses;
    }
}
