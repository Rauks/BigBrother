/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

import java.lang.reflect.Field;

/**
 *
 * @author Karl
 */
public class ObservableField {
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
        if(this.field.isAccessible()){
            return Visibility.UNLOCKED;
        }
        return Visibility.LOCKED;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
}
