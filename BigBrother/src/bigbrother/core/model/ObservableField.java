/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author Karl
 */
public class ObservableField{
    private final Field field;

    ObservableField(Field field) {
        this.field = field;
    }
    
    public String getName(){
        return this.field.getName();
    }
    
    public ObservableClass getType(){
        return new ObservableClass(this.field.getType());
    }
    
    public Visibility getVisibility(){
        if(Modifier.isPrivate(this.field.getModifiers())){
            return Visibility.PRIVATE;
        }
        if(Modifier.isProtected(this.field.getModifiers())){
            return Visibility.PROTECTED;
        }
        return Visibility.PUBLIC;
    }
    
    public boolean isStatic(){
        return Modifier.isStatic(this.field.getModifiers());
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
