/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author azizmma
 */
public class GetFrequencies {

    final static NumberFormat formatter = new DecimalFormat("#0.00");

    public static String getRandomSeq(int length, String s1, int prob1, String s2) {
        String seq = "";
        for (int j = 0; j < length; j++) {
            int rand = ThreadLocalRandom.current().nextInt(1, 95);
            if (rand < prob1) {
                seq += s1;
            } else {
                seq += s2;
            }
            if (j % 2 != 0) {
                seq += " ";
            }
        }
        return seq;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        File fl = new File("freq-asn2.frq");
        Scanner sc = new Scanner(fl);
        int i = 0, j = 1;
        Map<Double, Integer> ratioCount = new TreeMap<>();
        while (sc.hasNext()) {
            String next = sc.nextLine();
            i++;
            if (i < 2) {
                continue;
            }
            String[] parts = next.split("\t");
            if (parts.length > 5) {
                double tmp1 = Double.parseDouble(formatter.format(Double.parseDouble(parts[4].split(":")[1])));
                double tmp2 = Double.parseDouble(formatter.format(Double.parseDouble(parts[5].split(":")[1])));
                if (tmp1 < .95 && tmp1 > 0) {
                    System.out.println(parts[4] + "--" + parts[5]+"--"+parts[1]);
                }
            }
        }
//        for (Map.Entry<Double, Integer> entrySet : ratioCount.entrySet()) {
//            if (entrySet.getKey() > 0) {
//                System.out.println(entrySet.getKey() + "\t" + entrySet.getValue());
//            }
//        }
    }

}
