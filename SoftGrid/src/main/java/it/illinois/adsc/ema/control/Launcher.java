package it.illinois.adsc.ema.control;

/**
 * Created by SmartPower on 20/5/2016.
 */
public class Launcher {
    public static void main(String[] args) {
        System.out.println(" I'm live...!");
        for (String arg : args) {
            System.out.println("arg = " + arg);
        }
        try {
            SmartPowerControler.initiate(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
