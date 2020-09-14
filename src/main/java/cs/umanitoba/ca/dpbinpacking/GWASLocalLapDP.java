/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

import DB.DBQuery;

import DB.GwasLocalLaplace;
import DB.GwasOriginal;
import java.util.List;
import java.util.Random;

/**
 *
 * @author azizmma
 */
public class GWASLocalLapDP {

    /**
     * @param args the command line arguments
     */
    static double privacy_loss = 0.0;
//    static int DataOwners = 2;

    public static void main(String[] args) {
        String[] class_names = new String[]{"classA", "classB", "classC"};//,"classB", "classC"
        DBQuery dBQuery = new DBQuery();
        Random random = new Random();
//        List<GwasLocalLaplace> resLocal = dBQuery.getAllLocalLapSNP();
        List<GwasOriginal> resLocal = dBQuery.getAllOriginalSNP();
        for (String class_name : class_names) {
//            System.out.println("Class " + class_name);
//            List<GwasOriginal> res = dBQuery.getAllOriginalSNP();
//        int[] tests = {10, 100, 1000};
            int[] tests = {1000};

            for (int ii = 0; ii < 10; ii++) {
                if (resLocal != null) {
                    for (int test : tests) {
                        int mismatch = 0;
                        for (int i = 0; i < test; i++) {

                            int rnd = random.nextInt(resLocal.size() - 1);

//                        GwasLocalLaplace snpALocal = resLocal.get(rnd);
                            GwasOriginal snpA = resLocal.get(rnd);
                            GwasLocalLaplace snpALocalLap = dBQuery.getFromLocalLapSnip(snpA.getSnpid(), snpA.getCasecontrol(), class_name);
//                        GwasLocal snpA = dBQuery.getFromOriginalSnip(snpALocal.getSnpid(), snpALocal.getCasecontrol());

//                        while (snpA.getCasecontrol() == 1) {
//                            snpA = res.get(random.nextInt(res.size() - 1));
//                        }
//                    while (snpALocal.getCasecontrol() == 1) {
//                        snpALocal = resLocal.get(random.nextInt(resLocal.size() - 1));
//                    }
                            GwasLocalLaplace snpBLocalLap = (dBQuery.getFromLocalLapSnip(snpALocalLap.getSnpid(), snpALocalLap.getCasecontrol() == 0 ? 1 : 0, class_name));

                            GwasOriginal snpB = (dBQuery.getFromOriginalSnip(snpALocalLap.getSnpid(), snpALocalLap.getCasecontrol() == 0 ? 1 : 0));
                            assert snpALocalLap.getSnpid().equals(snpA.getSnpid());
                            assert snpBLocalLap.getSnpid().equals(snpB.getSnpid());
                            int noPriv, priv = 0;
                            noPriv = GWASLocalRRDP.calculateFET(snpA,snpB);
                            priv = GWASLocalRRDP.calculateFET(snpALocalLap,snpBLocalLap);
//                        noPriv = calculateHWE(snpA);
//                        priv = calculateHWE(snpALocal);

                            if (priv >= 0) {
                                mismatch += Math.abs(priv - noPriv);
                            } else {
                                i -= 1;
                            }
                        }
                        System.out.println(class_name + "\t" + test + "\t" + (test - mismatch) / (test * .01));
                    }

                }
            }

        }
//        System.out.println(privacy_loss);

    }

}
