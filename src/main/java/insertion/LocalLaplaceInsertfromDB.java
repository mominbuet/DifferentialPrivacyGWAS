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
import static cs.umanitoba.ca.dpbinpacking.GWASDP.init_DPAlgos;
import static insertion.LocalLaplaceInsert.getStat;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author azizmma
 */
public class LocalLaplaceInsertfromDB {

    public static int MAXSNPS = 2000;
    public static int MAXSQS = 500;
    public static int MAXDO = 9;
    final static NumberFormat formatter = new DecimalFormat("#0.000");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
//        List<LocalSequenceInsert.SNPs> snps = new ArrayList<>();
        String[] classes = new String[]{
            "classA", "classB", "classC"};

        DBQuery dBQuery = new DBQuery();
        List<GwasOriginal> resLocal = dBQuery.getAllOriginalSNP();

//        Random random = new Random();
        int changed = 0, total = 0;
        double[] epsilon_global_max = new double[]{3, 1, .25};
        double[] epsilon_global_min = new double[]{0.5, .2, .05};
//        double epsilon_global_max = 1, epsilon_global_min = .2;
        ArrayList<DPAlgorithms>[] algosDO = new ArrayList[classes.length];
        for (int i = 0; i < classes.length; i++) {
            algosDO[i] = init_DPAlgos(epsilon_global_max[i], epsilon_global_min[i]);
        }
        for (GwasOriginal gwasOriginal : resLocal) {
            int maj_maj = gwasOriginal.getMajormajor(), maj_min = gwasOriginal.getMajorminor(), min_maj = gwasOriginal.getMinormajor(), min_min = gwasOriginal.getMinorminor();
            for (int j = 0; j < classes.length; j++) {
                //3 data owners; too lazy to make it n ;)
                double maj_maj_dp = 0, maj_min_dp = 0, min_maj_dp = 0, min_min_dp = 0;

                for (int dataOnwer = 0; dataOnwer < MAXDO; dataOnwer++) {

                    double[] resDO1 = getStat((int) maj_maj / MAXDO, (int) maj_min / MAXDO, (int) min_maj / MAXDO, (int) min_min / MAXDO, algosDO[j]);
                    maj_maj_dp += resDO1[0];
                    maj_min_dp += resDO1[1];
                    min_maj_dp += resDO1[2];
                    min_min_dp += resDO1[3];

                }
//                System.out.println("");
//                System.out.println(maj_maj + " " + maj_min + " " + min_maj + " " + min_min);
//                System.out.println(maj_maj_dp + " " + maj_min_dp + " " + min_maj_dp + " " + min_min_dp + " " + noise);
                GwasLocalLaplace gwasLocal = new GwasLocalLaplace();
                gwasLocal.setCasecontrol(gwasOriginal.getCasecontrol());
                gwasLocal.setDpClass(classes[j]);
                gwasLocal.setMajormajor(maj_maj_dp);
                gwasLocal.setMajorminor(maj_min_dp);
                gwasLocal.setMinormajor(min_maj_dp);
                gwasLocal.setMinorminor(min_min_dp);
                gwasLocal.setSnpid(gwasOriginal.getSnpid());
                dBQuery.insertGeneric(gwasLocal);

            }
        }

    }

}
