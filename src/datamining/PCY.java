/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.io.*;
import java.util.*;

/**
 *
 * @author Cory
 */
public final class PCY {

    private static int threshold;
    private static BitSet bitVector;
    private static int bucketSize = 65536;

    private PCY() {
    }
    //sets the Threshold for support
    public static void setThreshold(int t) {
        threshold = t;
    }

    //sets the bucket size for to be created for the hashmap
    public static void setBucketSize(int num) {
        bucketSize = num;
    }

    //return the set of frequent pairs and their number of support
    public static LinkedHashMap<Pair, Integer> findCandidatePairs(String fileName) {
        HashMap<Integer, Integer> freqItem = firstPassPCY(fileName);
        LinkedHashMap<Pair, Integer> candidatesPair = new LinkedHashMap<>();
        String line;
        String numsLine[];
        Integer key;
        ArrayList<Integer> list = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                numsLine = line.split("\\s+");
                for (String num : numsLine) {
                    key = Integer.parseInt(num);
                    if (freqItem.containsKey(key) && !list.contains(key)) {
                        list.add(key);//was freq from the first pass after pruning
                    }
                }
                Collections.sort(list);//just incase the input baskets were not not ordered
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        Pair<Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));//creates pair combinations
                        if (!bitVector.get(Math.abs(temp.hashCode()) % bucketSize)) {//if bit vector is not found skip iteration
                            continue;
                        }
                        if (candidatesPair.containsKey(temp)) {
                            candidatesPair.put(temp, candidatesPair.get(temp) + 1);
                        } else {
                            candidatesPair.put(temp, 1);
                        }
                    }
                }
                list.clear();

            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }//while Block

        removeNonFreqPair(candidatesPair);
        return candidatesPair;
    }

    //prunes the hashmap for non frequent entries
    private static void removeNonFreqPair(LinkedHashMap<Pair, Integer> map) {

        Iterator<Map.Entry<Pair, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Pair, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    //the first pass for pcy sets a bitvector and finds the frequent singleton from file
    private static HashMap<Integer, Integer> firstPassPCY(String fileName) {
        HashMap<Integer, Integer> pass1 = new HashMap<>();
        Bucket firstBucket = new Bucket(bucketSize);
        String line = "";
        String numsLine[];
        Integer key;
        ArrayList<Integer> list = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                numsLine = line.split("\\s+");
                for (String num : numsLine) {
                    key = Integer.parseInt(num);
                    if (pass1.containsKey(key)) {
                        pass1.put(key, pass1.get(key) + 1);
                    } else {
                        pass1.put(key, 1);
                    }
                    if (!list.contains(key)) {
                        list.add(key);
                    }
                }
                Collections.sort(list);//just incase the input baskets were not not ordered
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        Pair <Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));

                        if (firstBucket.containsKey(temp)) {
                            firstBucket.put(temp, firstBucket.get(temp) + 1);
                        } else {
                            firstBucket.put(temp, 1);
                        }
                    }
                }
                list.clear();

            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }//while Block

        removeNonFreqItem(pass1);
        bitVector = firstBucket.convertToBitSet(threshold);

        firstBucket = null; //Marked for removal for GC
        return pass1;
    }

    //prunes the list for non frequent singleton item
    private static void removeNonFreqItem(HashMap<Integer, Integer> map) {

        Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    private static boolean isFreq(int item) {
        return item >= threshold;
    }
}
