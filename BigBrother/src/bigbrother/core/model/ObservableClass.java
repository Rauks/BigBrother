/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Karl
 */
public class ObservableClass{
    public enum Type{
        ANNOTATION("Interface (Annotation)"),
        INTERFACE("Interface"),
        ENUMERATION("Enumeration"),
        CLASS("Classe"),
        CLASS_ANONYMOUS("Classe (Anonyme)"),
        CLASS_LOCAL("Classe (Locale)"),
        CLASS_MEMBER("Classe (Membre)"),
        CLASS_PRIMITIVE("Classe (Primitive)"),
        CLASS_SYNTHETIC("Classe (Synthétique)");
        
        private final String name;
        
        private Type(String name){
            this.name = name;
        }
        
        public String getName(){
            return this.name;
        }
        
        @Override
        public String toString(){
            return this.getName();
        }
    }
    
    private final Class classe;
    
    public ObservableClass(Class classe){
        this.classe = classe;
    }
    
    public String getName(){
        return this.classe.getName();
    }
    
    public String getSimpleName(){
        return (this.classe.getSimpleName().isEmpty())?this.getName().replaceAll(".*\\.", ""):this.classe.getSimpleName();
    }
    
    public Type getType(){
        if(this.classe.isAnnotation()){
            return Type.ANNOTATION;
        }
        if(this.classe.isInterface()){
            return Type.INTERFACE;
        }
        if(this.classe.isEnum()){
            return Type.ENUMERATION;
        }
        if(this.classe.isAnonymousClass()){
            return Type.CLASS_ANONYMOUS;
        }
        if(this.classe.isLocalClass()){
            return Type.CLASS_LOCAL;
        }
        if(this.classe.isMemberClass()){
            return Type.CLASS_MEMBER;
        }
        if(this.classe.isPrimitive()){
            return Type.CLASS_PRIMITIVE;
        }
        if(this.classe.isSynthetic()){
            return Type.CLASS_SYNTHETIC;
        }
        return Type.CLASS;
    }
    
    public String getPackageName(){
        Package p = this.classe.getPackage();
        if(p == null){
            return "";
        }
        return p.getName();
    }
    
    public List<ObservableMethod> getMethods() throws ObservableClassException{
        List<ObservableMethod> list = new ArrayList<>();
        try{
            for(Method method : this.classe.getDeclaredMethods()){
                list.add(new ObservableMethod(method));
            }
        }
        catch(NoClassDefFoundError ex){
            throw new ObservableClassException("Classe indéfinie.");
        }
        return list;
    }
    
    public List<ObservableField> getFields() throws ObservableClassException{
        List<ObservableField> list = new ArrayList<>();
        try{
            for(Field field : this.classe.getDeclaredFields()){
                list.add(new ObservableField(field));
            }
        }
        catch(NoClassDefFoundError ex){
            throw new ObservableClassException("Classe indéfinie.");
        }
        return list;
    }
    
    public ObservableClass getSuperClass() throws ObservableClassException{
        try{
            Class superClass = this.classe.getSuperclass();
            if(superClass == null){
                return null;
            }
            return new ObservableClass(superClass);
        }
        catch(NoClassDefFoundError ex){
            throw new ObservableClassException("Classe indéfinie.");
        }
    }
    
    public List<ObservableClass> getInterfaces() throws ObservableClassException{
        List<ObservableClass> list = new ArrayList<>();
        try{
            for(Class impl : this.classe.getInterfaces()){
                list.add(new ObservableClass(impl));
            }
        }
        catch(NoClassDefFoundError ex){
            throw new ObservableClassException("Classe indéfinie.");
        }
        return list;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
