/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

import DB.DBQuery;
import DB.GwasPlaintext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.apache.commons.math3.distribution.LaplaceDistribution;

/**
 *
 * @author azizmma
 */
public class GWASDP {

    /**
     * @param args the command line arguments
     */
//    static double privacy_loss = 0.0;
    public static void main(String[] args) {
        DBQuery dBQuery = new DBQuery();
        Random random = new Random();
        List<GwasPlaintext> res = dBQuery.getAllSNP();
//        double epsilon_global_max = 1, epsilon_global_min = .2;//B
//        double epsilon_global_max = 3, epsilon_global_min = .5;//A

        double[] epsilon_global_max = new double[]{3, 1, .25};
        double[] epsilon_global_min = new double[]{0.5, .2, .05};
        String[] classes = new String[]{"classA", "classB", "classC"};
//        double epsilon_global_max = .25, epsilon_global_min = .05;//C

//        int[] tests = {10, 100, 1000};
        int[] tests = {1000};

        if (res != null) {
            for (int j = 0; j < classes.length; j++) {
                System.out.println(classes[j]);
                double[][] mismatches = new double[10][2];
                for (int iter = 0; iter < 10; iter++) {

                    for (int test : tests) {
                        ArrayList<DPAlgorithms> algos = init_DPAlgos(epsilon_global_max[j], epsilon_global_min[j]);
                        List<Double> privacy_loss = new ArrayList<>();
                        int mismatch = 0;

                        for (int i = 0; i < test; i++) {

                            int rnd = random.nextInt(res.size() - 1);
                            GwasPlaintext snpA = res.get(rnd);
                            while (snpA.getCasecontrol() == 1) {
                                snpA = res.get(random.nextInt(res.size() - 1));
                            }

                            GwasPlaintext snpB = dBQuery.getFromSnip(snpA.getSnpid(), snpA.getCasecontrol() == 0 ? 1 : 0);
                            while (Objects.equals(snpA.getId(), snpB.getId())) {
                                snpB = res.get(new Random().nextInt(res.size() - 1));
                            }
//                        double[] priv = calculateLD(snpA, algos, true);
//                        double[] noPriv = calculateLD(snpA, algos, false);

//                    
                            double[] noPriv = calculateHWE(snpA, algos, false);
                            double[] priv = calculateHWE(snpA, algos, true);//
//                    int noPriv = calculateFET(snpA, snpB, algos, false);
//                    int priv = calculateFET(snpA, snpB, algos, true);
//                    mismatch += Math.abs(priv - noPriv);
//                            double[] priv = calculateCATT(snpA, snpB, algos, true);
//                            double[] noPriv = calculateCATT(snpA, snpB, algos, false);
                            mismatch += Math.abs(priv[0] - noPriv[0]);

                            privacy_loss.add(priv[1]);
//                    System.out.println(priv[1]);
                        }
                        double full = 0.0;
                        for (DPAlgorithms algo : algos) {
                            full += algo.selection_prob;
//                    System.out.println(algo.selection_prob + "," + algo.getCurrent_epsilon());
                        }
                        System.out.println(test + "\t" + (test - mismatch) / (test * .01) + "\t" + privacy_loss.stream().mapToDouble(a -> a).average().getAsDouble());
                        mismatches[iter][0] = (test - mismatch) / (test * .01);
                        mismatches[iter][1] = privacy_loss.stream().mapToDouble(a -> a).average().getAsDouble();
                    }

                }
//                System.out.println(Arrays.toString(mismatches));
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
                    return algo;

                }
            }
//            privacy_loss += current_algo.getCurrent_epsilon();

//            System.out.println(current_algo.getCurrent_epsilon()+","+current_algo.getId());
        }
        return current_algo;
    }

    public static double getPosLaplaceNoise(DPAlgorithms current_algo) {
        LaplaceDistribution LDistro = new LaplaceDistribution((current_algo.getEpsilon_max() - current_algo.getEpsilon_min()) / 2, 1 / current_algo.getCurrent_epsilon());
        return LDistro.sample();
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
            current_algo.enforce_penalty2();
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
        float sum = 0;
        for (int i = 0; i < algos.size(); i++) {
            sum += algos.get(i).getSelection_prob();
        }
//        if (sum < 1) {
//            algos.get(0).add_selection(1 - sum / 10);
//        } else
        if (sum > 1.0000005) {
            System.out.println("ERROR! probability >1, it is " + sum);
        }

    }

    public static void checkNoiseonEpsilon(double N, double noise, DPAlgorithms current_algo, ArrayList<DPAlgorithms> algos) {
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

    public static void checkNoise(double N, double noise, DPAlgorithms current_algo, ArrayList<DPAlgorithms> algos) {
        /**
         * Penalty enforced here
         */

        if (Math.abs(noise) > Math.sqrt(N)) {
            double reward = current_algo.getSelection_prob();
            current_algo.enforce_penalty2();
            reward -= current_algo.getSelection_prob();
            for (int i = 0; i < algos.size(); i++) {
                if (i + 1 != current_algo.id) {
                    algos.get(i).add_selection(reward / (algos.size() - 1));
                }

            }

        }
    }

    private static double[] calculateLD(GwasPlaintext snpA, ArrayList<DPAlgorithms> algos, boolean dp) {
        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);
        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double total_epsilon = 0.0;
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double N = OriginalN + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        total_epsilon += current_algo.getCurrent_epsilon();

        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_AB_d = snpA.getMajormajor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();

        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Ab_d = snpA.getMajorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();

        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aB_d = snpA.getMinormajor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();

        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_ab_d = snpA.getMinorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();

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

        return new double[]{((D_prime == 0.0) ? 0 : 1), total_epsilon};
    }

    private static double[] calculateHWE(GwasPlaintext snpA, ArrayList<DPAlgorithms> algos, boolean dp) {
        double OriginalN = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();

        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);
        double total_epsilon = 0.0;

        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        total_epsilon += current_algo.getCurrent_epsilon();
        double N_AA_d = snpA.getMajormajor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        total_epsilon += current_algo.getCurrent_epsilon();
        double N_Aa_d = snpA.getMajorminor() + noise;
        checkNoise(OriginalN, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        total_epsilon += current_algo.getCurrent_epsilon();
        double N_aa_d = snpA.getMinorminor() + noise;
//        current_algo = getAlgo(new Random(), algos, dp);
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
        return new double[]{((chi_square >= 3.841) ? 0 : 1), total_epsilon};
    }

    private static double[] calculateCATT(GwasPlaintext snpA, GwasPlaintext snpB, ArrayList<DPAlgorithms> algos, boolean dp) {
        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();
        double total_epsilon = 0.0;
        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);

        double noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;
        double N_Aa_case_d = snpA.getMajorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;
        double N_aa_case_d = snpA.getMinorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;
        double N_Aa_control_d = snpB.getMajorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getPosLaplaceNoise(current_algo) : 0;
        double N_aa_control_d = snpB.getMajorminor() + noise;
        total_epsilon += current_algo.getCurrent_epsilon();
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

        return new double[]{((chi_square >= 3.841) ? 1 : 0), total_epsilon};
    }

    private static double[] calculateFET(GwasPlaintext snpA, GwasPlaintext snpB, ArrayList<DPAlgorithms> algos, boolean dp) {

        double OriginalNCase = snpA.getMajormajor() + snpA.getMajorminor() + snpA.getMinormajor() + snpA.getMinorminor();
        double OriginalNControl = snpB.getMajormajor() + snpB.getMajorminor() + snpB.getMinormajor() + snpB.getMinorminor();
        double total_epsilon = 0.0;

        DPAlgorithms current_algo = getAlgo(new Random(), algos, dp);

        double noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_AA_case_d = snpA.getMajormajor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Aa_case_d = snpA.getMajorminor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aa_case_d = snpA.getMinorminor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNCase, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;

        double case_sum = N_AA_case_d + N_Aa_case_d + N_aa_case_d;

        double N_AA_control_d = snpB.getMajormajor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_Aa_control_d = snpB.getMajorminor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNControl, noise, current_algo, algos);
        current_algo = getAlgo(new Random(), algos, dp);
//        total_epsilon += current_algo.current_epsilon;
        noise = (dp) ? getLaplaceNoise(current_algo) : 0;
        double N_aa_control_d = snpB.getMinorminor() + noise;
        total_epsilon += current_algo.current_epsilon;
        checkNoise(OriginalNControl, noise, current_algo, algos);
//        current_algo = getAlgo(new Random(), algos, dp);

        double control_sum = N_AA_control_d + N_Aa_control_d + N_aa_control_d;
        double sum = case_sum + control_sum;

        double AA_sum = N_AA_case_d + N_AA_control_d;
        double Aa_sum = N_Aa_case_d + N_Aa_control_d;
        double aa_sum = N_aa_case_d + N_aa_control_d;

        //int lob = factorial(case_sum)*factorial(control_sum) * factorial(AA_sum) * factorial(Aa_sum) * factorial(aa_sum);
        //int hor = factorial(N_AA_control_d) * factorial(N_Aa_control_d) * factorial(N_aa_control_d) * factorial(N_AA_case_d) * factorial(N_Aa_case_d) * factorial(N_aa_case_d) * factorial(sum);
//        int denominator = factorial(5) * factorial(4) * factorial(3) * factorial(3) * factorial(3);
//        int numerator = factorial(1) * factorial(2) * factorial(2) * factorial(2) * factorial(1) * factorial(1) * factorial(19);
        //float p_value = fisher23(70,20,10,40,30,30,0);  //denominator/(float)numerator;
        //float p_value = fisher23(0,3,2,6,5,1,0);
        double p_value = FisherTest.fisher23(N_AA_control_d, N_Aa_control_d, N_aa_control_d, N_AA_case_d, N_Aa_case_d, N_aa_case_d, 0);

        //float p_value = (factorial(case_sum) * factorial(control_sum) * factorial(AA_sum) * factorial(Aa_sum) * factorial(aa_sum)) / (float)(factorial(N_AA_control_d) * factorial(N_Aa_control_d)
        //* factorial(N_aa_control_d) * factorial(N_AA_case_d) * factorial(N_Aa_case_d) * factorial(N_aa_case_d) * factorial(sum));
        //float p_value = (double)(factorial(20) * factorial(2) * factorial(1) * factorial(2) * factorial(2)) / (double)(factorial(3) * factorial(4)
        //* factorial(4) * factorial(4) * factorial(4) * factorial(4) * factorial(4));
        //printf("%f \n", p_value);
        //df = 1, critical chi_square value = 3.841
        //null hypothesis: no statistical association between genotype and disease
        //fetResult = (p_value < 0.05)? "1":"0";
//        return (p_value < 0.05) ? 1 : 0;
        return new double[]{((p_value < 0.05) ? 1 : 0), total_epsilon};
    }

    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }

}
