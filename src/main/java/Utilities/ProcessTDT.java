/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import DB.DBQuery;
import DB.GwasLocal;
import DB.GwasLocalLaplace;
import static Utilities.GetFrequencies.formatter;
import cs.umanitoba.ca.dpbinpacking.DPAlgorithms;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.checkNoise;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.getAlgo;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.init_DPAlgos;
import static insertion.LocalLaplaceInsert.MAXSQS;
import insertion.LocalSequenceInsert;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author azizmma
 */
public class ProcessTDT {

    public static char setLocal(ArrayList<DPAlgorithms> algos, char c_in, LocalSequenceInsert.SNPs snp) {
        char res_c = c_in;
        DPAlgorithms current_algo = getAlgo(new Random(), algos, true);
        double threshold = 1 / (1 + Math.exp(current_algo.current_epsilon));
//                    System.out.print(Double.parseDouble((new DecimalFormat("#0.000")).format(threshold)) + ",");
        if (new Random().nextDouble() < threshold) {
            res_c =(snp.c1==c_in)?snp.c2:snp.c1;
            checkNoise(current_algo, algos);
        }
        return res_c;
    }

    public static String get_name(char c1, char c2, char major_allele) {
        if (c1 == c2) {
            if (c1 == major_allele) {
                return "M_M";
            } else {
                return "m_m";
            }
            /*} else if (c1 == major_allele) {
             return "M_m";*/
        } else {
            return "M_m";
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Map<Integer, LocalSequenceInsert.SNPs> snps = new TreeMap<>();
        Scanner sc = new Scanner(new File("freq_refined.txt"));
        while (sc.hasNext()) {
            String[] next = sc.nextLine().split("--");
            double ratio1 = Double.parseDouble(formatter.format(Double.parseDouble(next[0].split(":")[1])));
            char c1 = next[0].split(":")[0].charAt(0);
            char c2 = next[1].split(":")[0].charAt(0);
            snps.put(Integer.parseInt(next[2]), new LocalSequenceInsert.SNPs(c1, c2, Integer.parseInt(next[2]), ratio1));
        }

//                double epsilon_global_max = 3, epsilon_global_min = .5;//class a
//        double epsilon_global_max = 1, epsilon_global_min = .2;//class b
        double epsilon_global_max = .25, epsilon_global_min = .05;//class c
        ArrayList<DPAlgorithms> algos = init_DPAlgos(epsilon_global_max, epsilon_global_min);
//        DBQuery dBQuery = new DBQuery();
        for (Map.Entry<Integer, LocalSequenceInsert.SNPs> entrySet : snps.entrySet()) {
            int maj_maj = 0, maj_min = 0, min_maj = 0, min_min = 0;
            int snpID = entrySet.getKey();
            Map<String, Integer> dataMap = new TreeMap<>();
            for (int j = 0; j < MAXSQS; j++) {

                char c1 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c1 = setLocal(algos, c1, snps.get(snpID));
                char c2 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c2 = setLocal(algos, c2, snps.get(snpID));
                char c3 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c3 = setLocal(algos, c3, snps.get(snpID));
                char c4 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c4 = setLocal(algos, c4, snps.get(snpID));
                char c5 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c5 = setLocal(algos, c5, snps.get(snpID));
                char c6 = ((new Random()).nextDouble() > snps.get(snpID).ratio_c1) ? snps.get(snpID).c2 : snps.get(snpID).c1;
                c6 = setLocal(algos, c6, snps.get(snpID));
                
                String tmp = get_name(c1, c2, snps.get(snpID).c1) + "+" + get_name(c3, c4, snps.get(snpID).c1) + "=" + get_name(c5, c6, snps.get(snpID).c1);
                if (dataMap.containsKey(tmp)) {
                    dataMap.put(tmp, dataMap.get(tmp) + 1);
                } else {
                    dataMap.put(tmp, 1);
                }
            }
            System.out.print("1 pos" + entrySet.getKey() + " 0 " + entrySet.getKey() + " ");
            for (Map.Entry<String, Integer> entrySet1 : dataMap.entrySet()) {
                System.out.print(entrySet1.getKey() + " " + entrySet1.getValue() + " ");
            }
            System.out.println("");
//            GwasLocal gwasLocal = dBQuery.getFromLocalSnip(entrySet.getKey()+"",1,"classA");
        }

    }

}
