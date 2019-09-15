/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

/**
 *
 * @author leon
 */
public class Attribute <T>{
    /*public T getValue();
    public void setValue(T newValue) throws IllegalStateException;*/
    private T val;
    private final String key;
    public Attribute(String key, T defValue){
        val = defValue;
        this.key = key;
    }
    public String getKey(){
        return key;
    }
    public T getValue(){
        return val;
    }
    public void setValue(T newVal){
        val = newVal;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Attribute)
            return ((Attribute)obj).key.equals(key);
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
