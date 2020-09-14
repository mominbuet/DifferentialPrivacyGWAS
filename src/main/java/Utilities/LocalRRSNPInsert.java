/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import DB.DBQuery;
import DB.GwasLocal;
import DB.GwasOriginal;
import DB.GwasOriginalLocal;
import cs.umanitoba.ca.dpbinpacking.DPAlgorithms;
import cs.umanitoba.ca.dpbinpacking.GWASDP;
import insertion.LocalSequenceInsert;
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
public class LocalRRSNPInsert {

    final static NumberFormat formatter = new DecimalFormat("#0.00");

    public static int MAXSNPS = 2000;
    public static int MAXSQS = 500;
    public static int MAXDO = 3;

    /**
     * @param args the command line arguments
     */
    public static double priv_loss = 0.0;

    public static void main(String[] args) throws FileNotFoundException {
        List<LocalSequenceInsert.SNPs> snps = new ArrayList<>();
        Scanner sc = new Scanner(new File("freq_refined.txt"));
        while (sc.hasNext()) {
            String[] next = sc.nextLine().split("--");
            double ratio1 = Double.parseDouble(formatter.format(Double.parseDouble(next[0].split(":")[1])));
            char c1 = next[0].split(":")[0].charAt(0);
            char c2 = next[1].split(":")[0].charAt(0);
            snps.add(new LocalSequenceInsert.SNPs(c1, c2, Integer.parseInt(next[2]), ratio1));
        }
        DBQuery dBQuery = new DBQuery();
//        Random random = new Random();
        int changed = 0, total = 0;
//        String className = "classC";
//        double epsilon_global_max = .25, epsilon_global_min = .05;
//        String className = "classA";
//        double epsilon_global_max = 3, epsilon_global_min = .5;
        String className = "classB";
        double epsilon_global_max = 1, epsilon_global_min = .2;
        for (int i = 0; i < MAXSNPS; i++) {

            for (int cc = 0; cc < 2; cc++) {

                ArrayList<DPAlgorithms> algos = init_DPAlgos(epsilon_global_max, epsilon_global_min);
                int maj_maj = 0, maj_min = 0, min_maj = 0, min_min = 0;
                int maj_maj_dp = 0, maj_min_dp = 0, min_maj_dp = 0, min_min_dp = 0;
                int noise = 0;
                double priv_loss_loc = 0;
                for (int j = 0; j < MAXSQS; j++) {

                    char c1 = ((new Random()).nextDouble() > snps.get(i).ratio_c1) ? snps.get(i).c2 : snps.get(i).c1;
                    char c2 = ((new Random()).nextDouble() > snps.get(i).ratio_c1) ? snps.get(i).c2 : snps.get(i).c1;
                    if (c1 == c2) {
                        if (c1 == snps.get(i).c1) {
                            maj_maj++;
                        } else {
                            min_min++;
                        }
                    } else if (c1 == snps.get(i).c1) {
                        maj_min++;
                    } else {
                        min_maj++;
                    }

                    DPAlgorithms current_algo = getAlgo(new Random(), algos, true);
                    double threshold = 1 / (1 + Math.exp(current_algo.current_epsilon));
//                    System.out.print(Double.parseDouble((new DecimalFormat("#0.000")).format(threshold)) + ",");

                    if (new Random().nextDouble() < threshold) {
                        if (new Random().nextDouble() < .5) {
                            c1 = (c1 == snps.get(i).c1) ? snps.get(i).c2 : snps.get(i).c1;
                        } else {
                            c2 = (c2 == snps.get(i).c2) ? snps.get(i).c1 : snps.get(i).c2;
                        }
                        noise++;
                        priv_loss_loc += current_algo.getCurrent_epsilon();

                    }

                    GWASDP.checkNoiseLocal(MAXSQS, noise, current_algo, algos);

                    if (c1 == c2) {
                        if (c1 == snps.get(i).c1) {
                            maj_maj_dp++;
                        } else {
                            min_min_dp++;
                        }
                    } else if (c1 == snps.get(i).c1) {
                        maj_min_dp++;
                    } else {
                        min_maj_dp++;
                    }

                }
                priv_loss += priv_loss_loc / MAXSQS;
//                System.out.println("");
//                System.out.println(maj_maj + " " + maj_min + " " + min_maj + " " + min_min);
//                System.out.println(maj_maj_dp + " " + maj_min_dp + " " + min_maj_dp + " " + min_min_dp + " " + noise);
                GwasLocal gwasLocal = new GwasLocal();
                gwasLocal.setCasecontrol(cc);
                gwasLocal.setDpClass(className);
                gwasLocal.setMajormajor(maj_maj_dp);
                gwasLocal.setMajorminor(maj_min_dp);
                gwasLocal.setMinormajor(min_maj_dp);
                gwasLocal.setMinorminor(min_min_dp);
                gwasLocal.setSnpid(snps.get(i).pos + "");
                dBQuery.insertGeneric(gwasLocal);

                GwasOriginalLocal gwasOriginal = new GwasOriginalLocal();
                gwasOriginal.setCasecontrol(cc);
//                gwasOriginal.setDpClass(className);
                gwasOriginal.setMajormajor(maj_maj);
                gwasOriginal.setMajorminor(maj_min);
                gwasOriginal.setMinormajor(min_maj);
                gwasOriginal.setMinorminor(min_min);
                gwasOriginal.setSnpid(snps.get(i).pos + "");
                dBQuery.insertGeneric(gwasOriginal);

            }
        }
        System.out.println(className + " Priv loss " + priv_loss / MAXSNPS);
    }
}
