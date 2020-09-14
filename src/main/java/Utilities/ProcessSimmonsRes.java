/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author azizmma
 */
public class ProcessSimmonsRes {

    /**
     * @param args the command line arguments
     */
    final static int TOP = 10;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("SimmonsLMM.txt"));
        int i = 0;
        String[][] res = new String[TOP][10];
        List<String> original = new ArrayList<String>();
        while (sc.hasNext()) {
            String[] next = sc.nextLine().split("\t");
            if (i == 0) {
                i++;
                
                continue;
            }else if(i==TOP+1)
                break;
            for (int j = 0; j < next.length; j++) {
                res[i-1][j] = next[j];
                if (j==0)
                    original.add(next[j]);
            }
            i++;
            
        }
        for (int j = 1; j < res[0].length; j++) {
            int count = 0;
            for (int k = 0; k < res.length; k++) {
                if (original.contains( res[k][j]))
                    count++;
            }
            System.out.println(count);
            
        }
    }

}
