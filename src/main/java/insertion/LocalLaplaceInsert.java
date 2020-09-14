/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package insertion;

import DB.DBQuery;
import DB.GwasLocalLaplace;
import DB.GwasOriginal;
import cs.umanitoba.ca.dpbinpacking.DPAlgorithms;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.checkNoise;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.getAlgo;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.getLaplaceNoise;
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
public class LocalLaplaceInsert {

    public static int MAXSNPS = 2000;
    public static int MAXSQS = 500;
    public static int MAXDO = 3;
    final static NumberFormat formatter = new DecimalFormat("#0.000");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        List<LocalSequenceInsert.SNPs> snps = new ArrayList<>();
        String[] classes = new String[]{
            "classA", "classB", "classC"};
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
        double[] epsilon_global_max = new double[]{3, 1, .25};
        double[] epsilon_global_min = new double[]{0.5, .2, .05};
//        double epsilon_global_max = 1, epsilon_global_min = .2;
        for (int i = 0; i < MAXSNPS; i++) {

            for (int cc = 0; cc < 2; cc++) {

                int maj_maj = 0, maj_min = 0, min_maj = 0, min_min = 0;

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
                }
                for (int j = 0; j < classes.length; j++) {
                    //3 data owners; too lazy to make it n ;)
                    double maj_maj_dp=0, maj_min_dp=0, min_maj_dp=0, min_min_dp = 0;

                    for (int dataOnwer = 0; dataOnwer < MAXDO; dataOnwer++) {
                        ArrayList<DPAlgorithms> algosDO1 = init_DPAlgos(epsilon_global_max[j], epsilon_global_min[j]);
                        double[] resDO1 = getStat((int) maj_maj / MAXDO, (int) maj_min / MAXDO, (int) min_maj / MAXDO, (int) min_min / MAXDO, algosDO1);
                        maj_maj_dp += resDO1[0];
                        maj_min_dp += resDO1[1];
                        min_maj_dp += resDO1[2];
                        min_min_dp += resDO1[3];

                    }
//                System.out.println("");
//                System.out.println(maj_maj + " " + maj_min + " " + min_maj + " " + min_min);
//                System.out.println(maj_maj_dp + " " + maj_min_dp + " " + min_maj_dp + " " + min_min_dp + " " + noise);
                    GwasLocalLaplace gwasLocal = new GwasLocalLaplace();
                    gwasLocal.setCasecontrol(cc);
                    gwasLocal.setDpClass(classes[j]);
                    gwasLocal.setMajormajor(maj_maj_dp);
                    gwasLocal.setMajorminor(maj_min_dp);
                    gwasLocal.setMinormajor(min_maj_dp);
                    gwasLocal.setMinorminor(min_min_dp);
                    gwasLocal.setSnpid(snps.get(i).pos + "");
                    dBQuery.insertGeneric(gwasLocal);

                }

                GwasOriginal gwasOriginal = new GwasOriginal();
                gwasOriginal.setCasecontrol(cc);
                gwasOriginal.setMajormajor(maj_maj);
                gwasOriginal.setMajorminor(maj_min);
                gwasOriginal.setMinormajor(min_maj);
                gwasOriginal.setMinorminor(min_min);
                gwasOriginal.setSnpid(snps.get(i).pos + "");
                dBQuery.insertGeneric(gwasOriginal);
            }
        }
    }

    public static double[] getStat(int maj_maj, int maj_min, int min_maj, int min_min, ArrayList<DPAlgorithms> algos) {
        double maj_maj_dp = 0, maj_min_dp = 0, min_maj_dp = 0, min_min_dp = 0;
        DPAlgorithms current_algo = getAlgo(new Random(), algos, true);
        double noise = getLaplaceNoise(current_algo);
        maj_maj_dp = maj_maj + noise;
        checkNoise(maj_maj, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, true);
        noise = getLaplaceNoise(current_algo);
        maj_min_dp = maj_min + noise;
        checkNoise(maj_min, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, true);
        noise = getLaplaceNoise(current_algo);
        min_maj_dp = min_maj + noise;
        checkNoise(min_maj, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, true);
        noise = getLaplaceNoise(current_algo);
        min_min_dp = min_min + noise;
        checkNoise(min_min, noise, current_algo, algos);
        return new double[]{maj_maj_dp, maj_min_dp, min_maj_dp, min_min_dp};
    }
}
