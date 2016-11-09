package it.illinois.adsc.ema.pw.tools;

import java.util.Random;

/**
 * Created by prageethmahendra on 30/3/2016.
 */ //"Branch '1' '31' '1'" "OPEN BOTH" "CHECK" ""
public class TransientCommand implements Comparable {
    //Double.MAX_VALUE;
    public static boolean RANDOM_DELAY = false;
    public static boolean COUNTBASED = false;
    public static boolean MITIGATION = false;

    public static double CALC_MITIGATION_DURATION = 0;
    private static Random random = new Random(System.nanoTime());
    private String contingencyName = "\"OPEN_ALL\"";
    private double timeDelay = 1.0000;
    private double mitigationDuration = PWV18_TimeDelayAnalysis.MITIGATION_DURATION;
    private double networkDelay;
    private String objectType = "\"Branch";
    private String keyValues; // eg from_bus to_bus circuiteLine
    private String command = "\"OPEN BOTH\"";
    private String reverseCommand = "\"CLOSE BOTH\"";
    private String other = "\"CHECK\" \"\"";
    private static double minMitiDuration = Double.MAX_VALUE;
    private static double maxMitiDuration = Double.MIN_VALUE;

    private String toString;

    public TransientCommand(String key, String type) {
        this.keyValues = key;
        objectType = "\"" + type;
    }

    @Override
    public String toString() {
//        this.mitigationDuration = COUNTBASED ? CALC_MITIGATION_DURATION : PWV18_TimeDelayAnalysis.MITIGATION_DURATION  + ((random.nextFloat() * 10.0)/1800.0);
        // set the fixed part of the mitigation duration
        this.mitigationDuration = (COUNTBASED ? CALC_MITIGATION_DURATION : PWV18_TimeDelayAnalysis.MITIGATION_DURATION);
        // add the random part of the mitigation duration
        this.mitigationDuration = 1 + this.mitigationDuration + this.mitigationDuration * (Configuration.PT_Percent / 100.00) * ((random.nextFloat() * 1000.0) / 1000.0);
        // calculate the random part of the network delay
        this.networkDelay = PWV18_TimeDelayAnalysis.NETWORK_DELAY;
        this.networkDelay = this.networkDelay + this.networkDelay * (Configuration.PN_Percent / 100.00) * ((random.nextFloat() * 1000.0) / 1000.0);
        // add the network delay to the mitigation duration
        this.mitigationDuration = this.mitigationDuration + networkDelay;
        if (!MITIGATION || mitigationDuration > timeDelay) {
            //System.out.println(PWV18_TimeDelayAnalysis.MITIGATION_DURATION +" , "+ timeDelay);
            toString = contingencyName + " " +
                    String.valueOf(timeDelay) +
                    objectType + " " +
                    keyValues + " " +
                    command + " " +
                    other;
//            toString = toString + "\n" + contingencyName + " " +
//                    String.valueOf(timeDelay + 1.1) +
//                    objectType + " " +
//                    keyValues + " " +
//                    reverseCommand + " " +
//                    other;
        } else {
//            System.out.println("mitigated");
            System.out.println("mitigationDuration = " + mitigationDuration + " & " + "timeDelay =" + timeDelay);
            toString = "";
        }
        System.out.println("Failed = " + timeDelay + " = " + mitigationDuration);
        minMitiDuration = Math.min(mitigationDuration, minMitiDuration);
        maxMitiDuration = Math.max(mitigationDuration, maxMitiDuration);
        System.out.println("minMitiDuration = " + minMitiDuration + " maxMitiDuration = " + maxMitiDuration);
//      System.out.println("toString = " + toString);
//        System.out.println("toString = " + toString + "mitigationDuration = " + mitigationDuration);
        return toString;
    }

    public TransientCommand() {
    }

    public TransientCommand(String keyValues) {
        this.keyValues = keyValues;
    }

    public String getContingencyName() {
        return contingencyName;
    }

    public void setContingencyName(String contingencyName) {
        this.contingencyName = contingencyName;
    }

    public double getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(double timeDelay) {
        this.timeDelay = timeDelay;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getKeyValues() {
        return keyValues;
    }

    public void setKeyValues(String keyValues) {
        this.keyValues = keyValues;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public void handleTimeLimits() {
        if (RANDOM_DELAY) {
            timeDelay = Math.round(PWV18_TimeDelayAnalysis.DelayUpperBound * RandomGenerator.getRandomNumber(PWV18_TimeDelayAnalysis.RANDOM_FUNCTION) * 10000.00) / 10000.00 + 1;
            timeDelay = Math.round(timeDelay * 10000.0) / 10000.0;
            if (PWV18_TimeDelayAnalysis.DelaySelectThreshold > 0) {
                if ((timeDelay - 1) < PWV18_TimeDelayAnalysis.DelaySelectThreshold) {
                    timeDelay = 1;
                } else {
                    timeDelay = 1 + PWV18_TimeDelayAnalysis.DelayUpperBound;
                }
            }
        }

    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TransientCommand) {
            TransientCommand transientCommand = (TransientCommand) o;
            return this.timeDelay == transientCommand.getTimeDelay() ? 0 : (this.timeDelay > transientCommand.getTimeDelay() ? 1 : -1);
        }
        return -1;
    }
}
