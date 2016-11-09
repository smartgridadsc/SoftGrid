package it.illinois.adsc.ema.control;

/**
 * Created by prageethmahendra on 2/3/2016.
 */
public class ExperimentCB {

    public static void main(String[] args) {
        String[] params = {"-f","C:\\EMA\\Demo\\smartpower\\SmartPower\\config.properties","IED"};

        SmartPowerControler.initiate(params);
        params[2] = "PRX";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SmartPowerControler.initiate(params);
        params[2] = "ATK";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SmartPowerControler.initiate(params);
    }
}
