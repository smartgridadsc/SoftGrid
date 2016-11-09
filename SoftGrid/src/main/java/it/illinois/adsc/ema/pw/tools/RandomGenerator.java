package it.illinois.adsc.ema.pw.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by prageeth mahendra on 12/4/2016.
 */
public class RandomGenerator {
    public static final int LINEAR = 1;
    public static final int NU_LAST = 2;
    public static final int NU_FIRST = 3;
    public static final int NU_MIDLE = 4;
    public static final int FIXED_5 = 5;
    public static final int FIXED_5_30P = 6;

    public static double getRandomNumber(int type) {
        switch (type) {
            case LINEAR:
                return getLinearRandomNumber();
            case NU_FIRST:
                return getNuFirstRandom(1000);
            case NU_LAST:
                return getNuLastRandom(1000);
            case NU_MIDLE:
                return getNuMiddleRandom();
            case FIXED_5:
                return getFixed5();
            case FIXED_5_30P:
                return getFixed5_30P();
            default:
        }
        return 10.3;
    }

    private static double getFixed5_30P()  {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() < 0.3 ? 0.0 : 1.0;
    }
    private static double getFixed5() {
        Random random = new Random(System.nanoTime());
        return random.nextDouble() < 0.5 ? 0.0 : 1.0;
    }

    public static double getLinearRandomNumber() {
        Random random = new Random(System.nanoTime());
        return random.nextDouble();
    }

    public static double getNuFirstRandom(int maxSize) {
        //Get a linearly multiplied random number
        return 1- (getNuLastRandom(maxSize));
    }

    public static double getNuLastRandom(int maxSize) {
            //Get a linearly multiplied random number
            int randomMultiplier = maxSize * (maxSize + 1) / 2;
            Random r=new Random();
            int randomInt = r.nextInt(randomMultiplier);

            //Linearly iterate through the possible values to find the correct one
            int linearRandomNumber = 0;
            for(int i=maxSize; randomInt >= 0; i--){
                randomInt -= i;
                linearRandomNumber++;
            }
            double divider = Math.pow(10,String.valueOf(linearRandomNumber).length());

            return linearRandomNumber/divider;
    }

    public static double getNuMiddleRandom() {
        Random random = new Random(System.nanoTime());
        return random.nextDouble();
    }

    public static void main(String[] args) {
        File randomNumbers = new File("RandomNumbers.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(randomNumbers));
            for (int i = 0; i < 1000; i++)
                writer.write(String.valueOf(RandomGenerator.getRandomNumber(NU_FIRST)) + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
