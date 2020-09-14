/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

import DB.DBQuery;
import DB.GwasLocal;
import DB.GwasLocalLaplace;
import DB.GwasOriginal;
import DB.GwasOriginalLocal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.math3.distribution.LaplaceDistribution;

/**
 *
 * @author azizmma
 */
public class GWASLocalRRDP {

    /**
     * @param args the command line arguments
     */
    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.

     /**
     * @param args the command line arguments
     */
    static double privacy_loss = 0.0;

    public static void main(String[] args) {
        String[] class_names = new String[]{"classA", "classB", "classC"};//{"classA","classB","classC"};//,"classB", "classC"
        DBQuery dBQuery = new DBQuery();
        Random random = new Random();
        List<GwasOriginalLocal> resLocal = dBQuery.getAllGwasOriginalLocalSNP();
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

                            GwasOriginalLocal snpA = resLocal.get(rnd);
                            GwasLocal snpALocal = dBQuery.getFromLocalSnip(snpA.getSnpid(), snpA.getCasecontrol(), class_name);
//                        while (snpA.getCasecontrol() == 1) {
//                            snpA = res.get(random.nextInt(res.size() - 1));
//                        }
//                    while (snpALocal.getCasecontrol() == 1) {
//                        snpALocal = resLocal.get(random.nextInt(resLocal.size() - 1));
//                    }
                            GwasOriginalLocal snpB = dBQuery.getFromOriginalLocalSnip(snpA.getSnpid(), snpA.getCasecontrol() == 0 ? 1 : 0);
                            GwasLocal snpBLocal = dBQuery.getFromLocalSnip(snpALocal.getSnpid(), snpALocal.getCasecontrol() == 0 ? 1 : 0, class_name);

                            assert snpA.getSnpid().equals(snpALocal.getSnpid());
//                            assert snpB.getSnpid().equals(snpBLocal.getSnpid());
                            int noPriv, priv = 0;
                            noPriv = calculateCATT(snpA,snpB);
                            priv = calculateCATT(snpALocal,snpBLocal);
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

    public static ArrayList<DPAlgorithms> init_DPAlgos(double epsilon_global_max, double epsilon_global_min) {
        int n = (int) ((epsilon_global_max / epsilon_global_min) - 1);
//        System.out.println("Number of algo: " + n);
        ArrayList<DPAlgorithms> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            DPAlgorithms a = new DPAlgorithms(i, epsilon_global_max / i, epsilon_global_max / (i + 1), 1.0 / n);
            res.add(a);
        }
        return res;

    }

    public static DPAlgorithms getAlgo(Random random, ArrayList<DPAlgorithms> algos, boolean dp) {
        int select = random.nextInt(100);
        int current_probab = 0;
        DPAlgorithms current_algo = algos.get(algos.size() - 1);
        if (dp) {
            for (DPAlgorithms algo : algos) {
                current_probab += algo.getSelection_prob() * 100;
                if ((current_probab - (algo.getSelection_prob() * 100) < select) && (current_probab > select)) {
                    current_algo = algo;
                    break;
                }
            }
            privacy_loss += current_algo.getCurrent_epsilon();

//            System.out.println(current_algo.getCurrent_epsilon()+","+current_algo.getId());
        }
        return current_algo;
    }

    public static double getLaplaceNoise(DPAlgorithms current_algo) {
        LaplaceDistribution LDistro = new LaplaceDistribution(0, 1 / current_algo.getCurrent_epsilon());
        return LDistro.sample();
    }

    public static void checkNoiseLocal(int N, int noise, DPAlgorithms current_algo, ArrayList<DPAlgorithms> algos) {
        /**
         * Penalty enforced here
         */
        if (noise > Math.sqrt(N)) {
            double reward = current_algo.getSelection_prob();
            current_algo.enforce_penalty();
            reward -= current_algo.getSelection_prob();
            for (int i = 0; i < algos.size(); i++) {
                if (i + 1 != current_algo.id) {
                    algos.get(i).add_selection(reward / (algos.size() - 1));
                }

            }
        }
    }

    public static void checkNoise(DPAlgorithms current_algo, ArrayList<DPAlgorithms> algos) {
        /**
         * Penalty enforced here
         */

        double reward = current_algo.getSelection_prob();
        current_algo.enforce_penalty();
        reward -= current_algo.getSelection_prob();
        for (int i = 0; i < algos.size(); i++) {
            if (i + 1 != current_algo.id) {
                algos.get(i).add_selection(reward / (algos.size() - 1));
            }

        }

    }

    public static void checkNoise(double N, double noise, DPAlgorithms current_algo, ArrayList<DPAlgorithms> algos) {
        /**
         * Penalty enforced here
         */

        if (Math.abs(noise) > Math.sqrt(N)) {
            double reward = current_algo.getSelection_prob();
            current_algo.enforce_penalty();
            reward -= current_algo.getSelection_prob();
            for (int i = 0; i < algos.size(); i++) {
                if (i + 1 != current_algo.id) {
                    algos.get(i).add_selection(reward / (algos.size() - 1));
                }

            }

        }
    }

    public static int calculateLD(GwasOriginal snpA) {
        double noise = 0.0;

        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        double N_AB_d = snpA.getMajormajor() + noise;
        double N_Ab_d = snpA.getMajorminor() + noise;
        double N_aB_d = snpA.getMinormajor() + noise;
        double N_ab_d = snpA.getMinorminor() + noise;

        double P_AB = (N_AB_d / N);
        double P_Ab = N_Ab_d / N;
        double P_aB = N_aB_d / N;
        double P_ab = N_ab_d / N;

        double D = P_AB * P_ab - P_aB * P_Ab;
        double P_A = P_AB + P_Ab;
        double P_B = P_AB + P_aB;

        double D_max;
        if (D > 0) {
            D_max = Math.min(P_A * (1 - P_B), (1 - P_A) * P_B);
        } else {
            D_max = Math.min(P_A * P_B, (1 - P_A) * (1 - P_B));
        }

        double D_prime = Math.abs(D / D_max);

        return (D_prime == 0.0) ? 0 : 1;
    }

    public static int calculateLD(GwasLocalLaplace snpA) {
        double noise = 0.0;

        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        double N_AB_d = snpA.getMajormajor() + noise;
        double N_Ab_d = snpA.getMajorminor() + noise;
        double N_aB_d = snpA.getMinormajor() + noise;
        double N_ab_d = snpA.getMinorminor() + noise;

        double P_AB = (N_AB_d / N);
        double P_Ab = N_Ab_d / N;
        double P_aB = N_aB_d / N;
        double P_ab = N_ab_d / N;

        double D = P_AB * P_ab - P_aB * P_Ab;
        double P_A = P_AB + P_Ab;
        double P_B = P_AB + P_aB;

        double D_max;
        if (D > 0) {
            D_max = Math.min(P_A * (1 - P_B), (1 - P_A) * P_B);
        } else {
            D_max = Math.min(P_A * P_B, (1 - P_A) * (1 - P_B));
        }

        double D_prime = Math.abs(D / D_max);

        return (D_prime == 0.0) ? 0 : 1;
    }

    public static int calculateLD(GwasLocal snpA) {
        double noise = 0.0;

        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        double N_AB_d = snpA.getMajormajor() + noise;
        double N_Ab_d = snpA.getMajorminor() + noise;
        double N_aB_d = snpA.getMinormajor() + noise;
        double N_ab_d = snpA.getMinorminor() + noise;

        double P_AB = (N_AB_d / N);
        double P_Ab = N_Ab_d / N;
        double P_aB = N_aB_d / N;
        double P_ab = N_ab_d / N;

        double D = P_AB * P_ab - P_aB * P_Ab;
        double P_A = P_AB + P_Ab;
        double P_B = P_AB + P_aB;

        double D_max;
        if (D > 0) {
            D_max = Math.min(P_A * (1 - P_B), (1 - P_A) * P_B);
        } else {
            D_max = Math.min(P_A * P_B, (1 - P_A) * (1 - P_B));
        }

        double D_prime = Math.abs(D / D_max);

        return (D_prime == 0.0) ? 0 : 1;
    }

    public static int calculateLD(GwasOriginalLocal snpA) {
        double noise = 0.0;

        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        double N_AB_d = snpA.getMajormajor() + noise;
        double N_Ab_d = snpA.getMajorminor() + noise;
        double N_aB_d = snpA.getMinormajor() + noise;
        double N_ab_d = snpA.getMinorminor() + noise;

        double P_AB = (N_AB_d / N);
        double P_Ab = N_Ab_d / N;
        double P_aB = N_aB_d / N;
        double P_ab = N_ab_d / N;

        double D = P_AB * P_ab - P_aB * P_Ab;
        double P_A = P_AB + P_Ab;
        double P_B = P_AB + P_aB;

        double D_max;
        if (D > 0) {
            D_max = Math.min(P_A * (1 - P_B), (1 - P_A) * P_B);
        } else {
            D_max = Math.min(P_A * P_B, (1 - P_A) * (1 - P_B));
        }

        double D_prime = Math.abs(D / D_max);

        return (D_prime == 0.0) ? 0 : 1;
    }

    private static int calculateLD(GwasOriginal snpA, ArrayList<DPAlgorithms> algos, boolean dp) {
        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);
        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;

        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_AB_d = snpA.getMajormajor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Ab_d = snpA.getMajorminor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aB_d = snpA.getMinormajor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_ab_d = snpA.getMinorminor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);

        double P_AB = (N_AB_d / N);
        double P_Ab = N_Ab_d / N;
        double P_aB = N_aB_d / N;
        double P_ab = N_ab_d / N;

        double D = P_AB * P_ab - P_aB * P_Ab;
        double P_A = P_AB + P_Ab;
        double P_B = P_AB + P_aB;

        double D_max;
        if (D > 0) {
            D_max = Math.min(P_A * (1 - P_B), (1 - P_A) * P_B);
        } else {
            D_max = Math.min(P_A * P_B, (1 - P_A) * (1 - P_B));
        }

        double D_prime = Math.abs(D / D_max);

        return (D_prime == 0.0) ? 0 : 1;
    }

    public static int calculateHWE(GwasOriginal snpA, ArrayList<DPAlgorithms> algos, boolean dp) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();

        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);

        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_AA_d = snpA.getMajormajor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Aa_d = snpA.getMajorminor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aa_d = snpA.getMinorminor() + noise;
        current_algo = getAlgo(new Random(), algos, dp);
        checkNoise(OriginalN, noise, current_algo, algos);
//        noise = (dp) ? getLaplaceNoise(current_algo) : 0;

        double N = N_AA_d + N_Aa_d + N_aa_d;

        //step 2. P_A = (n_AA/n)+(0.5*(n_Aa/n))  Then, P_a = 1 - P_A
        double P_A = (N_AA_d / (double) N) + (0.5 * (N_Aa_d / N));
        double P_a = 1.0 - P_A;

        //step 3. Expected counts of AA= nP_A^2, Aa=2*nP_AP_a, aa=nP_a^2
        double N_AA_exp = N * P_A * P_A;
        double N_Aa_exp = 2 * N * P_A * P_a;
        double N_aa_exp = N * P_a * P_a;

        //step 4. Pearson goodness of fit test 
        double chi_square = (Math.pow((N_AA_d - N_AA_exp), 2) / N_AA_exp)
                + (Math.pow((N_Aa_d - N_Aa_exp), 2) / N_Aa_exp) + (Math.pow((N_aa_d - N_aa_exp), 2) / N_aa_exp);

        //0 for hwe doe not hold, 1 for hwe holds
        return (chi_square >= 3.841) ? 0 : 1;
    }

    public static int calculateHWE(GwasOriginalLocal snpA) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();

        double noise = 0;
        double N_AA_d = snpA.getMajormajor() + noise;
        double N_Aa_d = snpA.getMajorminor() + noise;
        double N_aa_d = snpA.getMinorminor() + noise;

        double N = N_AA_d + N_Aa_d + N_aa_d;

        //step 2. P_A = (n_AA/n)+(0.5*(n_Aa/n))  Then, P_a = 1 - P_A
        double P_A = (N_AA_d / (double) N) + (0.5 * (N_Aa_d / N));
        double P_a = 1.0 - P_A;

        //step 3. Expected counts of AA= nP_A^2, Aa=2*nP_AP_a, aa=nP_a^2
        double N_AA_exp = N * P_A * P_A;
        double N_Aa_exp = 2 * N * P_A * P_a;
        double N_aa_exp = N * P_a * P_a;

        //step 4. Pearson goodness of fit test 
        double chi_square = (Math.pow((N_AA_d - N_AA_exp), 2) / N_AA_exp)
                + (Math.pow((N_Aa_d - N_Aa_exp), 2) / N_Aa_exp) + (Math.pow((N_aa_d - N_aa_exp), 2) / N_aa_exp);

        //0 for hwe doe not hold, 1 for hwe holds
        return (chi_square >= 3.841) ? 0 : 1;
    }

    public static int calculateHWE(GwasOriginal snpA) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();

        double noise = 0;
        double N_AA_d = snpA.getMajormajor() + noise;
        double N_Aa_d = snpA.getMajorminor() + noise;
        double N_aa_d = snpA.getMinorminor() + noise;

        double N = N_AA_d + N_Aa_d + N_aa_d;

        //step 2. P_A = (n_AA/n)+(0.5*(n_Aa/n))  Then, P_a = 1 - P_A
        double P_A = (N_AA_d / (double) N) + (0.5 * (N_Aa_d / N));
        double P_a = 1.0 - P_A;

        //step 3. Expected counts of AA= nP_A^2, Aa=2*nP_AP_a, aa=nP_a^2
        double N_AA_exp = N * P_A * P_A;
        double N_Aa_exp = 2 * N * P_A * P_a;
        double N_aa_exp = N * P_a * P_a;

        //step 4. Pearson goodness of fit test 
        double chi_square = (Math.pow((N_AA_d - N_AA_exp), 2) / N_AA_exp)
                + (Math.pow((N_Aa_d - N_Aa_exp), 2) / N_Aa_exp) + (Math.pow((N_aa_d - N_aa_exp), 2) / N_aa_exp);

        //0 for hwe doe not hold, 1 for hwe holds
        return (chi_square >= 3.841) ? 0 : 1;
    }

    public static int calculateHWE(GwasLocal snpA) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double noise = 0;
        double N_AA_d = snpA.getMajormajor() + noise;
        double N_Aa_d = snpA.getMajorminor() + noise;
        double N_aa_d = snpA.getMinorminor() + noise;

        double N = N_AA_d + N_Aa_d + N_aa_d;

        //step 2. P_A = (n_AA/n)+(0.5*(n_Aa/n))  Then, P_a = 1 - P_A
        double P_A = (N_AA_d / (double) N) + (0.5 * (N_Aa_d / N));
        double P_a = 1.0 - P_A;

        //step 3. Expected counts of AA= nP_A^2, Aa=2*nP_AP_a, aa=nP_a^2
        double N_AA_exp = N * P_A * P_A;
        double N_Aa_exp = 2 * N * P_A * P_a;
        double N_aa_exp = N * P_a * P_a;

        //step 4. Pearson goodness of fit test 
        double chi_square = (Math.pow((N_AA_d - N_AA_exp), 2) / N_AA_exp)
                + (Math.pow((N_Aa_d - N_Aa_exp), 2) / N_Aa_exp) + (Math.pow((N_aa_d - N_aa_exp), 2) / N_aa_exp);

        //0 for hwe doe not hold, 1 for hwe holds
        return (chi_square >= 3.841) ? 0 : 1;
    }

    public static int calculateHWE(GwasLocalLaplace snpA) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double noise = 0;//as already noise added in the database
        double N_AA_d = snpA.getMajormajor() + noise;
        double N_Aa_d = snpA.getMajorminor() + noise;
        double N_aa_d = snpA.getMinorminor() + noise;

        double N = N_AA_d + N_Aa_d + N_aa_d;

        //step 2. P_A = (n_AA/n)+(0.5*(n_Aa/n))  Then, P_a = 1 - P_A
        double P_A = (N_AA_d / (double) N) + (0.5 * (N_Aa_d / N));
        double P_a = 1.0 - P_A;

        //step 3. Expected counts of AA= nP_A^2, Aa=2*nP_AP_a, aa=nP_a^2
        double N_AA_exp = N * P_A * P_A;
        double N_Aa_exp = 2 * N * P_A * P_a;
        double N_aa_exp = N * P_a * P_a;

        //step 4. Pearson goodness of fit test 
        double chi_square = (Math.pow((N_AA_d - N_AA_exp), 2) / N_AA_exp)
                + (Math.pow((N_Aa_d - N_Aa_exp), 2) / N_Aa_exp) + (Math.pow((N_aa_d - N_aa_exp), 2) / N_aa_exp);

        //0 for hwe doe not hold, 1 for hwe holds
        return (chi_square >= 3.841) ? 0 : 1;
    }

    public static int calculateCATT(GwasOriginal snpA, GwasOriginal snpB, ArrayList<DPAlgorithms> algos, boolean dp) {
        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);

        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Aa_case_d = snpA.getMajorminor() + noise;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aa_case_d = snpA.getMinorminor() + noise;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Aa_control_d = snpB.getMajorminor() + noise;
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aa_control_d = snpB.getMajorminor() + noise;
        checkNoise(OriginalNControl, noise, current_algo, algos);

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        //codominant model (0,1,2) 
        double weight1 = 0.0;
        double weight2 = 1.0;
        double weight3 = 2.0;

        double T = weight1 * (N_AA_control_d * case_sum - N_AA_case_d * control_sum)
                + weight2 * (N_Aa_control_d * case_sum - N_Aa_case_d * control_sum)
                + weight3 * (N_aa_control_d * case_sum - N_aa_case_d * control_sum);

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        double var_T = ((control_sum * case_sum) / (double) (control_sum + case_sum))
                * (((weight1 * weight1) * (sum - AA_sum) * AA_sum
                + (weight2 * weight2) * (sum - Aa_sum) * Aa_sum
                + (weight3 * weight3) * (sum - aa_sum) * aa_sum)
                - (2 * ((Math.pow(weight1, 2) * Math.pow(weight2, 2) * AA_sum * Aa_sum) + ((Math.pow(weight2, 2) * Math.pow(weight2, 2) * Aa_sum * aa_sum)))));
        double chi_square = (T * T) / var_T;

        return (chi_square >= 3.841) ? 1 : 0;
    }

    public static int calculateCATT(GwasOriginalLocal snpAin, GwasOriginalLocal snpBin) {
        GwasOriginalLocal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasOriginalLocal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double noise = 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;

        double N_Aa_case_d = snpA.getMajorminor() + noise;

        double N_aa_case_d = snpA.getMinorminor() + noise;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;

        double N_Aa_control_d = snpB.getMajorminor() + noise;

        double N_aa_control_d = snpB.getMajorminor() + noise;

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        //codominant model (0,1,2) 
        double weight1 = 0.0;
        double weight2 = 1.0;
        double weight3 = 2.0;

        double T = weight1 * (N_AA_control_d * case_sum - N_AA_case_d * control_sum)
                + weight2 * (N_Aa_control_d * case_sum - N_Aa_case_d * control_sum)
                + weight3 * (N_aa_control_d * case_sum - N_aa_case_d * control_sum);

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        double var_T = ((control_sum * case_sum) / (double) (control_sum + case_sum))
                * (((weight1 * weight1) * (sum - AA_sum) * AA_sum
                + (weight2 * weight2) * (sum - Aa_sum) * Aa_sum
                + (weight3 * weight3) * (sum - aa_sum) * aa_sum)
                - (2 * ((Math.pow(weight1, 2) * Math.pow(weight2, 2) * AA_sum * Aa_sum) + ((Math.pow(weight2, 2) * Math.pow(weight2, 2) * Aa_sum * aa_sum)))));
        double chi_square = (T * T) / var_T;

        return (chi_square >= 3.841) ? 1 : 0;
    }

    public static int calculateCATT(GwasOriginal snpAin, GwasOriginal snpBin) {
        GwasOriginal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasOriginal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double noise = 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;

        double N_Aa_case_d = snpA.getMajorminor() + noise;

        double N_aa_case_d = snpA.getMinorminor() + noise;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;

        double N_Aa_control_d = snpB.getMajorminor() + noise;

        double N_aa_control_d = snpB.getMajorminor() + noise;

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        //codominant model (0,1,2) 
        double weight1 = 0.0;
        double weight2 = 1.0;
        double weight3 = 2.0;

        double T = weight1 * (N_AA_control_d * case_sum - N_AA_case_d * control_sum)
                + weight2 * (N_Aa_control_d * case_sum - N_Aa_case_d * control_sum)
                + weight3 * (N_aa_control_d * case_sum - N_aa_case_d * control_sum);

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        double var_T = ((control_sum * case_sum) / (double) (control_sum + case_sum))
                * (((weight1 * weight1) * (sum - AA_sum) * AA_sum
                + (weight2 * weight2) * (sum - Aa_sum) * Aa_sum
                + (weight3 * weight3) * (sum - aa_sum) * aa_sum)
                - (2 * ((Math.pow(weight1, 2) * Math.pow(weight2, 2) * AA_sum * Aa_sum) + ((Math.pow(weight2, 2) * Math.pow(weight2, 2) * Aa_sum * aa_sum)))));
        double chi_square = (T * T) / var_T;

        return (chi_square >= 3.841) ? 1 : 0;
    }

    public static int calculateCATT(GwasLocal snpAin, GwasLocal snpBin) {
        GwasLocal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasLocal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double noise = 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;

        double N_Aa_case_d = snpA.getMajorminor() + noise;

        double N_aa_case_d = snpA.getMinorminor() + noise;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;

        double N_Aa_control_d = snpB.getMajorminor() + noise;

        double N_aa_control_d = snpB.getMajorminor() + noise;

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        //codominant model (0,1,2) 
        double weight1 = 0.0;
        double weight2 = 1.0;
        double weight3 = 2.0;

        double T = weight1 * (N_AA_control_d * case_sum - N_AA_case_d * control_sum)
                + weight2 * (N_Aa_control_d * case_sum - N_Aa_case_d * control_sum)
                + weight3 * (N_aa_control_d * case_sum - N_aa_case_d * control_sum);

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        double var_T = ((control_sum * case_sum) / (double) (control_sum + case_sum))
                * (((weight1 * weight1) * (sum - AA_sum) * AA_sum
                + (weight2 * weight2) * (sum - Aa_sum) * Aa_sum
                + (weight3 * weight3) * (sum - aa_sum) * aa_sum)
                - (2 * ((Math.pow(weight1, 2) * Math.pow(weight2, 2) * AA_sum * Aa_sum) + ((Math.pow(weight2, 2) * Math.pow(weight2, 2) * Aa_sum * aa_sum)))));
        double chi_square = (T * T) / var_T;

        return (chi_square >= 3.841) ? 1 : 0;
    }

    public static int calculateCATT(GwasLocalLaplace snpAin, GwasLocalLaplace snpBin) {
        GwasLocalLaplace snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasLocalLaplace snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double noise = 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;

        double N_Aa_case_d = snpA.getMajorminor() + noise;

        double N_aa_case_d = snpA.getMinorminor() + noise;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;

        double N_Aa_control_d = snpB.getMajorminor() + noise;

        double N_aa_control_d = snpB.getMajorminor() + noise;

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        //codominant model (0,1,2) 
        double weight1 = 0.0;
        double weight2 = 1.0;
        double weight3 = 2.0;

        double T = weight1 * (N_AA_control_d * case_sum - N_AA_case_d * control_sum)
                + weight2 * (N_Aa_control_d * case_sum - N_Aa_case_d * control_sum)
                + weight3 * (N_aa_control_d * case_sum - N_aa_case_d * control_sum);

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        double var_T = ((control_sum * case_sum) / (double) (control_sum + case_sum))
                * (((weight1 * weight1) * (sum - AA_sum) * AA_sum
                + (weight2 * weight2) * (sum - Aa_sum) * Aa_sum
                + (weight3 * weight3) * (sum - aa_sum) * aa_sum)
                - (2 * ((Math.pow(weight1, 2) * Math.pow(weight2, 2) * AA_sum * Aa_sum) + ((Math.pow(weight2, 2) * Math.pow(weight2, 2) * Aa_sum * aa_sum)))));
        double chi_square = (T * T) / var_T;

        return (chi_square >= 3.841) ? 1 : 0;
    }

    public static int calculateFET(GwasOriginalLocal snpAin, GwasOriginalLocal snpBin) {
        GwasOriginalLocal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasOriginalLocal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double N_AA_case_d = snpA.getMajormajor();
        double N_Aa_case_d = snpA.getMajorminor();
        double N_aa_case_d = snpA.getMinorminor();
        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor();
        double N_Aa_control_d = snpB.getMajorminor();
        double N_aa_control_d = snpB.getMinorminor();

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;
        if (N_AA_control_d <= 0 || N_Aa_control_d <= 0 || N_aa_control_d <= 0 || N_AA_case_d <= 0 || N_Aa_case_d <= 0 || N_aa_case_d <= 0) {
            return -1;
        }
        double p_value = FisherTest.fisher23(N_AA_control_d, N_Aa_control_d, N_aa_control_d, N_AA_case_d, N_Aa_case_d, N_aa_case_d, 0);
        return (p_value < 0.05) ? 1 : 0;
    }

    public static int calculateFET(GwasLocalLaplace snpAin, GwasLocalLaplace snpBin) {
        GwasLocalLaplace snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasLocalLaplace snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double N_AA_case_d = snpA.getMajormajor();
        double N_Aa_case_d = snpA.getMajorminor();
        double N_aa_case_d = snpA.getMinorminor();
        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor();
        double N_Aa_control_d = snpB.getMajorminor();
        double N_aa_control_d = snpB.getMinorminor();

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;
        if (N_AA_control_d <= 0 || N_Aa_control_d <= 0 || N_aa_control_d <= 0 || N_AA_case_d <= 0 || N_Aa_case_d <= 0 || N_aa_case_d <= 0) {
            return -1;
        }
        double p_value = FisherTest.fisher23(N_AA_control_d, N_Aa_control_d, N_aa_control_d, N_AA_case_d, N_Aa_case_d, N_aa_case_d, 0);
        return (p_value < 0.05) ? 1 : 0;
    }

    private static int calculateFET(GwasLocal snpAin, GwasLocal snpBin) {
        GwasLocal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasLocal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double N_AA_case_d = snpA.getMajormajor();
        double N_Aa_case_d = snpA.getMajorminor();
        double N_aa_case_d = snpA.getMinorminor();
        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor();
        double N_Aa_control_d = snpB.getMajorminor();
        double N_aa_control_d = snpB.getMinorminor();

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;
        if (N_AA_control_d <= 0 || N_Aa_control_d <= 0 || N_aa_control_d <= 0 || N_AA_case_d <= 0 || N_Aa_case_d <= 0 || N_aa_case_d <= 0) {
            return -1;
        }
        double p_value = FisherTest.fisher23(N_AA_control_d, N_Aa_control_d, N_aa_control_d, N_AA_case_d, N_Aa_case_d, N_aa_case_d, 0);
        return (p_value < 0.05) ? 1 : 0;
    }

    public static int calculateFET(GwasOriginal snpAin, GwasOriginal snpBin) {

        GwasOriginal snpA = snpAin.getCasecontrol() == 0 ? snpAin : snpBin;
        GwasOriginal snpB = snpBin.getCasecontrol() == 1 ? snpBin : snpAin;

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();

        double N_AA_case_d = snpA.getMajormajor();
        double N_Aa_case_d = snpA.getMajorminor();
        double N_aa_case_d = snpA.getMinorminor();
        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor();
        double N_Aa_control_d = snpB.getMajorminor();
        double N_aa_control_d = snpB.getMinorminor();

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

//        System.out.println(N_AA_control_d + " " + N_Aa_control_d + " " + N_aa_control_d + " " + N_AA_case_d + " " + N_Aa_case_d + " " + N_aa_case_d);
        double p_value = FisherTest.fisher23(N_AA_control_d, N_Aa_control_d, N_aa_control_d, N_AA_case_d, N_Aa_case_d, N_aa_case_d, 0);
        return (p_value < 0.05) ? 1 : 0;
    }

    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}
