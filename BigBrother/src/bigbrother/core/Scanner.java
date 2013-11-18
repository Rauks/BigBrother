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
                this.loadedClasses.add(new ObservableClass(c));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Scanner.class.getName()).log(Level.SEVERE, null, ex);
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
}
