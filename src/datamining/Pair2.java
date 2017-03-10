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
 */

//a subclass of Pair mainly used to override the standard Java hashcode function
//to use a different hashing function for Multistep and MultiStage hashing in PCY
class Pair2 <T1, T2> extends Pair <T1, T2> {

    public Pair2(T1 t1, T2 t2) {
        super(t1, t2);
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash = 103 * hash + Objects.hashCode(this.obj1);
        hash = 53 * hash + Objects.hashCode(this.obj2);
        return hash;
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

}
