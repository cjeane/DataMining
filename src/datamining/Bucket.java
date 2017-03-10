/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.util.BitSet;

/**
 *
 * @author Cory
 */

//Datastructure for hash buckets basically an array implement via Java standards
//for hold entries
public class Bucket {

    private int SIZE = 65536;// default intial size 2^20; hopefully less than all possible candidate pairs to simulate collision
    private Entry table[];

    public Bucket() {
        table = new Entry[SIZE];
    }

    public int getSize() {
        return SIZE;
    }

    public Bucket(int num) {
        SIZE = num;
        table = new Entry[SIZE];
    }

    //Hard code type of Integer for return for Value
    public Integer get(Pair key) {
        int hash = Math.abs(key.hashCode() % SIZE);
        Entry ent = table[hash];
        return ent.getValue();

    }

    //No collision setup because of the need to convert this to a bit vector later    
    //Else once we get to the bitvector we can't determine which bucket the pair belong to accurately
    //To simulate using near max memory 
    public void put(Pair k, Integer val) {
        int hash = Math.abs(k.hashCode() % SIZE);
        Entry ent = table[hash];

        if (ent != null) {
            ent.value = val;
        } else {
            Entry newEntry = new Entry(k, val);
            table[hash] = newEntry;
        }
    }
    
    //checks if that hash location already has an entry 
    public boolean containsKey(Pair key) {
        int hash = Math.abs(key.hashCode() % SIZE);
        return (table[hash] != null);
    }

    public String toString() {
        String res = "{";
        for (Entry e : table) {
            if (e != null) {
                res += e;
            }
        }
        return res += "}";
    }

    //returns the current bucket structure as a bit vector
    public BitSet convertToBitSet(Integer threshold) {
        BitSet vector = new BitSet(SIZE);

        for (int i = 0; i < SIZE; i++) {
            if (table[i] != null && table[i].getValue() >= threshold) {
                vector.set(i);
            }
        }

        return vector;
    }

}
