/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.util.Objects;

/**
 *
 * @author Cory
 * @param <T1>
 * @param <T2>
 */

//data structure for holding pairs of ints
public class Pair<T1, T2> {

    public T1 obj1;
    public T2 obj2;

    public Pair(T1 t1, T2 t2) {
        obj1 = t1;
        obj2 = t2;
    }

    public void setPair(T1 t1, T2 t2) {
        obj1 = t1;
        obj2 = t2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        return (obj1 == ((Pair) obj).obj1) && (obj2 == ((Pair) obj).obj2);
    }

    @Override
    //a simple hash function for a pair of ints order dependent
    public int hashCode() {
        int hash = 421;
        hash = 17 * hash + Objects.hashCode(this.obj1);
        hash = 53 * hash + Objects.hashCode(this.obj2);
        return hash;
    }

    @Override
    public String toString() {
        return "(" + obj1 + ", " + obj2 + ")";
    }

}
