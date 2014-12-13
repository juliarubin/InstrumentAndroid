package com.android.drop.features.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	public static void runSystemCommand(String cmd) {
		 
        String s = null;
 
        try {
        	System.out.println("Running " + cmd);
            Process p = Runtime.getRuntime().exec(cmd);
             
            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
 
            // read the output from the command
            System.out.println("Standard output:");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
             
            // read any errors from the attempted command
            System.out.println("Standard error:");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }     
        }
        catch (IOException e) {
            System.out.println("exception happened:");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
