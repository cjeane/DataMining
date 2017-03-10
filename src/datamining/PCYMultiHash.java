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
public final class PCYMultiHash {

    private static int threshold;
    private static BitSet bitVector1, bitVector2;
    private static int bucketSize = 65536;

    private PCYMultiHash() {

    }

    //sets the threshold for support
    public static void setThreshold(int t) {
        threshold = t;
    }

    //sets the max size of the array for buckets
    public static void setBucketSize(int num) {
        bucketSize = num;
    }

    //returns the Hashmap of pairs thats meet the threshold and their count
    public static LinkedHashMap<Pair, Integer> findCandidatePairs(String fileName) {

        LinkedHashMap<Pair, Integer> candidatesPair = new LinkedHashMap<>();
        String line;
        String numsLine[];
        Integer key;
        ArrayList<Integer> list = new ArrayList<>();

        HashMap<Integer, Integer> freqItem = firstPassPCYMultiHash(fileName);

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
                        Pair<Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));
                        Pair2<Integer, Integer> temp2 = new Pair2<>(list.get(i), list.get(j));
                        if (!bitVector1.get(Math.abs(temp.hashCode()) % (bucketSize / 2))
                                || !bitVector2.get(Math.abs(temp2.hashCode()) % (bucketSize / 2))) {//if bit vector 1 or 2 is not found skip iteration
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

    //prunes the list for non frequent pairs
    private static void removeNonFreqPair(LinkedHashMap<Pair, Integer> map) {

        Iterator<Map.Entry<Pair, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Pair, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    //The first passover data for Multihash
    private static HashMap<Integer, Integer> firstPassPCYMultiHash(String fileName) {
        HashMap<Integer, Integer> pass1 = new HashMap<>();
        Bucket firstBucket = new Bucket(bucketSize / 2);// the two hashmap/bucket structures
        Bucket secondBucket = new Bucket(bucketSize / 2);// To simulate multihash standard bucket size is cut in half
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
                    if (!list.contains(key)) {//ensuring list is unique
                        list.add(key);
                    }
                }
                Collections.sort(list);//just incase the input baskets were not not ordered for hashing
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        Pair<Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));
                        Pair2<Integer, Integer> temp2 = new Pair2<>(list.get(i), list.get(j));
                        //hashs to first bucket
                        if (firstBucket.containsKey(temp)) {
                            firstBucket.put(temp, firstBucket.get(temp) + 1);
                        } else {
                            firstBucket.put(temp, 1);
                        }
                        //hashs to second bucket
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

        removeNonFreqItem(pass1);
        bitVector1 = firstBucket.convertToBitSet(threshold / 2);//makes bit vectors from buckets
        bitVector2 = secondBucket.convertToBitSet(threshold / 2);
        firstBucket = null; //Marked for removal for GC
        secondBucket = null;
        return pass1;
    }

    //prunes all entries that doesn't meet threshold aka non frequent
    private static void removeNonFreqItem(HashMap<Integer, Integer> map) {

        Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
    }

    //boolean function to check if an amount is frequent
    private static boolean isFreq(int item) {
        return item >= threshold;
    }

}
