package it.illinois.adsc.ema.control.proxy.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by prageethmahendra on 3/2/2016.
 */
public class ProxyTimeLoger {
    private static long startTime = System.currentTimeMillis();
    private static BufferedWriter bufferedWriter = null;

    static {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File("Proxy_Res_Time.csv")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logDuration(String prfix) {
        try {
            bufferedWriter.newLine();
            if(prfix != null)
            {
                bufferedWriter.write(prfix + ": ");
            }
            bufferedWriter.write(String.valueOf(System.currentTimeMillis() - startTime));
            bufferedWriter.flush();
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetStartTime() {
        startTime = System.currentTimeMillis();
    }
}
