/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Karl
 */
public class ObservableMethod{
    private final Method method;
    
    ObservableMethod(Method method) {
        this.method = method;
    }
    
    public String getName(){
        return this.method.getName();
    }
    
    public ObservableClass getReturnType(){
        return new ObservableClass(this.method.getReturnType());
    }
    
    public Visibility getVisibility(){
        if(Modifier.isPrivate(this.method.getModifiers())){
            return Visibility.PRIVATE;
        }
        if(Modifier.isProtected(this.method.getModifiers())){
            return Visibility.PROTECTED;
        }
        return Visibility.PUBLIC;
    }
    
    public boolean isStatic(){
        return Modifier.isStatic(this.method.getModifiers());
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
