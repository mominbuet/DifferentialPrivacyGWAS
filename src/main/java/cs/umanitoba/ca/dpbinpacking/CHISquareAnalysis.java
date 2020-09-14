/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author azizmma
 */
public class CHISquareAnalysis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        int top = 10;
        Scanner sc = new Scanner(new File("tdt_count_data.txt"));
        String[] originalOrder = new String[]{"IGR1369a_2", "IGR1373a_1", "IGR1369a_1", "IGR1371a_1", "IGR2008a_2",
            "IGR1143a_1", "IGR1218a_2", "IGR2008a_1", "IGR1219a_2", "IGR2020a_1"};//,"IGR1144a_1","IGR1367a_1","IGR1118a_1"
//        String[] originalOrder = new String[]{"pos100979512", "pos99207698", "pos99846806", "pos99367796", "pos99847004",
//            "pos99459309", "pos99971517", "pos100373107", "pos99956141", "pos100009588"};
//        , "pos99105224", "pos99704903","pos100966849","pos100818491","pos99893686","pos99917751","pos101287017","pos99833153","pos99486038","pos99177369"};
        List<String> originalList = Arrays.asList(originalOrder).subList(0, top);
//        List<String> myList =new ArrayList<>();
        List<SNP> snpList = new ArrayList<SNP>();
        Map<SNP, Double> snpOrder = new HashMap<>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] next = line.split("\\s+");
            if (next.length > 3) {
                snpList.add(new SNP(next[0], Integer.parseInt(next[1]), Integer.parseInt(next[2])));
            }
        }
//        double epsilon_global_max = 3, epsilon_global_min = .5;//class a
//        double epsilon_global_max = 1, epsilon_global_min = .2;//class b
        double epsilon_global_max = .25, epsilon_global_min = .05;//class c
        int match = 0;
        for (int i = 0; i < 20; i++) {

            ArrayList<DPAlgorithms> algos = GWASDP.init_DPAlgos(epsilon_global_max, epsilon_global_min);
            DPAlgorithms current_algo = GWASDP.getAlgo(new Random(), algos, true);
            for (SNP snp : snpList) {

//            double updated_b = snp.b + noise;
//            GWASDP.checkNoise(snp.N, noise, current_algo, algos);
//            noise = GWASDP.getLaplaceNoise(current_algo);
//            double updated_c = snp.c + noise;
//            GWASDP.checkNoise(snp.N, noise, current_algo, algos);
//            double val = (Math.pow(updated_b - updated_c, 2) / (updated_b+updated_c);
                if (snp.b > 0) {
                    double noise = GWASDP.getLaplaceNoise(current_algo);
                    //local laplace
                    int c1 = new Random().nextInt(snp.c);
                    int c2 = new Random().nextInt(snp.c - c1);
                    int c3 = snp.c - c1 - c2;
                    int b1 = new Random().nextInt(snp.b);
                    int b2 = new Random().nextInt(snp.b - b1);
                    int b3 = snp.b - b1 - b2;

                    //p1
                    b1 += GWASDP.getLaplaceNoise(current_algo);;
                    GWASDP.checkNoise(snp.N / 3, noise, current_algo, algos);
                    current_algo = GWASDP.getAlgo(new Random(), algos, true);
                    noise = GWASDP.getLaplaceNoise(current_algo);
                    c1 += noise;

                    //p2
                    algos = GWASDP.init_DPAlgos(epsilon_global_max, epsilon_global_min);
                    current_algo = GWASDP.getAlgo(new Random(), algos, true);
                    b2 += GWASDP.getLaplaceNoise(current_algo);;
                    GWASDP.checkNoise(snp.N / 3, noise, current_algo, algos);
                    current_algo = GWASDP.getAlgo(new Random(), algos, true);
                    noise = GWASDP.getLaplaceNoise(current_algo);
                    c2 += noise;

                    //p3
                    algos = GWASDP.init_DPAlgos(epsilon_global_max, epsilon_global_min);
                    current_algo = GWASDP.getAlgo(new Random(), algos, true);
                    b3 += GWASDP.getLaplaceNoise(current_algo);;
                    GWASDP.checkNoise(snp.N / 3, noise, current_algo, algos);
                    current_algo = GWASDP.getAlgo(new Random(), algos, true);
                    noise = GWASDP.getLaplaceNoise(current_algo);
                    c3 += noise;

                    double val = (Math.pow(b1 + b2 + b3 - c1 - c2 - c3, 2) / (c1 + b1 + c3 + b3 + c2 + b2));
//                    GWASDP.checkNoise(snp.N, noise, current_algo, algos);
//                      GWASDP.checkNoise(current_algo, algos);
                    snpOrder.put(snp, val);
                }
            }
//        LinkedHashMap sortedByValueMap = snpOrder.entrySet().stream()
//                .sorted(comparing(Entry<Key,Value>::getValue).thenComparing(Entry::getKey))     //first sorting by Value, then sorting by Key(entries with same value)
//                .collect(LinkedHashMap::new,(map,entry) -> map.put(entry.getKey(),entry.getValue()),LinkedHashMap::putAll);
            Map<SNP, Double> topK
                    = snpOrder.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(originalList.size())
                    .collect(Collectors.toMap(
                                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            for (Entry<SNP, Double> entrySet : topK.entrySet()) {
                if (originalList.contains(entrySet.getKey().name)) {
                    match++;
                }
//                System.out.println(entrySet.getKey().name + ":" + entrySet.getValue());
            }

        }
        System.out.println(match + " acc " + (match * 5.0 / (originalList.size())));
    }

    static class SNP {

        String name;
        int b, c;
        int N;

        public SNP(String name, int b, int c) {
            this.name = name;
            this.b = b;
            this.c = c;
            this.N = this.c + this.b;
        }

    }
}
