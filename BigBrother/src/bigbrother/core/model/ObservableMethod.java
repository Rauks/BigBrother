/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

import java.lang.reflect.Method;

/**
 *
 * @author Karl
 */
public class ObservableMethod {
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
        if(this.method.isAccessible()){
            return Visibility.UNLOCKED;
        }
        return Visibility.LOCKED;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
