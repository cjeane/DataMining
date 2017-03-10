/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamining;

import java.io.*;

/**
 *
 * @author Cory
 */
public class DataMiningDriver {

    private static boolean DEBUG = true;//set to true to view the returns pairs and support

    public static void main(String[] args) throws IOException {//Test Driver

        long startTime;
        long endTime;
        long totalTime;
        int maxBuckets = 65536;//bucket size to use can set according to memory default 65536 size pass this has degrading performance
        int[] percent = {1, 5, 10};//the percentage for support

        for (int p : percent) {
            for (int c = 1; c <= 12; c++) {//

                String testFile = "dataSet" + c + ".txt";
                int size = countLines(testFile);
                System.out.println(testFile);
                System.out.println("Buckets " + size + " read in at " + p + "% support.");
                int threshold = calcThreshold(size, p);

                startTime = System.currentTimeMillis();
                APri.setThreshold(threshold);
                if (!DEBUG) {
                    APri.freqPairCandidates(testFile);
                } else {
                    System.out.println(APri.freqPairCandidates(testFile));
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("APri: " + totalTime + "ms.");

                startTime = System.currentTimeMillis();
                PCY.setThreshold(threshold);
                PCY.setBucketSize(maxBuckets);//BucketSize affects PCY GREATLY run comparsion later to represent maxed out Memory
                if (!DEBUG) {
                    PCY.findCandidatePairs(testFile);
                } else {
                    System.out.println(PCY.findCandidatePairs(testFile));
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("PCY: " + totalTime + "ms.");

                startTime = System.currentTimeMillis();
                PCYMultiStage.setThreshold(threshold);
                PCYMultiStage.setBucketSize(maxBuckets);//BucketSize affects PCY GREATLY run comparsion later to represent maxed out Memory
                if (!DEBUG) {
                    PCYMultiStage.findCandidatePairs(testFile);
                } else {
                    System.out.println(PCYMultiStage.findCandidatePairs(testFile));
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("PCY-MultiStage: " + totalTime + "ms.");

                startTime = System.currentTimeMillis();
                PCYMultiHash.setThreshold(threshold);
                PCYMultiHash.setBucketSize(maxBuckets * 2);//BucketSize affects PCY GREATLY run comparsion later to represent maxed out Memory
                if (!DEBUG) {
                    PCYMultiHash.findCandidatePairs(testFile);
                } else {
                    System.out.println(PCYMultiHash.findCandidatePairs(testFile));
                }
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("PCY-MultiHash: " + totalTime + "ms.\n");
            }
        }
    }

    //returns an int for the amount of support needed for require for the 
    //desired percentage
    public static int calcThreshold(int total, double percent) {
        if (percent > 100) {
            return total;
        } else if (percent < 0) {
            return 0;
        } else {
            return (int) Math.round((percent / 100) * total);
        }
    }

    //quick count for amount of buckets we are reading in
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

}//Driver
