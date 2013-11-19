/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigbrother.core;

import bigbrother.core.model.ObservableClass;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karl
 */
public class Scanner {
    private List<ObservableClass> loadedClasses = new ArrayList<>();
    private boolean encouredError = false;
    
    /**
     * Load a jar file and load all contained classes.
     * 
     * @param pathToJar The path to the jar.
     * @throws IOException In case of IO error.
     */
    public Scanner(String pathToJar) throws IOException{
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration entries = jarFile.entries();

        URL[] urls = {
            new URL("jar:file:" + pathToJar+"!/")
        };
        URLClassLoader classLoader = URLClassLoader.newInstance(urls);

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = jarEntry.getName().substring(0, jarEntry.getName().length()-6);
            className = className.replace('/', '.');
            try {
                Class c = classLoader.loadClass(className);
                c.isAnonymousClass(); //To provoque NoClassDefFoundError in case of incomplete loading.
                this.loadedClasses.add(new ObservableClass(c));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Scanner.class.getName()).log(Level.SEVERE, null, ex);
            } catch(NoClassDefFoundError ex){
                Logger.getLogger(Scanner.class.getName()).log(Level.INFO, null, ex);
                this.encouredError = true;
            }
        }
    }

    /**
     * List all the classes of the scanned jar.
     * 
     * @return The list of classes.
     */
    public List<ObservableClass> getClasses(){
        return this.loadedClasses;
    }
    
    /**
     * Return true if a {@link NoClassDefFoundError} encoured during the scanning and classes loading process.
     * 
     * @return Return true if an error encoured.
     */
    public boolean encouredError(){
        return this.encouredError;
    }
}
