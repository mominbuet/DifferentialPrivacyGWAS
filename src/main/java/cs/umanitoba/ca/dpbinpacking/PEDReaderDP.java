/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.umanitoba.ca.dpbinpacking;

import static cs.umanitoba.ca.dpbinpacking.GWASDP.checkNoise;
import static cs.umanitoba.ca.dpbinpacking.GWASDP.getAlgo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author azizmma
 */
public class PEDReaderDP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("sample.ped"));
//        List<Data> data = new ArrayList<>();
        String main_text = "", text = "";
//        double epsilon_global_max = .25, epsilon_global_min = .05;
//        double epsilon_global_max = 3, epsilon_global_min = .5;//class a
//        double epsilon_global_max = 1, epsilon_global_min = .2;//class b
        double epsilon_global_max = .25, epsilon_global_min = .05;//class c
        String filename = "sampleC.ped";
        ArrayList<DPAlgorithms> algos = GWASDP.init_DPAlgos(epsilon_global_max, epsilon_global_min);
//        DPAlgorithms current_algo = GWASDP.getAlgo(new Random(), algos, true);
        String[] original = new String[]{"13", "13", "41", "42", "21", "31", "42", "32", "43", "24", "24", "21", "12", "13", "42", "32", "13", "32", "41", "43"};
        //make child come last
        int perturbed = 0;
        double loss = 0.0;
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] split = line.split("\t");
            for (int j = 0; j < 6; j++) {
                main_text += split[j] + "\t";
            }

            boolean already_added = false;
            for (int j = 6; j < split.length; j++) {
//                System.out.print(split[j] + "  " + original[j - 6] + ",");
                if (!split[j].contains("0")) {
                    String[] snps = split[j].split("\\s+");
                    String[] DPsnps = new String[2];
                    for (int i = 0; i < snps.length; i++) {

                        DPAlgorithms current_algo = getAlgo(new Random(), algos, true);
                        double threshold = 1 / (1 + Math.exp(current_algo.current_epsilon));
                        double val = new Random().nextDouble();
                        if (val < threshold && !already_added & loss<2) {
                            if (snps[i].equals(original[j - 6].toCharArray()[0])) {
                                DPsnps[i] = original[j - 6].toCharArray()[1] + "";
                            } else {
                                DPsnps[i] = original[j - 6].toCharArray()[0] + "";
                            }
                            perturbed++;
                            already_added = true;
                            loss += current_algo.getCurrent_epsilon();
                            checkNoise(current_algo, algos);

                        } else {
                            DPsnps[i] = snps[i] + "";
                        }
                    }
                    main_text += DPsnps[0] + "  " + DPsnps[1];
                } else {
                    main_text += split[j];
                }
                if (j < split.length - 1) {
                    main_text += "\t";
                }
            }
            main_text += "\r\n";

        }
        System.out.println(perturbed + " loss " + loss);
        PrintWriter pw = new PrintWriter(new File(filename));
        pw.write(main_text);
        pw.close();
        pw.flush();

    }
}
