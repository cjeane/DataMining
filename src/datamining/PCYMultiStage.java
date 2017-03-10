/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.util.*;
import java.io.*;

/**
 *
 * @author Cory
 */
public final class PCYMultiStage {

    private static int threshold;
    private static BitSet bitVector1, bitVector2;
    private static int bucketSize = 65536;

    private PCYMultiStage() {
    }

    //sets the threshold for support
    public static void setThreshold(int t) {
        threshold = t;
    }

    //sets the bucket size
    public static void setBucketSize(int num) {
        bucketSize = num;
    }

    //returns the set of pairs which meets the threshold and their support number
    public static LinkedHashMap<Pair, Integer> findCandidatePairs(String fileName) {

        HashMap<Integer, Integer> freqItem = firstPassPCYMultiStage(fileName);
        secondPassPCYMultiStage(fileName, freqItem);

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
                        Pair <Integer, Integer>temp = new Pair<>(list.get(i), list.get(j));
                        Pair2 <Integer, Integer>temp2 = new Pair2<>(list.get(i), list.get(j));
                        if (!bitVector1.get(Math.abs(temp.hashCode()) % bucketSize)
                                || !bitVector2.get(Math.abs(temp2.hashCode()) % bucketSize)) {//if bit vector 1 is not found skip iteration
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

    //prunes the list for non-frequent entries
    private static void removeNonFreqPair(LinkedHashMap<Pair, Integer> map) {

        Iterator<Map.Entry<Pair, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Pair, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    //the second pass for multistage where make use of the bitvector1 must be called after firstPass and create bit vector2
    //with another hashing functions
    private static void secondPassPCYMultiStage(String fileName, HashMap<Integer, Integer> freqItem) {
        String line;
        String numsLine[];
        Integer key;
        ArrayList<Integer> list = new ArrayList<>();
        Bucket secondBucket = new Bucket(bucketSize);
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
                        Pair <Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));
                        if (!bitVector1.get(Math.abs(temp.hashCode()) % bucketSize)) {//if bit vector 1 is not found skip iteration
                            continue;
                        }
                        Pair2 <Integer, Integer> temp2 = new Pair2<>(list.get(i), list.get(j));
                        if (secondBucket.containsKey(temp2)) {
                            secondBucket.put(temp2, secondBucket.get(temp2) + 1);
                        } else {
                            secondBucket.put(temp2, 1);
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
        bitVector2 = secondBucket.convertToBitSet(threshold);
    }

    //the first pass for multistage where we find the frequent singleton items and create the bit vector1 for frequent pairs
    private static HashMap<Integer, Integer> firstPassPCYMultiStage(String fileName) {
        HashMap<Integer, Integer> pass1 = new HashMap<>();
        Bucket firstBucket = new Bucket(bucketSize);
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
        bitVector1 = firstBucket.convertToBitSet(threshold);

        firstBucket = null; //Marked for removal for GC
        return pass1;
    }

    //prunes hashmap for nonfrequent items
    private static void removeNonFreqItem(HashMap<Integer, Integer> map) {

        Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    //checks if an amount is consider frequent
    private static boolean isFreq(int item) {
        return item >= threshold;
    }
}
