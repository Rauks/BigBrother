/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bigbrother.core.model;

/**
 *
 * @author Karl
 */
public enum Visibility {
    PUBLIC("Publique"),
    PRIVATE("Privée"),
    PROTECTED("Protégée");
     
    private final String name;

    private Visibility(String name){
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
