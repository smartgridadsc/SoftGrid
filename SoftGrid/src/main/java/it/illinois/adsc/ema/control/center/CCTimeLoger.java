package it.illinois.adsc.ema.control.center;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by prageethmahendra on 3/2/2016.
 */
public class CCTimeLoger {
    private static volatile long startTime = System.currentTimeMillis();
    private static volatile String currentCommand = "";
    private static BufferedWriter bufferedWriter = null;

    static {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File("CC_Res_Time.csv")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logDuration(String prfix) {
        try {
            bufferedWriter.newLine();
            if(prfix != null)
            {
                bufferedWriter.write(currentCommand + " : " + prfix + ": ");
            }
            bufferedWriter.write(String.valueOf(System.currentTimeMillis() - startTime));
            bufferedWriter.flush();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetStartTime(String command) {
        try {
            bufferedWriter.newLine();
            bufferedWriter.write("Request Send..." + command);
            bufferedWriter.flush();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentCommand = command;
        startTime = System.currentTimeMillis();
    }
}
