/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package insertion;

import DB.DBQuery;
import DB.SequenceLocal;
import DB.SequenceUnchanged;
import cs.umanitoba.ca.dpbinpacking.DPAlgorithms;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.checkNoise;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.getAlgo;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.init_DPAlgos;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author azizmma
 */
public class LocalSequenceInsert {

    public static class SNPs {

        public char c1, c2;
        public int pos;
        public double ratio_c1;

        public SNPs(char c1, char c2, int pos, double ratio_c1) {
            this.c1 = c1;
            this.c2 = c2;
            this.pos = pos;
            this.ratio_c1 = ratio_c1;
        }

    }
    public static int MAXSNPS = 3500;
    public static int MAXSQS = 10000;
    final static NumberFormat formatter = new DecimalFormat("#0.00");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        List<SNPs> snps = new ArrayList<>();
        Scanner sc = new Scanner(new File("freq_refined.txt"));
        while (sc.hasNext()) {
            String[] next = sc.nextLine().split("--");
            double ratio1 = Double.parseDouble(formatter.format(Double.parseDouble(next[0].split(":")[1])));
            char c1 = next[0].split(":")[0].charAt(0);
            char c2 = next[1].split(":")[0].charAt(0);
            snps.add(new SNPs(c1, c2, Integer.parseInt(next[2]), ratio1));
        }
        DBQuery dBQuery = new DBQuery();
//        Random random = new Random();
        int changed = 0, total = 0;
        double epsilon_global_max = 3, epsilon_global_min = .5;

        char[][] created_snps = new char[MAXSQS][MAXSNPS];
        char[][] DPSNPs = new char[MAXSQS][MAXSNPS];
        for (int i = 0; i < MAXSQS; i++) {
            ArrayList<DPAlgorithms> algos = init_DPAlgos(epsilon_global_max, epsilon_global_min);
            for (int j = 0; j < MAXSNPS; j++) {
                created_snps[i][j] = ((new Random()).nextDouble() > snps.get(j).ratio_c1) ? snps.get(j).c2 : snps.get(j).c1;
                DPAlgorithms current_algo = getAlgo(new Random(), algos, true);
                double threshold = 1 / (1 + Math.exp(current_algo.current_epsilon));
                if (new Random().nextDouble() < threshold) {
                    DPSNPs[i][j] = (created_snps[i][j] == snps.get(j).c1) ? snps.get(j).c2 : snps.get(j).c1;
                    changed++;
                    checkNoise(current_algo, algos);
                } else {
                    DPSNPs[i][j] = created_snps[i][j];
                    total++;
                }
            }
        }
        System.out.println("changed " + changed + " unchanged " + total);
        for (int i = 0; i < MAXSQS; i++) {
            String DPString = new String(DPSNPs[i]);

            SequenceLocal sequenceLocal = new SequenceLocal();
            sequenceLocal.setDpclass("classA");
            sequenceLocal.setOwner(i % 2500);
            sequenceLocal.setSequence(DPString);

            String OriginalString = new String(created_snps[i]);
            SequenceUnchanged sequenceUnchanged = new SequenceUnchanged();

            sequenceUnchanged.setOwner(i % 2500);
            sequenceUnchanged.setSequence(OriginalString);

            dBQuery.insertGeneric(sequenceLocal);
            dBQuery.insertGeneric(sequenceUnchanged);
        }
    }

}
