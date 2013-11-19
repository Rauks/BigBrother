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

/**
 *
 * @author Karl
 */
public class ObservableClass {
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
        return this.classe.getSimpleName();
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
    
    public List<ObservableMethod> getMethods(){
        List<ObservableMethod> list = new ArrayList<>();
        for(Method method : this.classe.getDeclaredMethods()){
            list.add(new ObservableMethod(method));
        }
        return list;
    }
    
    public List<ObservableField> getFields(){
        List<ObservableField> list = new ArrayList<>();
        for(Field field : this.classe.getDeclaredFields()){
            list.add(new ObservableField(field));
        }
        return list;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
