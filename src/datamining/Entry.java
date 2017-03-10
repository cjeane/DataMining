/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

/**
 *
 * @author Cory
 */

//Basic Implementation of an Key, Value Entry for dictionary not handled for collsiion

public class Entry {

    final Pair key;
    Integer value;    

    public Entry(Pair key, Integer val) {
        this.key = key;
        value = val;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer val) {
        value = val;
    }

    public Pair getKey() {
        return key;
    }

    public String toString() {

        return "[" + key + "=>" + value + "]";
    }
}
