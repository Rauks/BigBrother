/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigbrother.core;

import bigbrother.annotation.BigBrother;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Karl
 */
public class Scanner {
    /**
     * Return the list of classes annotated with the {@link BigBrother} annotation.
     * 
     * @param pckgnames The packages to scann to find the classes.
     * @return The list of annotated classes finded.
     * @throws ClassNotFoundException
     * @throws IOException 
     * 
     * @see BigBrother
     */
    public List<Class> getAnnotatedClasses(String... pckgnames) throws ClassNotFoundException, IOException{
        ArrayList<Class> classes = new ArrayList<>();
        for(String pckgname : pckgnames){
            for(Class classe : this.getClasses(pckgname)){
                if(classe.isAnnotationPresent(BigBrother.class)){
                    classes.add(classe);
                }
            }
        }
        return classes;
    }

    /**
     * List all the classes of a package.
     *
     * @param pckgname The package to scan.
     * @return The list of classes finded.
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.IOException
     */
    public List<Class> getClasses(String pckgname) throws ClassNotFoundException, IOException {
        ArrayList<Class> classes = new ArrayList<>();
        String[] entries = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        for (String entrie : entries) {
            if (entrie.endsWith(".jar")) {
                classes.addAll(scanJar(entrie, pckgname));
            } 
            else {
                classes.addAll(scanDir(entrie, pckgname));
            }
        }
        return classes;
    }

    /**
     * List the classes of a package in a directory in the classpath.
     *
     * @param dir The dir to scan.
     * @param pckgname The package of the classes.
     * @return The list of classes finded.
     */
    private List<Class> scanDir(String dir, String pckgname) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<>();
        StringBuilder sb = new StringBuilder(dir);
        String[] repsPkg = pckgname.split("\\.");
        for (String repsPkg1 : repsPkg) {
            sb.append(System.getProperty("file.separator")).append(repsPkg1);
        }
        File rep = new File(sb.toString());
        if (rep.exists() && rep.isDirectory()) {
            FilenameFilter filter = new ClassFilter();
            File[] liste = rep.listFiles(filter);
            for (File liste1 : liste) {
                classes.add(Class.forName(pckgname + "." + liste1.getName().split("\\.")[0]));
            }
        }
        return classes;
    }

    /**
     * List the classes of a package in a jar in the classpath.
     *
     * @param jar The jar to scan.
     * @param pckgname The package of the classes.
     * @return The list of classes finded.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private List<Class> scanJar(String jar, String pckgname) throws IOException, ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<>();
        JarFile jfile = new JarFile(jar);
        String pkgpath = pckgname.replace(".", "/");
        for (Enumeration<JarEntry> entries = jfile.entries(); entries.hasMoreElements();) {
            JarEntry element = entries.nextElement();
            if (element.getName().startsWith(pkgpath) && element.getName().endsWith(".class")) {
                String nomFichier = element.getName().substring(pckgname.length() + 1);
                classes.add(Class.forName(pckgname + "." + nomFichier.split("\\.")[0]));
            }
        }
        return classes;
    }

    /**
     * Custom filter used to find the .class files in a directory.
     */
    private class ClassFilter implements FilenameFilter {
        @Override
        public boolean accept(File arg0, String arg1) {
            return arg1.endsWith(".class");
        }
    }
}
