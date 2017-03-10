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
public final class APri {

    private static int threshold;

    private APri() {
        threshold = 0;
    }

    //sets the threshold for support
    public static void setThreshold(int t) {
        threshold = t;
    }

    //finds the Set of pairs which are frequent pair
    public static LinkedHashMap<Pair, Integer> freqPairCandidates(String fileName) {
        HashMap<Integer, Integer> freqSingleItems = freqItemCounts(fileName);
        LinkedHashMap<Pair, Integer> candidatesPair = new LinkedHashMap<>();
        String line = "";
        String numsLine[];
        Integer key;
        ArrayList<Integer> list = new ArrayList<>();

        try {//try catch block for opening file and parsing file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                numsLine = line.split("\\s+");
                for (String num : numsLine) {
                    key = Integer.parseInt(num);
                    if (freqSingleItems.containsKey(key) && !list.contains(key)) {
                        list.add(key);//was freq from the first pass after pruning
                    }
                }
                Collections.sort(list);//just incase the input baskets were not not ordered
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        //create a temp Pair object to use for hashing the pair of nums
                        Pair<Integer, Integer> temp = new Pair<>(list.get(i), list.get(j));
                        if (candidatesPair.containsKey(temp)) {
                            candidatesPair.put(temp,
                                    candidatesPair.get(temp) + 1);
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

    //finds the frequent singletons
    private static HashMap<Integer, Integer> freqItemCounts(String fileName) {
        HashMap<Integer, Integer> pass1 = new HashMap<>();

        String line = "";
        String numsLine[];
        Integer key;

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
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }//while Block
        removeNonFreqItem(pass1);
        return pass1;
    }

    //prunes the list for non frequent singletons
    private static void removeNonFreqItem(HashMap<Integer, Integer> map) {

        Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, Integer> entry = iter.next();
            if (!isFreq(entry.getValue())) {
                iter.remove();
            }
        }
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
    //boolean function to check if an amount is frequent
    private static boolean isFreq(int item) {
        return item >= threshold;
    }

}
